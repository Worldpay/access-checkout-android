package com.worldpay.access.checkout

import android.content.Context
import com.worldpay.access.checkout.config.CardConfigurationParser
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.validation.*
import com.worldpay.access.checkout.views.*
import java.util.*

/**
 * [AccessCheckoutCard] is responsible for responding to state changes on a Card field, by returning validation results
 * back to the [CardListener]
 *
 * @param context The android [Context] object
 * @param panView The reference to the pan field which needs to implement [CardTextView]
 * @param cvvView The reference to the cvv field which needs to implement [CardTextView]
 * @param dateView The reference to the date field which needs to implement [CardDateView]
 * @param factory (Optional) The object which is responsible for constructing dependencies for a [Card]. The default is [AccessCheckoutCardDefaultFactory]
 * @param cardConfiguration (Optional) The card configuration contains all the validation rules for the card fields. The default will be constructed by the [AccessCheckoutCardDefaultFactory]
 * @param cardValidator (Optional) The class responsible for validating the state of the card fields. The default will be constructed by the [AccessCheckoutCardDefaultFactory]
 * @param panLengthFilter (Optional) The class responsible for restricting the length of input into the pan field. The default will be constructed by the [AccessCheckoutCardDefaultFactory]
 * @param cvvLengthFilter (Optional) The class responsible for restricting the length of input into the cvv field. The default will be constructed by the [AccessCheckoutCardDefaultFactory]
 * @param dateLengthFilter (Optional) The class responsible for restricting the length of input into the date field. The default will be constructed by the [AccessCheckoutCardDefaultFactory]
 * @constructor Constructs an instance of [AccessCheckoutCard]
 */
class AccessCheckoutCard @JvmOverloads constructor(
    context: Context,
    private val panView: CardTextView,
    private val cvvView: CardTextView,
    private val dateView: CardDateView,
    private val factory: CardFactory = AccessCheckoutCardDefaultFactory(context),
    private val cardConfiguration: CardConfiguration = factory.getCardConfiguration(),
    override var cardValidator: CardValidator? = factory.getCardValidator(cardConfiguration),
    private val panLengthFilter: PANLengthFilter? = factory.getPANLengthFilter(cardValidator, cardConfiguration),
    private val cvvLengthFilter: CVVLengthFilter? = factory.getCVVLengthFilter(
        cardValidator,
        cardConfiguration,
        panView
    ),
    private val dateLengthFilter: DateLengthFilter = factory.getDateLengthFilter(cardConfiguration)
) : Card {

    override var cardListener: CardListener? = null

    override fun isValid(): Boolean {
        val pan = panView.getInsertedText()
        val cvv = cvvView.getInsertedText()
        val month = dateView.getInsertedMonth()
        val year = dateView.getInsertedYear()

        return cardValidator?.let {
            val panResult = it.validatePAN(pan)
            val (panValidationResult) = panResult
            val cvvResult = it.validateCVV(cvv, pan)
            val (cvvValidationResult) = cvvResult
            val dateValidationResult = it.validateDate(month, year)
            panValidationResult.complete && cvvValidationResult.complete && dateValidationResult.complete
        } ?: true
    }

    override fun onUpdatePAN(pan: String) {
        cardValidator?.let {
            val (panValidationResult, panCardBrand) = it.validatePAN(pan)

            cardListener?.onUpdate(panView, panValidationResult.partial || panValidationResult.complete)
            cardListener?.onUpdateCardBrand(panCardBrand)
            panLengthFilter?.let { cardListener?.onUpdateLengthFilter(panView, panLengthFilter) }

            val (cvvValidationResult) = it.validateCVV(cvvView.getInsertedText(), pan)
            cardListener?.onUpdate(cvvView, cvvValidationResult.complete)
        }
    }

    override fun onEndUpdatePAN(pan: String) {
        cardValidator?.let {
            val (panValidationResult, cardBrand) = it.validatePAN(pan)

            cardListener?.onUpdate(panView, panValidationResult.complete)
            cardListener?.onUpdateCardBrand(cardBrand)

            val (cvvValidationResult) = it.validateCVV(cvvView.getInsertedText(), pan)
            cardListener?.onUpdate(cvvView, cvvValidationResult.complete)
        }
    }

    override fun onUpdateCVV(cvv: String) {
        cardValidator?.let {
            val (cvvValidationResult) = it.validateCVV(cvv, panView.getInsertedText())

            cardListener?.onUpdate(cvvView, cvvValidationResult.partial || cvvValidationResult.complete)
            cvvLengthFilter?.let { cardListener?.onUpdateLengthFilter(cvvView, cvvLengthFilter) }
        }
    }

    override fun onEndUpdateCVV(cvv: String) {
        cardValidator?.let {
            val (cvvValidationResult) = it.validateCVV(cvv, panView.getInsertedText())

            cardListener?.onUpdate(cvvView, cvvValidationResult.complete)
        }
    }

    override fun onUpdateDate(month: String?, year: String?) {
        cardValidator?.let { validator ->
            cardListener?.let { listener ->
                val monthField = month ?: dateView.getInsertedMonth()
                val yearField = year ?: dateView.getInsertedYear()

                listener.onUpdateLengthFilter(dateView, dateLengthFilter)

                if (!validator.canUpdate(monthField, yearField)) {
                    val validationResult = validator.validateDate(monthField, yearField)
                    listener.onUpdate(dateView, validationResult.complete)
                } else {
                    month?.let {
                      val monthValidationResult = validator.validateDate(it, null)
                      listener.onUpdate(dateView, monthValidationResult.partial || monthValidationResult.complete)
                    }
                    year?.let {
                      val yearValidationResult = validator.validateDate(null, it)
                      listener.onUpdate(dateView, yearValidationResult.partial || yearValidationResult.complete)
                    }
                }
            }
        }
    }

    override fun onEndUpdateDate(month: String?, year: String?) {
        cardValidator?.let {
            val validationResult =
                it.validateDate(month ?: dateView.getInsertedMonth(), year ?: dateView.getInsertedYear())
            cardListener?.onUpdate(dateView, validationResult.complete)
        }
    }

}

/**
 * [CardFactory] is responsible for constructing the dependencies needed for a [Card]
 */
interface CardFactory {

    /**
     * Creates a [CardValidator] instance
     * @param cardConfiguration the card configuration to use
     * @return [CardValidator] for validating the inputs of a card
     */
    fun getCardValidator(cardConfiguration: CardConfiguration): CardValidator

    /**
     * Creates a [CardConfiguration] instance
     * @return [CardConfiguration] for holding configurations for validating a card
     */
    fun getCardConfiguration(): CardConfiguration

    /**
     * Creates a [PANLengthFilter] instance
     * @param cardValidator (Optional) the card validator to use
     * @param cardConfiguration the card configuration to use
     * @return [PANLengthFilter] (Optional) for restricting the inputs of a pan field
     */
    fun getPANLengthFilter(cardValidator: CardValidator?, cardConfiguration: CardConfiguration): PANLengthFilter?

    /**
     * Creates a [CVVLengthFilter] instance
     * @param cardValidator (Optional) the card validator to use
     * @param cardConfiguration the card configuration to use
     * @param panView the pan field so that the cvv length can be validated against the current pan
     * @return [CVVLengthFilter] (Optional) for restricting the inputs of a cvv field
     */
    fun getCVVLengthFilter(
        cardValidator: CardValidator?,
        cardConfiguration: CardConfiguration,
        panView: CardTextView
    ): CVVLengthFilter?

    /**
     * Creates a [DateLengthFilter] instance
     * @param cardConfiguration the card configuration to use
     * @return [DateLengthFilter] for restricting the inputs of a date field
     */
    fun getDateLengthFilter(cardConfiguration: CardConfiguration): DateLengthFilter
}

/**
 * [AccessCheckoutCardDefaultFactory] is an implementation of [CardFactory] which is to be used by [AccessCheckoutCard]
 * @param context The android [Context] object
 */
class AccessCheckoutCardDefaultFactory(private val context: Context) : CardFactory {

    override fun getCardValidator(cardConfiguration: CardConfiguration): AccessCheckoutCardValidator {
        return AccessCheckoutCardValidator(
            getPANValidator(cardConfiguration),
            getCVVValidator(cardConfiguration),
            getExpiryDateValidator(cardConfiguration)
        )
    }

    override fun getCardConfiguration(): CardConfiguration {
        val cardConfiguration = context.resources.openRawResource(R.raw.card_configuration)
        return CardConfigurationParser().parse(cardConfiguration)
    }

    override fun getPANLengthFilter(
        cardValidator: CardValidator?,
        cardConfiguration: CardConfiguration
    ): PANLengthFilter? =
        cardValidator?.let { PANLengthFilter(it, cardConfiguration) }

    override fun getCVVLengthFilter(
        cardValidator: CardValidator?,
        cardConfiguration: CardConfiguration,
        panView: CardTextView
    ): CVVLengthFilter? =
        cardValidator?.let { CVVLengthFilter(it, cardConfiguration, panView) }

    override fun getDateLengthFilter(cardConfiguration: CardConfiguration): DateLengthFilter = DateLengthFilter(cardConfiguration)

    private fun getPANValidator(cardConfiguration: CardConfiguration) = PANValidatorImpl(cardConfiguration)
    private fun getCVVValidator(cardConfiguration: CardConfiguration) = CVVValidatorImpl(cardConfiguration)
    private fun getExpiryDateValidator(cardConfiguration: CardConfiguration) =
        DateValidatorImpl(Calendar.getInstance(), cardConfiguration)

}


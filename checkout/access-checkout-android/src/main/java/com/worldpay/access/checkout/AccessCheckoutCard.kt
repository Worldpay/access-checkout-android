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
 * @param panView The reference to the pan field which needs to implement [CardView]
 * @param cvvView The reference to the cvv field which needs to implement [CardView]
 * @param dateView The reference to the date field which needs to implement [DateCardView]
 * @param factory (Optional) The object which is responsible for constructing dependencies for a [Card]. The default is [AccessCheckoutCardDefaultFactory]
 * @param cardConfiguration (Optional) The card configuration contains all the validation rules for the card fields. The default will be constructed by the [AccessCheckoutCardDefaultFactory]
 * @param cardValidator (Optional) The class responsible for validating the state of the card fields. The default will be constructed by the [AccessCheckoutCardDefaultFactory]
 * @param panLengthFilter (Optional) The class responsible for restricting the length of input into the pan field. The default will be constructed by the [AccessCheckoutCardDefaultFactory]
 * @param cvvLengthFilter (Optional) The class responsible for restricting the length of input into the cvv field. The default will be constructed by the [AccessCheckoutCardDefaultFactory]
 * @param monthLengthFilter (Optional) The class responsible for restricting the length of input into the month field. The default will be constructed by the [AccessCheckoutCardDefaultFactory]
 * @param yearLengthFilter (Optional) The class responsible for restricting the length of input into the year field. The default will be constructed by the [AccessCheckoutCardDefaultFactory]
 * @constructor Constructs an instance of [AccessCheckoutCard]
 */
class AccessCheckoutCard @JvmOverloads constructor(
    context: Context,
    private val panView: CardView,
    private val cvvView: CardView,
    private val dateView: DateCardView,
    private val factory: CardFactory = AccessCheckoutCardDefaultFactory(context),
    private val cardConfiguration: CardConfiguration = factory.getCardConfiguration(),
    override var cardValidator: CardValidator? = factory.getCardValidator(cardConfiguration),
    private val panLengthFilter: PANLengthFilter? = factory.getPANLengthFilter(cardValidator, cardConfiguration),
    private val cvvLengthFilter: CVVLengthFilter? = factory.getCVVLengthFilter(
        cardValidator,
        cardConfiguration,
        panView
    ),
    private val monthLengthFilter: MonthLengthFilter = factory.getMonthLengthFilter(cardConfiguration),
    private val yearLengthFilter: YearLengthFilter = factory.getYearLengthFilter(cardConfiguration)
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
            cardListener?.onPANUpdateValidationResult(panValidationResult, panCardBrand, panLengthFilter)

            val (cvvValidationResult) = it.validateCVV(cvvView.getInsertedText(), pan)
            cardListener?.onCVVEndUpdateValidationResult(cvvValidationResult)

            cardListener?.onValidationResult(ValidationResult(false, isValid()))
        }
    }

    override fun onEndUpdatePAN(pan: String) {
        cardValidator?.let {
            val (panValidationResult, cardBrand) = it.validatePAN(pan)
            cardListener?.onPANEndUpdateValidationResult(panValidationResult, cardBrand)

            val (cvvValidationResult) = it.validateCVV(cvvView.getInsertedText(), pan)
            cardListener?.onCVVEndUpdateValidationResult(cvvValidationResult)

            cardListener?.onValidationResult(ValidationResult(false, isValid()))
        }
    }

    override fun onUpdateCVV(cvv: String) {
        cardValidator?.let {
            val (cvvValidationResult) = it.validateCVV(cvv, panView.getInsertedText())
            cardListener?.onCVVUpdateValidationResult(cvvValidationResult, cvvLengthFilter)

            cardListener?.onValidationResult(ValidationResult(false, isValid()))
        }
    }

    override fun onEndUpdateCVV(cvv: String) {
        cardValidator?.let {
            val (cvvValidationResult) = it.validateCVV(cvv, panView.getInsertedText())
            cardListener?.onCVVEndUpdateValidationResult(cvvValidationResult)

            cardListener?.onValidationResult(ValidationResult(false, isValid()))

        }
    }

    override fun onUpdateDate(month: String?, year: String?) {
        cardValidator?.let { validator ->
            cardListener?.let { listener ->
                val monthField = month ?: dateView.getInsertedMonth()
                val yearField = year ?: dateView.getInsertedYear()

                var monthValidationResult: ValidationResult?
                var yearValidationResult: ValidationResult?
                month.let {
                    monthValidationResult = validator.validateDate(it, null)
                }
                year.let {
                    yearValidationResult = validator.validateDate(null, it)
                }

                if (!validator.canUpdate(monthField, yearField)) {
                    val validationResult = validator.validateDate(monthField, yearField)
                    listener.onDateUpdateValidationResult(
                        validationResult,
                        validationResult,
                        monthLengthFilter,
                        yearLengthFilter
                    )
                } else {
                    listener.onDateUpdateValidationResult(
                        monthValidationResult,
                        yearValidationResult,
                        monthLengthFilter,
                        yearLengthFilter
                    )
                }

                listener.onValidationResult(ValidationResult(false, isValid()))
            }
        }
    }

    override fun onEndUpdateDate(month: String?, year: String?) {
        cardValidator?.let {
            val validationResult =
                it.validateDate(month ?: dateView.getInsertedMonth(), year ?: dateView.getInsertedYear())
            cardListener?.onDateEndUpdateValidationResult(validationResult)

            cardListener?.onValidationResult(ValidationResult(false, isValid()))
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
     * @param (Optional)[cardValidator] the card validator to use
     * @param cardConfiguration the card configuration to use
     * @return (Optional)[PANLengthFilter] for restricting the inputs of a pan field
     */
    fun getPANLengthFilter(cardValidator: CardValidator?, cardConfiguration: CardConfiguration): PANLengthFilter?

    /**
     * Creates a [CVVLengthFilter] instance
     * @param (Optional)[cardValidator] the card validator to use
     * @param cardConfiguration the card configuration to use
     * @param panView the pan field so that the cvv length can be validated against the current pan
     * @return (Optional)[CVVLengthFilter] for restricting the inputs of a cvv field
     */
    fun getCVVLengthFilter(
        cardValidator: CardValidator?,
        cardConfiguration: CardConfiguration,
        panView: CardView
    ): CVVLengthFilter?

    /**
     * Creates a [MonthLengthFilter] instance
     * @param cardConfiguration the card configuration to use
     * @return [MonthLengthFilter] for restricting the inputs of a month field
     */
    fun getMonthLengthFilter(cardConfiguration: CardConfiguration): MonthLengthFilter

    /**
     * Creates a [YearLengthFilter] instance
     * @param cardConfiguration the card configuration to use
     * @return [YearLengthFilter] for restricting the inputs of a year field
     */
    fun getYearLengthFilter(cardConfiguration: CardConfiguration): YearLengthFilter
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
        panView: CardView
    ): CVVLengthFilter? =
        cardValidator?.let { CVVLengthFilter(it, cardConfiguration, panView) }

    override fun getMonthLengthFilter(cardConfiguration: CardConfiguration): MonthLengthFilter =
        MonthLengthFilter(cardConfiguration)

    override fun getYearLengthFilter(cardConfiguration: CardConfiguration): YearLengthFilter =
        YearLengthFilter(cardConfiguration)

    private fun getPANValidator(cardConfiguration: CardConfiguration) = PANValidatorImpl(cardConfiguration)
    private fun getCVVValidator(cardConfiguration: CardConfiguration) = CVVValidatorImpl(cardConfiguration)
    private fun getExpiryDateValidator(cardConfiguration: CardConfiguration) =
        DateValidatorImpl(Calendar.getInstance(), cardConfiguration)

}


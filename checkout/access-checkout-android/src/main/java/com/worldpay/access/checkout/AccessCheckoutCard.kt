package com.worldpay.access.checkout

import android.content.Context
import com.worldpay.access.checkout.config.CardConfigurationParser
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.validation.*
import com.worldpay.access.checkout.views.*
import java.util.*

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

interface CardFactory {

    fun getCardValidator(cardConfiguration: CardConfiguration): CardValidator
    fun getCardConfiguration(): CardConfiguration
    fun getPANLengthFilter(cardValidator: CardValidator?, cardConfiguration: CardConfiguration): PANLengthFilter?
    fun getCVVLengthFilter(
        cardValidator: CardValidator?,
        cardConfiguration: CardConfiguration,
        panView: CardTextView
    ): CVVLengthFilter?


    fun getDateLengthFilter(cardConfiguration: CardConfiguration): DateLengthFilter
}

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

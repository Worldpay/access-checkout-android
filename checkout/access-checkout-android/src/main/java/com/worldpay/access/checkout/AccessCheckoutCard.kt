package com.worldpay.access.checkout

import android.content.Context
import com.worldpay.access.checkout.config.CardConfigurationParser
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.validation.*
import com.worldpay.access.checkout.views.*
import com.worldpay.access.checkout.views.PANLengthFilter
import java.util.*

class AccessCheckoutCard(
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
                    listener.onDateUpdateValidationResult(validationResult, validationResult, monthLengthFilter, yearLengthFilter)
                } else {
                    listener.onDateUpdateValidationResult(monthValidationResult, yearValidationResult, monthLengthFilter, yearLengthFilter)
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

interface CardFactory {

    fun getCardValidator(cardConfiguration: CardConfiguration): CardValidator
    fun getCardConfiguration(): CardConfiguration
    fun getPANLengthFilter(cardValidator: CardValidator?, cardConfiguration: CardConfiguration): PANLengthFilter?
    fun getCVVLengthFilter(
        cardValidator: CardValidator?,
        cardConfiguration: CardConfiguration,
        panView: CardView
    ): CVVLengthFilter?

    fun getMonthLengthFilter(cardConfiguration: CardConfiguration): MonthLengthFilter
    fun getYearLengthFilter(cardConfiguration: CardConfiguration): YearLengthFilter
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

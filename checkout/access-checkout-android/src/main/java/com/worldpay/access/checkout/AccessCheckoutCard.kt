package com.worldpay.access.checkout

import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.validation.CardValidator
import com.worldpay.access.checkout.views.*

/**
 * [AccessCheckoutCard] is responsible for responding to state changes on a Card field, by returning validation results
 * back to the [CardListener]
 *
 * @param panView The reference to the pan field which needs to implement [CardTextView]
 * @param cvvView The reference to the cvv field which needs to implement [CardTextView]
 * @param dateView The reference to the date field which needs to implement [CardDateView]
 * @param factory (Optional) The object which is responsible for constructing dependencies for a [Card]. The default is [AccessCheckoutCardDefaultFactory]
 * @constructor Constructs an instance of [AccessCheckoutCard]
 */
class AccessCheckoutCard @JvmOverloads constructor(
    private val panView: CardTextView,
    private val cvvView: CardTextView,
    private val dateView: CardDateView,
    private val factory: CardFactory = AccessCheckoutCardDefaultFactory()
) : Card {

    private var panLengthFilter: PANLengthFilter? = null
    private var cvvLengthFilter: CVVLengthFilter? = null
    private var dateLengthFilter: DateLengthFilter? = null

    override var cardListener: CardListener? = null
    override var cardValidator: CardValidator? = null
        set(value) {
            field = value
            panLengthFilter = factory.getPANLengthFilter(value)
            cvvLengthFilter = factory.getCVVLengthFilter(value, panView)
            dateLengthFilter = factory.getDateLengthFilter(value?.cardConfiguration)

            panLengthFilter?.let { cardListener?.onUpdateLengthFilter(panView, it) }
            cvvLengthFilter?.let { cardListener?.onUpdateLengthFilter(cvvView, it) }
            dateLengthFilter?.let { cardListener?.onUpdateLengthFilter(dateView, it) }

            value?.let { revalidate(it) }
        }

    private fun revalidate(cardValidator: CardValidator) {
        if (panView.hasFocus()) {
            validatePartialPAN(cardValidator, panView.getInsertedText())
        } else {
            validateCompletePAN(cardValidator, panView.getInsertedText())
        }

        if (cvvView.hasFocus()) {
            validatePartialCVV(cardValidator, cvvView.getInsertedText())
        } else {
            validateCompleteCVV(cardValidator, cvvView.getInsertedText())
        }

        if (dateView.hasFocus()) {
            validatePartialDate(cardValidator, getNullableField(dateView.getInsertedMonth()), getNullableField(dateView.getInsertedYear()))
        } else {
            validateCompleteDate(cardValidator, dateView.getInsertedMonth(), dateView.getInsertedYear())
        }
    }


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
        cardValidator?.let { validatePartialPAN(it, pan) }
    }

    override fun onEndUpdatePAN(pan: String) {
        cardValidator?.let { validateCompletePAN(it, pan) }
    }

    override fun onUpdateCVV(cvv: String) {
        cardValidator?.let { validatePartialCVV(it, cvv) }
    }

    override fun onEndUpdateCVV(cvv: String) {
        cardValidator?.let {
            validateCompleteCVV(it, cvv)
        }
    }

    override fun onUpdateDate(month: String?, year: String?) {
        cardValidator?.let { validator ->
            validatePartialDate(validator, month, year)
        }
    }

    override fun onEndUpdateDate(month: String?, year: String?) {
        cardValidator?.let {
            validateCompleteDate(it, month, year)
        }
    }

    private fun validatePartialPAN(cardValidator: CardValidator, pan: String) {
        val (panValidationResult, panCardBrand) = cardValidator.validatePAN(pan)

        cardListener?.onUpdate(panView, panValidationResult.partial || panValidationResult.complete)
        cardListener?.onUpdateCardBrand(panCardBrand)
        panLengthFilter?.let { filter -> cardListener?.onUpdateLengthFilter(panView, filter) }

        val (cvvValidationResult) = cardValidator.validateCVV(cvvView.getInsertedText(), pan)
        cardListener?.onUpdate(cvvView, cvvValidationResult.complete)
    }

    private fun validateCompletePAN(cardValidator: CardValidator, pan: String) {
        val (panValidationResult, cardBrand) = cardValidator.validatePAN(pan)

        cardListener?.onUpdate(panView, panValidationResult.complete)
        cardListener?.onUpdateCardBrand(cardBrand)

        val (cvvValidationResult) = cardValidator.validateCVV(cvvView.getInsertedText(), pan)
        cardListener?.onUpdate(cvvView, cvvValidationResult.complete)
    }

    private fun validatePartialCVV(cardValidator: CardValidator, cvv: String) {
        val (cvvValidationResult) = cardValidator.validateCVV(cvv, panView.getInsertedText())

        cardListener?.onUpdate(cvvView, cvvValidationResult.partial || cvvValidationResult.complete)
        cvvLengthFilter?.let { filter -> cardListener?.onUpdateLengthFilter(cvvView, filter) }
    }

    private fun validateCompleteCVV(cardValidator: CardValidator, cvv: String) {
        val (cvvValidationResult) = cardValidator.validateCVV(cvv, panView.getInsertedText())
        cardListener?.onUpdate(cvvView, cvvValidationResult.complete)
    }

    private fun validatePartialDate(cardValidator: CardValidator, month: String?, year: String?) {
        cardListener?.let { listener ->
            val monthField = month ?: dateView.getInsertedMonth()
            val yearField = year ?: dateView.getInsertedYear()

            dateLengthFilter?.let { filter -> listener.onUpdateLengthFilter(dateView, filter) }

            if (!cardValidator.canUpdate(monthField, yearField)) {
                val validationResult = cardValidator.validateDate(monthField, yearField)
                listener.onUpdate(dateView, validationResult.complete)
            } else {
                month?.let {
                    val monthValidationResult = cardValidator.validateDate(it, null)
                    listener.onUpdate(dateView, monthValidationResult.partial || monthValidationResult.complete)
                }
                year?.let {
                    val yearValidationResult = cardValidator.validateDate(null, it)
                    listener.onUpdate(dateView, yearValidationResult.partial || yearValidationResult.complete)
                }
            }
        }
    }

    private fun validateCompleteDate(cardValidator: CardValidator, month: String?, year: String?) {
        val validationResult =
            cardValidator.validateDate(month ?: dateView.getInsertedMonth(), year ?: dateView.getInsertedYear())
        cardListener?.onUpdate(dateView, validationResult.complete)
    }

    private fun getNullableField(field: String): String? {
        return field.ifBlank { null }
    }

}

/**
 * [CardFactory] is responsible for constructing the dependencies needed for a [Card]
 */
interface CardFactory {

    /**
     * Creates a [PANLengthFilter] instance
     * @param cardValidator (Optional) the card validator to use
     * @return [PANLengthFilter] (Optional) for restricting the inputs of a pan field
     */
    fun getPANLengthFilter(cardValidator: CardValidator?): PANLengthFilter?

    /**
     * Creates a [CVVLengthFilter] instance
     * @param cardValidator (Optional) the card validator to use
     * @param panView the pan field so that the cvv length can be validated against the current pan
     * @return [CVVLengthFilter] (Optional) for restricting the inputs of a cvv field
     */
    fun getCVVLengthFilter(
        cardValidator: CardValidator?,
        panView: CardTextView?
    ): CVVLengthFilter?

    /**
     * Creates a [DateLengthFilter] instance
     * @param cardConfiguration the card configuration to use
     * @return [DateLengthFilter] for restricting the inputs of a date field
     */
    fun getDateLengthFilter(cardConfiguration: CardConfiguration?): DateLengthFilter?
}

/**
 * [AccessCheckoutCardDefaultFactory] is an implementation of [CardFactory] which is to be used by [AccessCheckoutCard]
 */
class AccessCheckoutCardDefaultFactory : CardFactory {

    override fun getPANLengthFilter(cardValidator: CardValidator?): PANLengthFilter? {
        return cardValidator?.let { PANLengthFilter(it) }
    }

    override fun getCVVLengthFilter(cardValidator: CardValidator?, panView: CardTextView?): CVVLengthFilter? {
        return cardValidator?.let { CVVLengthFilter(it, panView) }
    }

    override fun getDateLengthFilter(cardConfiguration: CardConfiguration?): DateLengthFilter? {
        return cardConfiguration?.let { DateLengthFilter(it) }
    }

}


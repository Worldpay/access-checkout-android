package com.worldpay.access.checkout

import com.worldpay.access.checkout.validation.CardValidator
import com.worldpay.access.checkout.views.CVVLengthFilter
import com.worldpay.access.checkout.views.CardCVVText

class AccessCheckoutCVV @JvmOverloads constructor(
   private val cvvView: CardCVVText,
   private val factory: CardFactory = AccessCheckoutCardDefaultFactory()
): Card {

    private var cvvLengthFilter: CVVLengthFilter? = null

    override var cardListener: CardListener? = null
    override var cardValidator: CardValidator? = null
        set(value) {
            field = value

            cvvLengthFilter = factory.getCVVLengthFilter(value, null)
            cvvLengthFilter?.let { cardListener?.onUpdateLengthFilter(cvvView, it) }

            value?.let { revalidate(it) }
        }

    private fun revalidate(cardValidator: CardValidator) {
        if (cvvView.hasFocus()) {
            validatePartialCVV(cardValidator, cvvView.getInsertedText())
        } else {
            validateCompleteCVV(cardValidator, cvvView.getInsertedText())
        }
    }

    override fun isValid(): Boolean {
        val cvv = cvvView.getInsertedText()

        return cardValidator?.let {
            val cvvResult = it.validateCVV(cvv, null)
            val (cvvValidationResult) = cvvResult
            cvvValidationResult.complete
        } ?: true
    }

    override fun onUpdatePAN(pan: String) {
    }

    override fun onEndUpdatePAN(pan: String) {
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
    }

    override fun onEndUpdateDate(month: String?, year: String?) {
    }

    private fun validatePartialCVV(cardValidator: CardValidator, cvv: String) {
        val (cvvValidationResult) = cardValidator.validateCVV(cvv, null)

        cardListener?.onUpdate(cvvView, cvvValidationResult.partial || cvvValidationResult.complete)
        cvvLengthFilter?.let { filter -> cardListener?.onUpdateLengthFilter(cvvView, filter) }
    }

    private fun validateCompleteCVV(cardValidator: CardValidator, cvv: String) {
        val (cvvValidationResult) = cardValidator.validateCVV(cvv, null)
        cardListener?.onUpdate(cvvView, cvvValidationResult.complete)
    }

}
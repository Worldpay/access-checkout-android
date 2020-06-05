package com.worldpay.access.checkout.client.validation

import android.widget.EditText
import com.worldpay.access.checkout.util.ValidationUtil.validateNotNull

class CardValidationConfig private constructor(
    val pan: EditText,
    val expiryMonth: EditText,
    val expiryYear: EditText,
    val cvv: EditText,
    val baseUrl: String,
    val validationListener: AccessCheckoutCardValidationSuccessListener
) : ValidationConfig {

    class Builder {

        private var pan: EditText? = null
        private var expiryMonth: EditText? = null
        private var expiryYear: EditText? = null
        private var cvv: EditText? = null
        private var baseUrl: String? = null
        private var validationListener: AccessCheckoutCardValidationSuccessListener? = null

        fun pan(pan: EditText): Builder {
            this.pan = pan
            return this
        }

        fun expiryMonth(expiryMonth: EditText): Builder {
            this.expiryMonth = expiryMonth
            return this
        }

        fun expiryYear(expiryYear: EditText): Builder {
            this.expiryYear = expiryYear
            return this
        }

        fun cvv(cvv: EditText): Builder {
            this.cvv = cvv
            return this
        }

        fun baseUrl(baseUrl: String): Builder {
            this.baseUrl = baseUrl
            return this
        }

        fun validationListener(validationListener: AccessCheckoutCardValidationSuccessListener): Builder {
            this.validationListener = validationListener
            return this
        }

        fun build(): CardValidationConfig {
            validateNotNull(pan, "pan component")
            validateNotNull(expiryMonth, "expiry month component")
            validateNotNull(expiryYear, "expiry year component")
            validateNotNull(cvv, "cvv component")
            validateNotNull(baseUrl, "base url")
            validateNotNull(validationListener, "validation listener")

            return CardValidationConfig(
                pan = pan as EditText,
                expiryMonth = expiryMonth as EditText,
                expiryYear = expiryYear as EditText,
                cvv = cvv as EditText,
                baseUrl = baseUrl as String,
                validationListener = validationListener as AccessCheckoutCardValidationSuccessListener
            )
        }

    }
}
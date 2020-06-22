package com.worldpay.access.checkout.client.validation.config

import android.widget.EditText
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import com.worldpay.access.checkout.util.ValidationUtil.validateNotNull

class CvcValidationConfig private constructor(
    val cvc: EditText,
    val validationListener: AccessCheckoutCvcValidationListener
): ValidationConfig {

    class Builder {

        private var cvc: EditText? = null
        private var validationListener: AccessCheckoutCvcValidationListener? = null

        fun cvc(cvc: EditText): Builder {
            this.cvc = cvc
            return this
        }

        fun validationListener(validationListener: AccessCheckoutCvcValidationListener): Builder {
            this.validationListener = validationListener
            return this
        }

        fun build(): CvcValidationConfig {
            validateNotNull(cvc, "cvc component")
            validateNotNull(validationListener, "validation listener")

            return CvcValidationConfig(
                cvc = cvc as EditText,
                validationListener = validationListener as AccessCheckoutCvcValidationListener
            )
        }

    }
}

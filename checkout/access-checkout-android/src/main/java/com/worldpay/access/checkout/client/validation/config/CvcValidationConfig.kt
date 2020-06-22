package com.worldpay.access.checkout.client.validation.config

import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import com.worldpay.access.checkout.util.ValidationUtil.validateNotNull

class CvcValidationConfig private constructor(
    val cvc: EditText,
    val validationListener: AccessCheckoutCvcValidationListener,
    val lifecycleOwner : LifecycleOwner
): ValidationConfig {

    class Builder {

        private var cvc: EditText? = null
        private var validationListener: AccessCheckoutCvcValidationListener? = null
        private var lifecycleOwner: LifecycleOwner? = null

        fun cvc(cvc: EditText): Builder {
            this.cvc = cvc
            return this
        }

        fun validationListener(validationListener: AccessCheckoutCvcValidationListener): Builder {
            this.validationListener = validationListener
            return this
        }

        fun lifecycleOwner(lifecycleOwner : LifecycleOwner) : Builder {
            this.lifecycleOwner = lifecycleOwner
            return this
        }

        fun build(): CvcValidationConfig {
            validateNotNull(cvc, "cvc component")
            validateNotNull(validationListener, "validation listener")
            validateNotNull(lifecycleOwner, "lifecycle owner")

            return CvcValidationConfig(
                cvc = cvc as EditText,
                validationListener = validationListener as AccessCheckoutCvcValidationListener,
                lifecycleOwner = lifecycleOwner as LifecycleOwner
            )
        }

    }
}

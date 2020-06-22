package com.worldpay.access.checkout.client.validation.config

import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.util.ValidationUtil.validateNotNull

class CardValidationConfig private constructor(
    val pan: EditText,
    val expiryDate: EditText,
    val cvc: EditText,
    val baseUrl: String,
    val validationListener: AccessCheckoutCardValidationListener,
    val lifecycleOwner : LifecycleOwner
) : ValidationConfig {

    class Builder {

        private var pan: EditText? = null
        private var expiryDate: EditText? = null
        private var cvc: EditText? = null
        private var baseUrl: String? = null
        private var validationListener: AccessCheckoutCardValidationListener? = null
        private var lifecycleOwner: LifecycleOwner? = null

        fun pan(pan: EditText): Builder {
            this.pan = pan
            return this
        }

        fun expiryDate(expiryDate: EditText): Builder {
            this.expiryDate = expiryDate
            return this
        }

        fun cvc(cvc: EditText): Builder {
            this.cvc = cvc
            return this
        }

        fun baseUrl(baseUrl: String): Builder {
            this.baseUrl = baseUrl
            return this
        }

        fun validationListener(validationListener: AccessCheckoutCardValidationListener): Builder {
            this.validationListener = validationListener
            return this
        }

        fun lifecycleOwner(lifecycleOwner : LifecycleOwner) : Builder {
            this.lifecycleOwner = lifecycleOwner
            return this
        }

        fun build(): CardValidationConfig {
            validateNotNull(pan, "pan component")
            validateNotNull(expiryDate, "expiry date component")
            validateNotNull(cvc, "cvc component")
            validateNotNull(baseUrl, "base url")
            validateNotNull(validationListener, "validation listener")
            validateNotNull(lifecycleOwner, "lifecycle owner")

            return CardValidationConfig(
                pan = pan as EditText,
                expiryDate = expiryDate as EditText,
                cvc = cvc as EditText,
                baseUrl = baseUrl as String,
                validationListener = validationListener as AccessCheckoutCardValidationListener,
                lifecycleOwner = lifecycleOwner as LifecycleOwner
            )
        }

    }
}

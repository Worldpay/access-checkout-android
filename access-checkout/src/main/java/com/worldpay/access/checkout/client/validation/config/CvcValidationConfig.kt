package com.worldpay.access.checkout.client.validation.config

import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import com.worldpay.access.checkout.ui.AccessEditText
import com.worldpay.access.checkout.util.PropertyValidationUtil.validateNotNull

/**
 * An implementation of the [ValidationConfig] that represents the cvc validation configuration.
 *
 * This configuration should be used to register the relevant fields and the listeners.
 *
 * @property[cvc] [AccessEditText] that represents the cvc ui element
 * @property[validationListener] [AccessCheckoutCvcValidationListener] that represents the validation listener that should be notified on validation changes
 * @property[lifecycleOwner] [LifecycleOwner] of the application so that validation state can be handled during lifecycle changes
 */
class CvcValidationConfig private constructor(
    val cvc: EditText,
    val validationListener: AccessCheckoutCvcValidationListener,
    val lifecycleOwner: LifecycleOwner
) : ValidationConfig {

    class Builder {

        private var cvc: EditText? = null
        private var validationListener: AccessCheckoutCvcValidationListener? = null
        private var lifecycleOwner: LifecycleOwner? = null

        /**
         * Sets the cvc ui element
         *
         * @param[cvcAccessEditText] [AccessEditText] that represents the cvc ui element
         */
        fun cvc(cvcAccessEditText: AccessEditText): Builder {
            this.cvc = cvcAccessEditText.editText
            return this
        }

        /**
         * Sets the cvc ui element
         *
         * @param[cvc] [EditText] that represents the cvc ui element
         */
        @Deprecated(
            message = "AccessEditText should now be used instead of EditText. The support for EditText components will be removed in the next major version.",
            replaceWith = ReplaceWith("cvc(cvc:AccessEditText)")
        )
        fun cvc(cvc: EditText): Builder {
            this.cvc = cvc
            return this
        }

        /**
         * Sets the validation listener that should be notified on validation changes
         *
         * @param[validationListener] [AccessCheckoutCardValidationListener] that represents the validation listener
         */
        fun validationListener(validationListener: AccessCheckoutCvcValidationListener): Builder {
            this.validationListener = validationListener
            return this
        }

        /**
         * Sets the lifecycle owner of the application so that validation state can be handled during lifecycle changes
         *
         * @param[lifecycleOwner] [LifecycleOwner] that represents the lifecycle owner of the application
         */
        fun lifecycleOwner(lifecycleOwner: LifecycleOwner): Builder {
            this.lifecycleOwner = lifecycleOwner
            return this
        }

        /**
         * Builds the validation configuration by returning an instance of the [CvcValidationConfig]
         *
         * @return [CvcValidationConfig] implementation that can be used to initialise validation
         * @throws [IllegalArgumentException] is thrown when a property is missing
         */
        fun build(): CvcValidationConfig {
            validateNotNull(cvc, "cvc component")
            validateNotNull(validationListener, "validation listener")
            validateNotNull(lifecycleOwner, "lifecycle owner")

            return CvcValidationConfig(
                cvc = cvc!!,
                validationListener = validationListener!!,
                lifecycleOwner = lifecycleOwner!!
            )
        }
    }
}

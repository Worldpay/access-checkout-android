package com.worldpay.access.checkout.client.validation.config

import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import com.worldpay.access.checkout.util.PropertyValidationUtil.validateNotNull

/**
 * An implementation of the [ValidationConfig] that represents the cvc validation configuration
 * and that can be built using the associated Builder
 *
 * This configuration should be used to register the relevant fields and the listeners.
 */
class CvcValidationConfig private constructor(
    val cvc: EditText,
    val validationListener: AccessCheckoutCvcValidationListener,
    val lifecycleOwner: LifecycleOwner
) : ValidationConfig {

    /**
     * A Builder used to create an instance of [CvcValidationConfig]
     */
    class Builder {

        private var cvc: EditText? = null
        private var validationListener: AccessCheckoutCvcValidationListener? = null
        private var lifecycleOwner: LifecycleOwner? = null

        /**
         * Sets the cvc ui element to be validated
         *
         * @param[cvcAccessCheckoutEditText] [AccessCheckoutEditText] to be validated
         */
        fun cvc(cvcAccessCheckoutEditText: AccessCheckoutEditText): Builder {
            this.cvc = cvcAccessCheckoutEditText.editText
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

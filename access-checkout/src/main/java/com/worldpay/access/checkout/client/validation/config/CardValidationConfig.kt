package com.worldpay.access.checkout.client.validation.config

import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.util.PropertyValidationUtil.validateNotNull

/**
 * An implementation of the [ValidationConfig] that represents the card validation configuration.
 *
 * This configuration should be build to register the relevant fields and the listeners.
 *
 * @property[pan] [EditText] that represents the pan ui element
 * @property[expiryDate] [EditText] that represents the expiry date ui element
 * @property[cvc] [EditText] that represents the cvc ui element
 * @property[baseUrl] [String] that represents the base url to use when calling Worldpay services
 * @property[validationListener] [AccessCheckoutCardValidationListener] that represents the validation listener that should be notified on validation changes
 * @property[lifecycleOwner] [LifecycleOwner] of the application so that validation state can be handled during lifecycle changes
 */
class CardValidationConfig private constructor(
    val pan: EditText,
    val expiryDate: EditText,
    val cvc: EditText,
    val acceptedCardBrands: Array<String>,
    val baseUrl: String,
    val validationListener: AccessCheckoutCardValidationListener,
    val lifecycleOwner : LifecycleOwner,
    val disablePanFormatting: Boolean
) : ValidationConfig {

    class Builder {

        private var pan: EditText? = null
        private var expiryDate: EditText? = null
        private var cvc: EditText? = null
        private var acceptedCardBrands: Array<String> = emptyArray()
        private var baseUrl: String? = null
        private var validationListener: AccessCheckoutCardValidationListener? = null
        private var lifecycleOwner: LifecycleOwner? = null
        private var disablePanFormatting: Boolean = false

        /**
         * Sets the pan ui element
         *
         * @param[pan] [EditText] that represents the pan ui element
         */
        fun pan(pan: EditText): Builder {
            this.pan = pan
            return this
        }

        /**
         * Sets the expiry date ui element
         *
         * @param[expiryDate] [EditText] that represents the expiry date ui element
         */
        fun expiryDate(expiryDate: EditText): Builder {
            this.expiryDate = expiryDate
            return this
        }

        /**
         * Sets the cvc ui element
         *
         * @param[cvc] [EditText] that represents the cvc ui element
         */
        fun cvc(cvc: EditText): Builder {
            this.cvc = cvc
            return this
        }

        /**
         * Sets the list of card brands to accept for validation. Any unrecognised card brand will be accepted at all times.
         *
         * @param[acceptedCardBrands] [Array] of [String] representing the card brands to accept
         */
        fun acceptedCardBrands(acceptedCardBrands: Array<String>): Builder {
            this.acceptedCardBrands = acceptedCardBrands
            return this
        }

        /**
         * Sets the base url to use when calling Worldpay services
         *
         * @param[baseUrl] [String] that represents the base url
         */
        fun baseUrl(baseUrl: String): Builder {
            this.baseUrl = baseUrl
            return this
        }

        /**
         * Sets the validation listener that should be notified on validation changes
         *
         * @param[validationListener] [AccessCheckoutCardValidationListener] that represents the validation listener
         */
        fun validationListener(validationListener: AccessCheckoutCardValidationListener): Builder {
            this.validationListener = validationListener
            return this
        }

        /**
         * Sets the lifecycle owner of the application so that validation state can be handled during lifecycle changes
         *
         * @param[lifecycleOwner] [LifecycleOwner] that represents the lifecycle owner of the application
         */
        fun lifecycleOwner(lifecycleOwner : LifecycleOwner) : Builder {
            this.lifecycleOwner = lifecycleOwner
            return this
        }

        /**
         * Disables the pan formatting on the view
         */
        fun disablePanFormatting(): Builder {
            this.disablePanFormatting = true
            return this
        }

        /**
         * Builds the validation configuration by returning an instance of the [CardValidationConfig]
         *
         * @return [CardValidationConfig] implementation that can be used to initialise validation
         * @throws [IllegalArgumentException] is thrown when a property is missing
         */
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
                acceptedCardBrands = acceptedCardBrands,
                baseUrl = baseUrl as String,
                validationListener = validationListener as AccessCheckoutCardValidationListener,
                lifecycleOwner = lifecycleOwner as LifecycleOwner,
                disablePanFormatting = disablePanFormatting
            )
        }

    }
}

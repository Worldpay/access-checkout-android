package com.worldpay.access.checkout.client.validation.config

import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.session.BaseUrlSanitiser.sanitise
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.ui.AccessEditText
import com.worldpay.access.checkout.util.PropertyValidationUtil.validateNotNull

/**
 * An implementation of the [ValidationConfig] that represents the card validation configuration.
 *
 * This configuration should be used to register the relevant fields and the listeners.
 *
 * @property[pan] [AccessEditText] that represents the pan ui element
 * @property[expiryDate] [AccessEditText] that represents the expiry date ui element
 * @property[cvc] [AccessEditText] that represents the cvc ui element
 * @property[baseUrl] [String] that represents the base url to use when calling Worldpay services
 * @property[validationListener] [AccessCheckoutCardValidationListener] that represents the validation listener that should be notified on validation changes
 * @property[lifecycleOwner] [LifecycleOwner] of the application so that validation state can be handled during lifecycle changes
 */
class CardValidationConfig private constructor(
    val pan: AccessEditText,
    val expiryDate: AccessEditText,
    val cvc: AccessEditText,
    val acceptedCardBrands: Array<String>,
    val baseUrl: String,
    val validationListener: AccessCheckoutCardValidationListener,
    val lifecycleOwner: LifecycleOwner,
    val enablePanFormatting: Boolean
) : ValidationConfig {

    class Builder {

        private var pan: AccessEditText? = null
        private var expiryDate: AccessEditText? = null
        private var cvc: AccessEditText? = null
        private var acceptedCardBrands: Array<String> = emptyArray()
        private var baseUrl: String? = null
        private var validationListener: AccessCheckoutCardValidationListener? = null
        private var lifecycleOwner: LifecycleOwner? = null
        private var enablePanFormatting: Boolean = false

        /**
         * Sets the pan ui element
         *
         * @param[panAccessEditText] [AccessEditText] that represents the pan ui element
         */
        fun pan(panAccessEditText: AccessEditText): Builder {
            this.pan = panAccessEditText
            return this
        }

        /**
         * Sets the expiry date ui element
         *
         * @param[expiryDateAccessEditText] [AccessEditText] that represents the expiry date ui element
         */
        fun expiryDate(expiryDateAccessEditText: AccessEditText): Builder {
            this.expiryDate = expiryDateAccessEditText
            return this
        }

        /**
         * Sets the cvc ui element
         *
         * @param[cvcAccessEditText] [AccessEditText] that represents the cvc ui element
         */
        fun cvc(cvcAccessEditText: AccessEditText): Builder {
            this.cvc = cvcAccessEditText
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
        fun lifecycleOwner(lifecycleOwner: LifecycleOwner): Builder {
            this.lifecycleOwner = lifecycleOwner
            return this
        }

        /**
         * Enables the pan formatting on the view
         */
        fun enablePanFormatting(): Builder {
            this.enablePanFormatting = true
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

            val sanitisedBaseUrl = sanitise(baseUrl)!!

            return CardValidationConfig(
                pan = pan!!,
                expiryDate = expiryDate!!,
                cvc = cvc!!,
                acceptedCardBrands = acceptedCardBrands,
                baseUrl = sanitisedBaseUrl,
                validationListener = validationListener!!,
                lifecycleOwner = lifecycleOwner!!,
                enablePanFormatting = enablePanFormatting
            )
        }
    }
}

package com.worldpay.access.checkout.client.validation.config

import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.session.BaseUrlSanitiser.sanitise
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import com.worldpay.access.checkout.util.PropertyValidationUtil.validateNotNull

/**
 * An implementation of the [ValidationConfig] that represents the card validation configuration
 * and that can be built using the associated Builder
 *
 * This configuration should be used to register the relevant fields and the listeners.
 */
class CardValidationConfig private constructor(
    val pan: EditText,
    val expiryDate: EditText,
    val cvc: EditText,
    val acceptedCardBrands: Array<String>,
    val baseUrl: String,
    val validationListener: AccessCheckoutCardValidationListener,
    val lifecycleOwner: LifecycleOwner,
    val enablePanFormatting: Boolean
) : ValidationConfig {

    /**
     * A Builder used to create an instance of [CardValidationConfig]
     */
    class Builder {

        private var pan: EditText? = null
        private var expiryDate: EditText? = null
        private var cvc: EditText? = null

        private var acceptedCardBrands: Array<String> = emptyArray()
        private var baseUrl: String? = null
        private var validationListener: AccessCheckoutCardValidationListener? = null
        private var lifecycleOwner: LifecycleOwner? = null
        private var enablePanFormatting: Boolean = false

        /**
         * Sets the pan ui element to be validated
         *
         * @param[panAccessCheckoutEditText] [AccessCheckoutEditText] to be validated
         */
        fun pan(panAccessCheckoutEditText: AccessCheckoutEditText): Builder {
            this.pan = panAccessCheckoutEditText.editText
            return this
        }

        /**
         * Sets the expiry date ui element to be validated
         *
         * @param[expiryDateAccessCheckoutEditText] [AccessCheckoutEditText] to be validated
         */
        fun expiryDate(expiryDateAccessCheckoutEditText: AccessCheckoutEditText): Builder {
            this.expiryDate = expiryDateAccessCheckoutEditText.editText
            return this
        }

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

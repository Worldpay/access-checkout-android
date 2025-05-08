package com.worldpay.access.checkout.client.session.model

import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.ui.AccessCheckoutEditText

/**
 * A class representing a shopper's card details that can be constructed with a [CardDetails.Builder]
 * All properties are internal and only accessible to the Access Checkout SDK.
 * This is designed to protect merchants from exposure to the card details so that they can reach the lowest
 * level of compliance (SAQ-A)
 */
class CardDetails private constructor(
    internal val pan: String?,
    internal val expiryDate: ExpiryDate?,
    internal val cvc: String?
) {

    /**
     * A builder designed to create an instance of the [CardDetails] class by passing references to:
     * - the AccessCheckoutEditText components used to capture pan, expiry date and cvc (card payment flow)
     * - or the AccessCheckoutEditText component used to capture the cvc (cvc only payment flow)
     */
    data class Builder(
        private var pan: String? = null,
        private var expiryDate: ExpiryDate? = null,
        private var cvc: String? = null
    ) {

        /**
         * Sets the pan using an instance of [AccessCheckoutEditText]
         *
         * @param[panAccessCheckoutEditText] [AccessCheckoutEditText] used to capture the pan
         */
        fun pan(panAccessCheckoutEditText: AccessCheckoutEditText) =
            apply { this.pan = panAccessCheckoutEditText.text.replace("\\s+".toRegex(), "") }

        /**
         * Sets the expiry date using an instance of [AccessCheckoutEditText]
         *
         * @param[expiryDateAccessCheckoutEditText] [AccessCheckoutEditText] used to capture the expiry date
         */
        fun expiryDate(expiryDateAccessCheckoutEditText: AccessCheckoutEditText) =
            apply { this.expiryDate = ExpiryDate(expiryDateAccessCheckoutEditText) }

        /**
         * Sets the cvc using an instance of [AccessCheckoutEditText]
         *
         * @param[cvcAccessCheckoutEditText] [AccessCheckoutEditText] used to capture the cvc
         */
        fun cvc(cvcAccessCheckoutEditText: AccessCheckoutEditText) = apply { this.cvc = cvcAccessCheckoutEditText.text }

        /**
         * Builds the [CardDetails] instance
         *
         * @return [CardDetails] instance with the given details
         */
        fun build() = CardDetails(pan, expiryDate, cvc)
    }

    /**
     * This class represents an expiry date for a [CardDetails]
     *
     * @property [month] the expiry month
     * @property [year] the expiry year
     */
    class ExpiryDate internal constructor(expiryDate: String) {

        internal val month: Int
        internal val year: Int

        private val maxExpiryDateLength = 4

        private val separator = "/"

        init {

            val expiryDateWithoutSeparator = expiryDate.trim().replace(separator, "")
            val isNumeric = isNumeric(expiryDateWithoutSeparator)
            val isCorrectLength = expiryDateWithoutSeparator.length == maxExpiryDateLength

            if (!isCorrectLength || !isNumeric) {
                throw AccessCheckoutException("expecting expiry date in format MM/YY or MMYY but found $expiryDate")
            }

            if (expiryDate.contains(separator)) {
                val split = expiryDate.split(separator)
                month = split.toTypedArray()[0].toInt()
                year = 2000 + split.toTypedArray()[1].toInt()
            } else {
                month = expiryDate.dropLast(2).toInt()
                year = 2000 + expiryDate.substring(2).toInt()
            }
        }

        constructor(expiryDateAccessCheckoutEditText: AccessCheckoutEditText) : this(expiryDateAccessCheckoutEditText.text)

        private fun isNumeric(text: String): Boolean {
            return try {
                text.toInt()
                true
            } catch (e: NumberFormatException) {
                false
            }
        }
    }
}

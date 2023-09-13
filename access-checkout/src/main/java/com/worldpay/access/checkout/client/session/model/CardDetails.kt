package com.worldpay.access.checkout.client.session.model

import com.worldpay.access.checkout.client.session.model.CardDetails.ExpiryDate
import com.worldpay.access.checkout.ui.AccessEditText

/**
 * This class is a representation of card information that can be constructed with a [CardDetails.Builder]
 *
 * @property [pan] an optional [String] containing the PAN
 * @property [expiryDate] an optional [ExpiryDate] object containing the expiry month and year
 * @property [cvc] an optional [String] containing the cvc
 */
class CardDetails private constructor(
    internal val pan: String?,
    internal val expiryDate: ExpiryDate?,
    internal val cvc: String?
) {

    /**
     * This builder helps build the [CardDetails] instance
     */
    data class Builder(
        private var pan: String? = null,
        private var expiryDate: ExpiryDate? = null,
        private var cvc: String? = null
    ) {

        /**
         * Sets the pan number for the card
         *
         * @param[panAccessEditText] [AccessEditText] that represents the pan number
         */
        fun pan(panAccessEditText: AccessEditText) = apply { this.pan = panAccessEditText.text.replace("\\s+".toRegex(), "") }

        /**
         * Sets the expiry date for the card
         *
         * @param[expiryDateAccessEditText] [AccessEditText] that represents the expiry date
         */
        fun expiryDate(expiryDateAccessEditText: AccessEditText) = apply { this.expiryDate = ExpiryDate(expiryDateAccessEditText) }

        /**
         * Sets the cvc for the card
         *
         * @param[cvcAccessEditText] [AccessEditText] that represents the cvc
         */
        fun cvc(cvcAccessEditText: AccessEditText) = apply { this.cvc = cvcAccessEditText.text }

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
    class ExpiryDate internal constructor(expiryDateAccessEditText: AccessEditText) {

        internal val month: Int
        internal val year: Int

        private val maxExpiryDateLength = 4

        private val separator = "/"

        init {

            val expiryDateWithoutSeparator = expiryDateAccessEditText.text.trim().replace(separator, "")
            val isNumeric = isNumeric(expiryDateWithoutSeparator)
            val isCorrectLength = expiryDateWithoutSeparator.length == maxExpiryDateLength

            if (!isCorrectLength || !isNumeric) {
                throw IllegalArgumentException("expecting expiry date in format MM/YY or MMYY but found ${expiryDateAccessEditText.text}")
            }

            if (expiryDateAccessEditText.text.contains(separator)) {
                val split = expiryDateAccessEditText.text.split(separator)
                month = split.toTypedArray()[0].toInt()
                year = 2000 + split.toTypedArray()[1].toInt()
            } else {
                month = expiryDateAccessEditText.text.dropLast(2).toInt()
                year = 2000 + expiryDateAccessEditText.text.substring(2).toInt()
            }
        }

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

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
    val pan: String?,
    val expiryDate: ExpiryDate?,
    val cvc: String?
) {

    /**
     * This build helps building the [CardDetails] instance
     */
    data class Builder(
        private var pan: String? = null,
        private var expiryDate: ExpiryDate? = null,
        private var cvc: String? = null
    ) {

        /**
         * Sets the pan number for the card
         *
         * @param[pan] [String] that represents the pan number
         */
        fun pan(panEditText: AccessEditText) = apply { this.pan = panEditText.text.replace("\\s+".toRegex(), "") }

        /**
         * Sets the expiry date for the card
         *
         * @param[expiryDate] [String] that represents the expiry date
         */
        fun expiryDate(expiryDateEditText: AccessEditText) = apply { this.expiryDate = ExpiryDate(expiryDateEditText) }

        /**
         * Sets the cvc for the card
         *
         * @param[cvc] [String] that represents the cvc
         */
        fun cvc(cvcEditText: AccessEditText) = apply { this.cvc = cvcEditText.text }

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
    class ExpiryDate internal constructor(expiryDateEditText: AccessEditText) {

        val month: Int
        val year: Int

        private val maxExpiryDateLength = 4

        private val separator = "/"

        init {

            val expiryDateWithoutSeparator = expiryDateEditText.text.trim().replace(separator, "")
            val isNumeric = isNumeric(expiryDateWithoutSeparator)
            val isCorrectLength = expiryDateWithoutSeparator.length == maxExpiryDateLength

            if (!isCorrectLength || !isNumeric) {
                throw IllegalArgumentException("expecting expiry date in format MM/YY or MMYY but found ${expiryDateEditText.text}")
            }

            if (expiryDateEditText.text.contains(separator)) {
                val split = expiryDateEditText.text.split(separator)
                month = split.toTypedArray()[0].toInt()
                year = 2000 + split.toTypedArray()[1].toInt()
            } else {
                month = expiryDateEditText.text.dropLast(2).toInt()
                year = 2000 + expiryDateEditText.text.substring(2).toInt()
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

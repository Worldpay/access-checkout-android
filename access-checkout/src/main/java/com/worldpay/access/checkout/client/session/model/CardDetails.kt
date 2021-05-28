package com.worldpay.access.checkout.client.session.model

import com.worldpay.access.checkout.client.session.model.CardDetails.ExpiryDate

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
        fun pan(pan: String) = apply { this.pan = pan.replace("\\s+".toRegex(), "") }

        /**
         * Sets the expiry date for the card
         *
         * @param[expiryDate] [String] that represents the expiry date
         */
        fun expiryDate(expiryDate: String) = apply { this.expiryDate = ExpiryDate(expiryDate) }

        /**
         * Sets the cvc for the card
         *
         * @param[cvc] [String] that represents the cvc
         */
        fun cvc(cvc: String) = apply { this.cvc = cvc }

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

        val month: Int
        val year: Int

        private val maxExpiryDateLength = 4

        private val separator = "/"

        init {

            val expiryDateWithoutSeparator = expiryDate.trim().replace(separator, "")
            val isNumeric = isNumeric(expiryDateWithoutSeparator)
            val isCorrectLength = expiryDateWithoutSeparator.length == maxExpiryDateLength

            if (!isCorrectLength || !isNumeric) {
                throw IllegalArgumentException("expecting expiry date in format MM/YY or MMYY but found $expiryDate")
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

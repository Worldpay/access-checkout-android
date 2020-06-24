package com.worldpay.access.checkout.client.session.model

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

    data class Builder(
        private var pan: String? = null,
        private var expiryDate: ExpiryDate? = null,
        private var cvc: String? = null
    ) {

        fun pan(pan: String) = apply { this.pan = pan }

        fun expiryDate(expiryDate: String) = apply {
            this.expiryDate = ExpiryDate(expiryDate)
        }

        fun cvc(cvc: String) = apply { this.cvc = cvc }

        fun build() =
            CardDetails(pan, expiryDate, cvc)

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
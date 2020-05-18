package com.worldpay.access.checkout.client

/**
 * [CardDetails] is a representation of card information that can be constructed with a builder.
 *
 * @param[pan] an optional [String] containing the PAN
 * @param[expiryDate] an optional [ExpiryDate] object containing the expiry month and year
 * @param[cvv] an optional [String] containing the cvv
 */
class CardDetails private constructor(
    val pan: String?,
    val expiryDate: ExpiryDate?,
    val cvv: String?
) {

    data class Builder(
        private var pan: String? = null,
        private var expiryDate: ExpiryDate? = null,
        private var cvv: String? = null
    ) {

        fun pan(pan: String) = apply { this.pan = pan }

        fun expiryDate(month: Int, year: Int) = apply { this.expiryDate =
            ExpiryDate(month, year)
        }

        fun cvv(cvv: String) = apply { this.cvv = cvv }

        fun build() =
            CardDetails(pan, expiryDate, cvv)

    }

}

class ExpiryDate(val month: Int, val year: Int)

package com.worldpay.access.checkout.client

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

package com.worldpay.access.checkout.client.card

class CardDetails(val pan: String?, val expiryDate: ExpiryDate?, val cvv: String?)

class ExpiryDate(val month: Int, val year: Int)

/**
 * The [CardDetailsBuilder] should be used to help build a card details object providing the details
 * that are required
 */
class CardDetailsBuilder {

    private var pan: String? = null

    private var expiryDate: ExpiryDate? = null

    private var cvv: String? = null

    fun pan(pan: String) = apply { this.pan = pan }

    fun expiryDate(month: Int, year: Int) = apply { this.expiryDate = ExpiryDate(month, year) }

    fun cvv(cvv: String) = apply { this.cvv = cvv }

    fun build() = CardDetails(pan, expiryDate, cvv)

}
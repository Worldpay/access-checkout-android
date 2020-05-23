package com.worldpay.access.checkout.testutils

object CardNumberUtil {

    const val PARTIAL_VISA = "4111"

    const val AMEX_PAN = "342793178931249"
    const val VISA_PAN = "4111111111111111"

    fun visa(length: Int = 16): String {
        return when (length) {
            16 -> "4111111111111111"
            18 -> "411111111111111111"
            19 -> "4111111111111111111"
            else -> throw IllegalArgumentException("valid lengths are 16, 18, 19")
        }
    }

}
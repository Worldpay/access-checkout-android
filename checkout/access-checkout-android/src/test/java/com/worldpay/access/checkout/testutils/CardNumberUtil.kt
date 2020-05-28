package com.worldpay.access.checkout.testutils

object CardNumberUtil {

    const val PARTIAL_VISA = "4111"

    const val VISA_PAN = "4111111111111111"
    const val MASTERCARD_PAN = "5292892193835234"
    const val AMEX_PAN = "342793178931249"
    const val JCB_PAN = "3534268134677774"
    const val DISCOVER_PAN = "6011454625346690"
    const val DINERS_PAN = "36121966553184"
    const val MAESTRO_PAN = "6761577168010117"

    fun visa(length: Int = 16): String {
        return when (length) {
            16 -> "4111111111111111"
            18 -> "411111111111111111"
            19 -> "4111111111111111111"
            else -> throw IllegalArgumentException("valid lengths are 16, 18, 19")
        }
    }

}
package com.worldpay.access.checkout.testutils

object CardNumberUtil {

    const val PARTIAL_VISA = "4111"

    const val MASTERCARD_PAN = "5292892193835234"
    const val MASTERCARD_PAN_FORMATTED = "5292 8921 9383 5234"
    const val AMEX_PAN = "342793178931249"
    const val AMEX_PAN_FORMATTED = "3427 931789 31249"
    const val JCB_PAN = "3534268134677774"
    const val DISCOVER_PAN = "6011454625346690"
    const val DINERS_PAN = "36121966553184"
    const val MAESTRO_PAN = "6761577168010117"

    const val VALID_UNKNOWN_LUHN = "0999008073997244886"
    const val VALID_UNKNOWN_LUHN_FORMATTED = "0999 0080 7399 7244 886"
    const val INVALID_UNKNOWN_LUHN = "0999008073997244887"

    fun visaPan(length: Int = 16, formatted: Boolean = false): String {
        return if (formatted) {
            when (length) {
                16 -> "4111 1111 1111 1111"
                18 -> "4111 1111 1111 1111 11"
                19 -> "4111 1111 1111 1111 111"
                else -> throw IllegalArgumentException("valid lengths are 16, 18, 19")
            }
        } else {
            when (length) {
                16 -> "4111111111111111"
                18 -> "411111111111111111"
                19 -> "4111111111111111111"
                else -> throw IllegalArgumentException("valid lengths are 16, 18, 19")
            }
        }
    }
}

package com.worldpay.access.checkout.sample.card

object CardNumberUtil {

    const val PARTIAL_VISA = "4111"
    const val PARTIAL_MASTERCARD = "22"
    const val PARTIAL_AMEX = "34"
    const val PARTIAL_MAESTRO = "676"
    const val PARTIAL_UNKNOWN_LUHN = "1111111"

    const val VISA_PAN = "4111111111111111"

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
}

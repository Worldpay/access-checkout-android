package com.worldpay.access.checkout.api.configuration

internal object DefaultCardRules {

    const val DEFAULT_MATCHER = "^[0-9]*\$"

    val PAN_DEFAULTS =
        CardValidationRule(
            matcher = DEFAULT_MATCHER,
            validLengths = listOf(12, 13, 14, 15, 16, 17, 18, 19)
        )

    val CVC_DEFAULTS =
        CardValidationRule(
            matcher = DEFAULT_MATCHER,
            validLengths = listOf(3, 4)
        )

    val EXPIRY_DATE_DEFAULTS =
        CardValidationRule(
            matcher = "^(0[1-9]|1[0-2])\\/([0-9][0-9])\$",
            validLengths = listOf(5)
        )

    val MONTH_DEFAULTS =
        CardValidationRule(
            matcher = "^0[1-9]{0,1}$|^1[0-2]{0,1}$",
            validLengths = listOf(2)
        )

    val YEAR_DEFAULTS =
        CardValidationRule(
            matcher = "^\\d{0,2}$",
            validLengths = listOf(2)
        )

    val CARD_DEFAULTS =
        CardDefaults(
            PAN_DEFAULTS,
            CVC_DEFAULTS,
            MONTH_DEFAULTS,
            YEAR_DEFAULTS,
            EXPIRY_DATE_DEFAULTS
        )

}

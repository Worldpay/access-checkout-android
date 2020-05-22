package com.worldpay.access.checkout.testutils

import com.worldpay.access.checkout.api.configuration.*

object CardConfigurationUtil {

    object Configurations {

        val CARD_CONFIG_BASIC =
            CardConfiguration(
                brands = listOf(
                    Brands.VISA_BRAND,
                    Brands.MASTERCARD_BRAND,
                    Brands.AMEX_BRAND
                ),
                defaults = Defaults.CARD_DEFAULTS
            )

        val CARD_CONFIG_NO_BRAND =
            CardConfiguration(
                brands = emptyList(),
                defaults = Defaults.CARD_DEFAULTS
            )

    }

    object Defaults {

        const val MATCHER = "^[0-9]*\$"

        val PAN_RULE =
            CardValidationRule(
                matcher = MATCHER,
                validLengths = listOf(15, 16, 18, 19)
            )

        val CVV_RULE =
            CardValidationRule(
                matcher = MATCHER,
                validLengths = listOf(3, 4)
            )

        val EXP_MONTH_RULE =
            CardValidationRule(
                "^0[1-9]{0,1}$|^1[0-2]{0,1}$",
                listOf(2)
            )

        val EXP_YEAR_RULE =
            CardValidationRule(
                "^\\d{0,2}$",
                listOf(2)
            )

        val CARD_DEFAULTS =
            CardDefaults(
                PAN_RULE,
                CVV_RULE,
                EXP_MONTH_RULE,
                EXP_YEAR_RULE
            )
    }

    object Brands {

        object Images {
            private const val BASE_PATH = "http://base-url/access-checkout/assets"

            val VISA_PNG =
                CardBrandImage(
                    type = "image/png",
                    url = "$BASE_PATH/visa.png"
                )
            val VISA_SVG =
                CardBrandImage(
                    type = "image/svg+xml",
                    url = "$BASE_PATH/visa.svg"
                )

            val MASTERCARD_PNG =
                CardBrandImage(
                    type = "image/png",
                    url = "$BASE_PATH/mastercard.png"
                )
            val MASTERCARD_SVG =
                CardBrandImage(
                    type = "image/svg+xml",
                    url = "$BASE_PATH/mastercard.svg"
                )

            val AMEX_PNG =
                CardBrandImage(
                    type = "image/png",
                    url = "$BASE_PATH/amex.png"
                )
            val AMEX_SVG =
                CardBrandImage(
                    type = "image/svg+xml",
                    url = "$BASE_PATH/amex.svg"
                )
        }

        val VISA_BRAND =
            CardBrand(
                name = "visa",
                images = listOf(
                    Images.VISA_PNG,
                    Images.VISA_SVG
                ),
                cvv = CardValidationRule(
                    matcher = "^\\d{0,3}\$",
                    validLengths = listOf(3)
                ),
                pan = CardValidationRule(
                    matcher = "^4\\d{0,15}",
                    validLengths = listOf(16)
                )
            )

        val MASTERCARD_BRAND =
            CardBrand(
                name = "mastercard",
                images = listOf(
                    Images.MASTERCARD_PNG,
                    Images.MASTERCARD_SVG
                ),
                cvv = CardValidationRule(
                    matcher = "^\\d{0,3}\$",
                    validLengths = listOf(3)
                ),
                pan = CardValidationRule(
                    matcher = "^5\\d{0,15}\$",
                    validLengths = listOf(16)
                )
            )

        val AMEX_BRAND =
            CardBrand(
                name = "amex",
                images = listOf(
                    Images.AMEX_PNG,
                    Images.AMEX_SVG
                ),
                cvv = CardValidationRule(
                    matcher = "^\\d{0,4}\$",
                    validLengths = listOf(4)
                ),
                pan = CardValidationRule(
                    matcher = "^3[47]\\d{0,13}\$",
                    validLengths = listOf(15)
                )
            )

    }

}
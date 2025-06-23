package com.worldpay.access.checkout.testutils

import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.api.configuration.CardDefaults
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.api.configuration.RemoteCardBrandImage
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.MATCHER
import com.worldpay.access.checkout.validation.configuration.CardConfigurationProvider
import com.worldpay.access.checkout.validation.transformers.ToCardBrandTransformer
import org.mockito.kotlin.given
import org.mockito.kotlin.mock

internal object CardConfigurationUtil {

    const val BASE_PATH = "https://example.com/access-checkout/assets"

    object Configurations {

        val CARD_CONFIG_BASIC =
            CardConfiguration(
                brands = listOf(
                    Brands.VISA_BRAND,
                    Brands.MASTERCARD_BRAND,
                    Brands.AMEX_BRAND,
                    Brands.JCB_BRAND,
                    Brands.DISCOVER_BRAND,
                    Brands.DINERS_BRAND,
                    Brands.MAESTRO_BRAND
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
                validLengths = listOf(12, 13, 14, 15, 16, 17, 18, 19)
            )

        val CVC_RULE =
            CardValidationRule(
                matcher = MATCHER,
                validLengths = listOf(3, 4)
            )

        val EXP_DATE_DEFAULTS =
            CardValidationRule(
                matcher = "^(0[1-9]|1[0-2])\\/([0-9][0-9])\$",
                validLengths = listOf(5)
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
                CVC_RULE,
                EXP_MONTH_RULE,
                EXP_YEAR_RULE,
                EXP_DATE_DEFAULTS
            )
    }

    object Brands {

        object Images {

            val VISA_PNG = RemoteCardBrandImage(type = "image/png", url = "$BASE_PATH/visa.png")
            val VISA_SVG = RemoteCardBrandImage(type = "image/svg+xml", url = "$BASE_PATH/visa.svg")

            val MASTERCARD_PNG = RemoteCardBrandImage(type = "image/png", url = "$BASE_PATH/mastercard.png")
            val MASTERCARD_SVG = RemoteCardBrandImage(type = "image/svg+xml", url = "$BASE_PATH/mastercard.svg")

            val AMEX_PNG = RemoteCardBrandImage(type = "image/png", url = "$BASE_PATH/amex.png")
            val AMEX_SVG = RemoteCardBrandImage(type = "image/svg+xml", url = "$BASE_PATH/amex.svg")

            val JCB_PNG = RemoteCardBrandImage(type = "image/png", url = "$BASE_PATH/jcb.png")
            val JCB_SVG = RemoteCardBrandImage(type = "image/svg+xml", url = "$BASE_PATH/jcb.svg")

            val DISCOVER_PNG = RemoteCardBrandImage(type = "image/png", url = "$BASE_PATH/discover.png")
            val DISCOVER_SVG = RemoteCardBrandImage(type = "image/svg+xml", url = "$BASE_PATH/discover.svg")

            val DINERS_PNG = RemoteCardBrandImage(type = "image/png", url = "$BASE_PATH/diners.png")
            val DINERS_SVG = RemoteCardBrandImage(type = "image/svg+xml", url = "$BASE_PATH/diners.svg")

            val MAESTRO_PNG = RemoteCardBrandImage(type = "image/png", url = "$BASE_PATH/maestro.png")
            val MAESTRO_SVG = RemoteCardBrandImage(type = "image/svg+xml", url = "$BASE_PATH/maestro.svg")
        }

        val VISA_BRAND =
            RemoteCardBrand(
                name = "visa",
                images = listOf(
                    Images.VISA_PNG,
                    Images.VISA_SVG
                ),
                cvc = CardValidationRule(
                    matcher = MATCHER,
                    validLengths = listOf(3)
                ),
                pan = CardValidationRule(
                    matcher = "^(?!^493698\\d*\$)4\\d*\$",
                    validLengths = listOf(16, 18, 19)
                )
            )

        val MASTERCARD_BRAND =
            RemoteCardBrand(
                name = "mastercard",
                images = listOf(
                    Images.MASTERCARD_PNG,
                    Images.MASTERCARD_SVG
                ),
                cvc = CardValidationRule(
                    matcher = MATCHER,
                    validLengths = listOf(3)
                ),
                pan = CardValidationRule(
                    matcher = "^(5[1-5]|2[2-7])\\d*\$",
                    validLengths = listOf(16)
                )
            )

        val AMEX_BRAND =
            RemoteCardBrand(
                name = "amex",
                images = listOf(
                    Images.AMEX_PNG,
                    Images.AMEX_SVG
                ),
                cvc = CardValidationRule(
                    matcher = MATCHER,
                    validLengths = listOf(4)
                ),
                pan = CardValidationRule(
                    matcher = "^3[47]\\d*\$",
                    validLengths = listOf(15)
                )
            )

        val JCB_BRAND =
            RemoteCardBrand(
                name = "jcb",
                images = listOf(
                    Images.JCB_PNG,
                    Images.JCB_SVG
                ),
                cvc = CardValidationRule(
                    matcher = MATCHER,
                    validLengths = listOf(3)
                ),
                pan = CardValidationRule(
                    matcher = "^(35[2-8]|2131|1800)\\d*\$",
                    validLengths = listOf(16, 17, 18, 19)
                )
            )

        val DISCOVER_BRAND =
            RemoteCardBrand(
                name = "discover",
                images = listOf(
                    Images.DISCOVER_PNG,
                    Images.DISCOVER_SVG
                ),
                cvc = CardValidationRule(
                    matcher = MATCHER,
                    validLengths = listOf(3)
                ),
                pan = CardValidationRule(
                    matcher = "^(6011|64[4-9]|65)\\d*$",
                    validLengths = listOf(16, 19)
                )
            )

        val DINERS_BRAND =
            RemoteCardBrand(
                name = "diners",
                images = listOf(
                    Images.DINERS_PNG,
                    Images.DINERS_SVG
                ),
                cvc = CardValidationRule(
                    matcher = MATCHER,
                    validLengths = listOf(3)
                ),
                pan = CardValidationRule(
                    matcher = "^(30[0-5]|36|38|39)\\d*\$",
                    validLengths = listOf(14, 16, 19)
                )
            )

        val MAESTRO_BRAND =
            RemoteCardBrand(
                name = "maestro",
                images = listOf(
                    Images.MAESTRO_PNG,
                    Images.MAESTRO_SVG
                ),
                cvc = CardValidationRule(
                    matcher = MATCHER,
                    validLengths = listOf(3)
                ),
                pan = CardValidationRule(
                    matcher = "^(493698|(50[0-5][0-9]{2}|506[0-5][0-9]|5066[0-9])|(5067[7-9]|506[89][0-9]|50[78][0-9]{2})|5[6-9]|63|67)\\d*\$",
                    validLengths = listOf(12, 13, 14, 15, 16, 17, 18, 19)
                )
            )
    }

    suspend fun mockSuccessfulCardConfiguration() {
        val cardConfigurationClient = mock<CardConfigurationClient>()
        given(cardConfigurationClient.getCardConfiguration()).willReturn(CARD_CONFIG_BASIC)
        CardConfigurationProvider(cardConfigurationClient, emptyList())
    }

    suspend fun mockUnsuccessfulCardConfiguration() {
        val cardConfigurationClient = mock<CardConfigurationClient>()
        given(cardConfigurationClient.getCardConfiguration()).willReturn(null)
        CardConfigurationProvider(cardConfigurationClient, emptyList())
    }

    fun toCardBrand(remoteCardBrand: RemoteCardBrand): CardBrand {
        return ToCardBrandTransformer().transform(remoteCardBrand)!!
    }

    fun toCardBrandList(remoteCardBrand: RemoteCardBrand): List<CardBrand> {
        val cardBrand = toCardBrand(remoteCardBrand)
        return listOf(cardBrand)
    }
}

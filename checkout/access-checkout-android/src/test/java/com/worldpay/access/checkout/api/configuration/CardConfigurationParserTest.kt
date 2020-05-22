package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDeserializationException
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.CVV_RULE
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.EXP_MONTH_RULE
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.EXP_YEAR_RULE
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.MATCHER
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.PAN_RULE
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import kotlin.test.assertNull

class CardConfigurationParserTest {

    private lateinit var cardConfigurationParser: CardConfigurationParser

    private val validCardConfigurationJson = """
                   [
                    {
                        "name": "visa",
                        "pattern": "/^4\\d*$/",
                        "panLengths": [
                          16,
                          18,
                          19
                        ],
                        "cvvLength": 3,
                        "images": [
                          {
                            "type": "image/png",
                            "url": "<BASE_URL>/visa.png"
                          },
                          {
                            "type": "image/svg+xml",
                            "url": "<BASE_URL>/visa.svg"
                          }
                        ]
                    },
                      {
                        "name": "mastercard",
                        "pattern": "^(5[1-5]|2[2-7])\\d*${'$'}",
                        "panLengths": [
                          16
                        ],
                        "cvvLength": 3,
                        "images": [
                          {
                            "type": "image/png",
                            "url": "<BASE_URL>/mastercard.png"
                          },
                          {
                            "type": "image/svg+xml",
                            "url": "<BASE_URL>/mastercard.svg"
                          }
                        ]
                      },
                      {
                        "name": "amex",
                        "pattern": "^3[47]\\d*${'$'}",
                        "panLengths": [
                          15
                        ],
                        "cvvLength": 4,
                        "images": [
                          {
                            "type": "image/png",
                            "url": "<BASE_URL>/amex.png"
                          },
                          {
                            "type": "image/svg+xml",
                            "url": "<BASE_URL>/amex.svg"
                          }
                        ]
                      }
                    ]
            """.trimIndent()

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Before
    fun setUp() {
        cardConfigurationParser =
            CardConfigurationParser()
    }

    @Test
    fun givenANullInputShouldReturnADefaultCardConfiguration() {
        val cardConfiguration = cardConfigurationParser.parse(null)
        assertEquals(PAN_RULE, cardConfiguration.defaults.pan)
        assertEquals(CVV_RULE, cardConfiguration.defaults.cvv)
        assertEquals(EXP_MONTH_RULE, cardConfiguration.defaults.month)
        assertEquals(EXP_YEAR_RULE, cardConfiguration.defaults.year)
    }

    @Test
    fun givenAnEmptyConfigShouldReturnADefaultCardConfiguration() {
        val cardConfiguration = cardConfigurationParser.parse("".byteInputStream())
        assertEquals(PAN_RULE, cardConfiguration.defaults.pan)
        assertEquals(CVV_RULE, cardConfiguration.defaults.cvv)
        assertEquals(EXP_MONTH_RULE, cardConfiguration.defaults.month)
        assertEquals(EXP_YEAR_RULE, cardConfiguration.defaults.year)
    }

    @Test
    fun givenAMalformedJsonInputShouldThrowAccessCheckoutDeserializationError() {
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Cannot interpret json: ABC")

        cardConfigurationParser.parse("ABC".byteInputStream())
    }

    @Test
    fun givenJsonWithMissingOptionalPropertiesShouldNotThrowAccessCheckoutDeserializationError() {
        val jsonWithMissingOptionalBrandProps = """
                [
                    {
                        "name": "brand",
                        "panLengths": []
                    }
                ]
        """.trimIndent()

        val cardConfiguration =
            cardConfigurationParser.parse(jsonWithMissingOptionalBrandProps.byteInputStream())

        assertEquals(1, cardConfiguration.brands.size)
        assertNull(cardConfiguration.brands[0].cvv)
        assertEquals(MATCHER, cardConfiguration.brands[0].pan?.matcher)
        assertEquals(emptyList<Int>(), cardConfiguration.brands[0].pan?.validLengths)
    }

    @Test
    fun givenJsonWithMissingRequiredPropertiesShouldThrowAccessCheckoutDeserializationError() {
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Missing property: 'name'")

        val requiredPropsMissing = """
                [
                    {
                        "image": "brand_logo",
                        "panLengths": [
                        ]
                    }
                ]
        """.trimIndent()

        cardConfigurationParser.parse(requiredPropsMissing.byteInputStream())
    }

    @Test
    fun givenJsonWithWrongTypeForStringPropertyThenShouldThrowAccessCheckoutDeserializationError() {
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Invalid property type: 'pattern', expected 'String'")

        val wrongPropertyType = """
             [
                {
                    "name": "brand",
                    "image": "brand_logo",
                    "pattern": 123,
                    "cvvLength": 3,
                    "panLengths": [
                    ]
                }
            ]
        """.trimIndent()
        cardConfigurationParser.parse(wrongPropertyType.byteInputStream())
    }

    @Test
    fun givenJsonWithWrongTypeForIntPropertyThenShouldThrowAccessCheckoutDeserializationError() {
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Invalid property type: 'cvvLength', expected 'Int'")

        val wrongPropertyType = """
           [
                {
                    "name": "brand",
                    "image": "brand_logo",
                    "cvvLength": "3",
                    "panLengths": [16]
                }
            ]
        """.trimIndent()

        cardConfigurationParser.parse(wrongPropertyType.byteInputStream())
    }

    @Test
    fun givenEmptyBrandsConfigThenShouldUseEmptyListInstead() {
        val missingBrands = """""".trimIndent()

        val cardConfiguration = cardConfigurationParser.parse(missingBrands.byteInputStream())

        assertEquals(0, cardConfiguration.brands.size)
    }

    @Test
    fun givenBrandsConfigWithEmptyCardValidationRulesThenShouldParseSuccessfully() {
        val emptyBrands = """[]""".trimIndent()

        val cardConfiguration = cardConfigurationParser.parse(emptyBrands.byteInputStream())

        assertEquals(emptyList<CardBrand>(), cardConfiguration.brands)
    }

    @Test
    fun givenAValidConfigShouldReturnACompleteCardConfiguration() {

        val cardConfiguration =
            cardConfigurationParser.parse(validCardConfigurationJson.byteInputStream())

        assertEquals(mutableListOf(16,18,19), cardConfiguration.brands[0].pan?.validLengths)
        assertEquals(mutableListOf(16), cardConfiguration.brands[1].pan?.validLengths)
        assertEquals(mutableListOf(15), cardConfiguration.brands[2].pan?.validLengths)

        val visa = cardConfiguration.brands[0]
        assertEquals("visa", visa.name)

        assertEquals(2, visa.images?.size)
        val visaCardBrandImage1 = visa.images!![0]
        assertEquals("image/png", visaCardBrandImage1.type)
        assertEquals("<BASE_URL>/visa.png", visaCardBrandImage1.url)
        val visaCardBrandImage2 = visa.images!![1]
        assertEquals("image/svg+xml", visaCardBrandImage2.type)
        assertEquals("<BASE_URL>/visa.svg", visaCardBrandImage2.url)

        assertEquals(listOf(3), visa.cvv?.validLengths)

        val mastercard = cardConfiguration.brands[1]
        assertEquals("mastercard", mastercard.name)

        assertEquals(2, mastercard.images?.size)
        val mastercardCardBrandImage1 = mastercard.images!![0]
        assertEquals("image/png", mastercardCardBrandImage1.type)
        assertEquals("<BASE_URL>/mastercard.png", mastercardCardBrandImage1.url)
        val mastercardCardBrandImage2 = mastercard.images!![1]
        assertEquals("image/svg+xml", mastercardCardBrandImage2.type)
        assertEquals("<BASE_URL>/mastercard.svg", mastercardCardBrandImage2.url)

        assertEquals(listOf(3), mastercard.cvv?.validLengths)
        assertEquals(listOf(16), mastercard.pan?.validLengths)

        val amex = cardConfiguration.brands[2]
        assertEquals("amex", amex.name)

        assertEquals(2, amex.images?.size)
        val amexCardBrandImage1 = amex.images!![0]
        assertEquals("image/png", amexCardBrandImage1.type)
        assertEquals("<BASE_URL>/amex.png", amexCardBrandImage1.url)
        val amexCardBrandImage2 = amex.images!![1]
        assertEquals("image/svg+xml", amexCardBrandImage2.type)
        assertEquals("<BASE_URL>/amex.svg", amexCardBrandImage2.url)

        assertEquals(listOf(4), amex.cvv?.validLengths)
        assertEquals(listOf(15), amex.pan?.validLengths)
    }

    @Test
    fun `should return a CardConfiguration with default parameters when valid json passed`() {
        val cardConfiguration =
            cardConfigurationParser.parse(validCardConfigurationJson.byteInputStream())

        assertEquals(listOf(15,16,18,19), cardConfiguration.defaults.pan.validLengths)
        assertEquals(listOf(3,4), cardConfiguration.defaults.cvv.validLengths)
        assertEquals(listOf(2), cardConfiguration.defaults.month.validLengths)
        assertEquals(listOf(2), cardConfiguration.defaults.year.validLengths)
    }

    @Test
    fun `should return a CardConfiguration when JSON is empty`() {
        val cardConfigurationJson = """"""

        val cardConfiguration =
            cardConfigurationParser.parse(cardConfigurationJson.byteInputStream())

        assertEquals(listOf(15,16,18,19), cardConfiguration.defaults.pan.validLengths)
        assertEquals(listOf(3,4), cardConfiguration.defaults.cvv.validLengths)
        assertEquals(listOf(2), cardConfiguration.defaults.month.validLengths)
        assertEquals(listOf(2), cardConfiguration.defaults.year.validLengths)
    }
}
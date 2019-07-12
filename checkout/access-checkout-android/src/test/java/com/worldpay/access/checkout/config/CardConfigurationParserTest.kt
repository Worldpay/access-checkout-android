package com.worldpay.access.checkout.config

import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDeserializationException
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardConfiguration
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
class CardConfigurationParserTest {

    private lateinit var cardConfigurationParser: CardConfigurationParser

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Before
    fun setUp() {
        cardConfigurationParser = CardConfigurationParser()
    }

    @Test
    fun givenANullInputShouldReturnAnEmptyCardConfiguration() {
        assertEquals(CardConfiguration(), cardConfigurationParser.parse(null))
    }

    @Test
    fun givenAnEmptyConfigShouldReturnAnEmptyCardConfiguration() {
        assertEquals(CardConfiguration(), cardConfigurationParser.parse("".byteInputStream()))
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
            {
                "defaults": {},
                "brands": [
                    {
                        "name": "brand",
                        "pans": [
                            {
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()

        val cardConfiguration =
            cardConfigurationParser.parse(jsonWithMissingOptionalBrandProps.byteInputStream())

        assertEquals(1, cardConfiguration.brands!!.size)
        assertEquals(1, cardConfiguration.brands!![0].pans.size)
        assertNull(cardConfiguration.brands!![0].cvv)
        assertNull(cardConfiguration.brands!![0].pans[0].matcher)
        assertNull(cardConfiguration.brands!![0].pans[0].maxLength)
        assertNull(cardConfiguration.brands!![0].pans[0].minLength)
        assertNull(cardConfiguration.brands!![0].pans[0].validLength)
        assertEquals(emptyList<CardConfiguration>(), cardConfiguration.brands!![0].pans[0].subRules)
    }

    @Test
    fun givenJsonWithMissingRequiredPropertiesShouldThrowAccessCheckoutDeserializationError() {
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Missing property: 'name'")

        val requiredPropsMissing = """
            {
                "defaults": {},
                "brands": [
                    {
                        "image": "brand_logo",
                        "cvv": {
                            "matcher": "some_matcher",
                            "validLength": 3
                        },
                        "pans": [
                        ]
                    }
                ]
            }
        """.trimIndent()

        cardConfigurationParser.parse(requiredPropsMissing.byteInputStream())
    }

    @Test
    fun givenJsonWithWrongTypeForStringPropertyThenShouldThrowAccessCheckoutDeserializationError() {
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Invalid property type: 'matcher', expected 'String'")

        val wrongPropertyType = """
            {
                "defaults": {},
                "brands": [
                    {
                        "name": "brand",
                        "image": "brand_logo",
                        "cvv": {
                            "matcher": "some_matcher",
                            "validLength": 3
                        },
                        "pans": [
                            {
                                "matcher": 123
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()

        cardConfigurationParser.parse(wrongPropertyType.byteInputStream())
    }

    @Test
    fun givenJsonWithWrongTypeForIntPropertyThenShouldThrowAccessCheckoutDeserializationError() {
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Invalid property type: 'maxLength', expected 'Int'")

        val wrongPropertyType = """
            {
                "defaults": {},
                "brands": [
                    {
                        "name": "brand",
                        "image": "brand_logo",
                        "cvv": {
                            "matcher": "some_matcher",
                            "validLength": 3
                        },
                        "pans": [
                            {
                                "maxLength": "123"
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()

        cardConfigurationParser.parse(wrongPropertyType.byteInputStream())
    }

    @Test
    fun givenEmptyBrandsConfigThenShouldParseSuccessfully() {
        val missingBrands = """
            {
                "defaults": {
                    "pan": {
                        "matcher": "^\\d{0,19}${'$'}",
                        "minLength": 13,
                        "maxLength": 19
                    },
                    "cvv": {
                        "matcher": "^\\d{0,4}${'$'}",
                        "minLength": 3,
                        "maxLength": 4
                    },
                    "month": {
                        "matcher": "^0[1-9]{0,1}${'$'}|^1[0-2]{0,1}${'$'}",
                        "minLength": 2,
                        "maxLength": 2
                    },
                    "year": {
                        "matcher": "^\\d{0,2}${'$'}",
                        "minLength": 2,
                        "maxLength": 2
                    }
                }
            }
        """.trimIndent()

        val cardConfiguration = cardConfigurationParser.parse(missingBrands.byteInputStream())

        assertNull(cardConfiguration.brands)
    }

    @Test
    fun givenNoDefaultsConfigThenShouldParseSuccessfully() {
        val missingDefaults = """
            {
                "brands": [{
                    "name": "test",
                    "image": "test",
                    "cvv": {
                        "matcher": "^\\d{0,3}${'$'}",
                        "validLength": 3
                    },
                    "pans": [
                        {
                            "matcher": "^4\\d{0,15}",
                            "validLength": 16
                        }
                    ]
                }]
            }
        """.trimIndent()


        val cardConfiguration = cardConfigurationParser.parse(missingDefaults.byteInputStream())

        assertNull(cardConfiguration.defaults)
    }

    @Test
    fun givenDefaultConfigWithEmptyCardValidationRulesThenShouldParseSuccessfully() {
        val emptyDefaults = """
            {
                "defaults": {}
            }
        """.trimIndent()

        val cardConfiguration = cardConfigurationParser.parse(emptyDefaults.byteInputStream())

        assertNotNull(cardConfiguration.defaults)
        assertNull(cardConfiguration.defaults?.pan)
        assertNull(cardConfiguration.defaults?.cvv)
        assertNull(cardConfiguration.defaults?.month)
        assertNull(cardConfiguration.defaults?.year)
    }

    @Test
    fun givenBrandsConfigWithEmptyCardValidationRulesThenShouldParseSuccessfully() {
        val emptyBrands = """
            {
                "brands": []
            }
        """.trimIndent()

        val cardConfiguration = cardConfigurationParser.parse(emptyBrands.byteInputStream())

        assertEquals(emptyList<CardBrand>(), cardConfiguration.brands)
    }

    @Test
    fun givenAValidConfigShouldReturnACompleteCardConfiguration() {
        val validCardConfiguration = """
            {
                "defaults": {
                    "pan": {
                        "matcher": "^\\d{0,19}${'$'}",
                        "minLength": 13,
                        "maxLength": 19
                    },
                    "cvv": {
                        "matcher": "^\\d{0,4}${'$'}",
                        "minLength": 3,
                        "maxLength": 4
                    },
                    "month": {
                        "matcher": "^0[1-9]{0,1}${'$'}|^1[0-2]{0,1}${'$'}",
                        "minLength": 2,
                        "maxLength": 2
                    },
                    "year": {
                        "matcher": "^\\d{0,2}${'$'}",
                        "minLength": 2,
                        "maxLength": 2
                    }
                },
                "brands": [
                    {
                        "name": "visa",
                        "image": "card_visa_logo",
                        "images": [
                            {
                                "type": "image/png",
                                "url": "http://localhost/visa.png"
                            },
                            {
                                "type": "image/svg+xml",
                                "url": "http://localhost/visa.svg"
                            }
                        ],
                        "cvv": {
                            "matcher": "^\\d{0,3}${'$'}",
                            "validLength": 3
                        },
                        "pans": [
                            {
                                "matcher": "^4\\d{0,15}",
                                "validLength": 16,
                                "subRules": [
                                    {
                                        "matcher": "^(413600|444509|444550|450603|450617|450628|450636|450640|450662|463100|476142|476143|492901|492920|492923|492928|492937|492939|492960)\\d{0,7}",
                                        "validLength": 13
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "name": "mastercard",
                        "image": "card_mastercard_logo",
                        "images": [
                            {
                                "type": "image/png",
                                "url": "http://localhost/mastercard.png"
                            },
                            {
                                "type": "image/svg+xml",
                                "url": "http://localhost/mastercard.svg"
                            }
                        ],
                        "cvv": {
                            "matcher": "^\\d{0,3}${'$'}",
                            "validLength": 3
                        },
                        "pans": [
                            {
                                "matcher": "^2[27]\\d{0,14}${'$'}",
                                "validLength": 16
                            },
                            {
                                "matcher": "^5\\d{0,15}${'$'}",
                                "validLength": 16
                            },
                            {
                                "matcher": "^67\\d{0,14}${'$'}",
                                "validLength": 16
                            }
                        ]
                    },
                    {
                        "name": "amex",
                        "image": "card_amex_logo",
                        "images": [
                            {
                                "type": "image/png",
                                "url": "http://localhost/amex.png"
                            },
                            {
                                "type": "image/svg+xml",
                                "url": "http://localhost/amex.svg"
                            }
                        ],
                        "cvv": {
                            "matcher": "^\\d{0,4}${'$'}",
                            "validLength": 4
                        },
                        "pans": [
                            {
                                "matcher": "^3[47]\\d{0,13}${'$'}",
                                "validLength": 15
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()

        val cardConfiguration =
            cardConfigurationParser.parse(validCardConfiguration.byteInputStream())

        assertNotNull(cardConfiguration.defaults?.pan)
        assertNotNull(cardConfiguration.defaults?.cvv)
        assertNotNull(cardConfiguration.defaults?.month)
        assertNotNull(cardConfiguration.defaults?.year)

        assertEquals("^\\d{0,19}$", cardConfiguration.defaults?.pan?.matcher)
        assertEquals("^\\d{0,4}$", cardConfiguration.defaults?.cvv?.matcher)
        assertEquals("^0[1-9]{0,1}$|^1[0-2]{0,1}$", cardConfiguration.defaults?.month?.matcher)
        assertEquals("^\\d{0,2}$", cardConfiguration.defaults?.year?.matcher)

        assertEquals(13, cardConfiguration.defaults?.pan?.minLength)
        assertEquals(3, cardConfiguration.defaults?.cvv?.minLength)
        assertEquals(2, cardConfiguration.defaults?.month?.minLength)
        assertEquals(2, cardConfiguration.defaults?.year?.minLength)

        assertEquals(19, cardConfiguration.defaults?.pan?.maxLength)
        assertEquals(4, cardConfiguration.defaults?.cvv?.maxLength)
        assertEquals(2, cardConfiguration.defaults?.month?.maxLength)
        assertEquals(2, cardConfiguration.defaults?.year?.maxLength)

        assertEquals(3, cardConfiguration.brands?.size)

        val visa = cardConfiguration.brands?.get(0)
        assertEquals("visa", visa!!.name)

        assertEquals(2, visa.images?.size)
        val visaCardBrandImage1 = visa.images!![0]
        assertEquals("image/png", visaCardBrandImage1.type)
        assertEquals("http://localhost/visa.png", visaCardBrandImage1.url)
        val visaCardBrandImage2 = visa.images!![1]
        assertEquals("image/svg+xml", visaCardBrandImage2.type)
        assertEquals("http://localhost/visa.svg", visaCardBrandImage2.url)

        assertEquals("^\\d{0,3}$", visa.cvv?.matcher)
        assertEquals(3, visa.cvv?.validLength)
        assertEquals(1, visa.pans.size)

        val visaPan = visa.pans[0]
        assertEquals("^4\\d{0,15}", visaPan.matcher)
        assertEquals(16, visaPan.validLength)

        assertEquals(1, visaPan.subRules.size)
        val visaSubRule = visaPan.subRules[0]
        assertEquals(
            "^(413600|444509|444550|450603|450617|450628|450636|450640|450662|463100|476142|476143|492901|492920|492923|492928|492937|492939|492960)\\d{0,7}",
            visaSubRule.matcher
        )
        assertEquals(13, visaSubRule.validLength)

        val mastercard = cardConfiguration.brands?.get(1)
        assertEquals("mastercard", mastercard!!.name)

        assertEquals(2, mastercard.images?.size)
        val mastercardCardBrandImage1 = mastercard.images!![0]
        assertEquals("image/png", mastercardCardBrandImage1.type)
        assertEquals("http://localhost/mastercard.png", mastercardCardBrandImage1.url)
        val mastercardCardBrandImage2 = mastercard.images!![1]
        assertEquals("image/svg+xml", mastercardCardBrandImage2.type)
        assertEquals("http://localhost/mastercard.svg", mastercardCardBrandImage2.url)

        assertEquals("^\\d{0,3}$", mastercard.cvv?.matcher)
        assertEquals(3, mastercard.cvv?.validLength)
        assertEquals(3, mastercard.pans.size)

        val mastercardPan1 = mastercard.pans[0]
        assertEquals("^2[27]\\d{0,14}$", mastercardPan1.matcher)
        assertEquals(16, mastercardPan1.validLength)

        val mastercardPan2 = mastercard.pans[1]
        assertEquals("^5\\d{0,15}$", mastercardPan2.matcher)
        assertEquals(16, mastercardPan2.validLength)

        val mastercardPan3 = mastercard.pans[2]
        assertEquals("^67\\d{0,14}$", mastercardPan3.matcher)
        assertEquals(16, mastercardPan3.validLength)

        val amex = cardConfiguration.brands?.get(2)
        assertEquals("amex", amex!!.name)

        assertEquals(2, amex.images?.size)
        val amexCardBrandImage1 = amex.images!![0]
        assertEquals("image/png", amexCardBrandImage1.type)
        assertEquals("http://localhost/amex.png", amexCardBrandImage1.url)
        val amexCardBrandImage2 = amex.images!![1]
        assertEquals("image/svg+xml", amexCardBrandImage2.type)
        assertEquals("http://localhost/amex.svg", amexCardBrandImage2.url)

        assertEquals("^\\d{0,4}$", amex.cvv?.matcher)
        assertEquals(4, amex.cvv?.validLength)
        assertEquals(1, amex.pans.size)

        val amexPan = amex.pans[0]
        assertEquals("^3[47]\\d{0,13}$", amexPan.matcher)
        assertEquals(15, amexPan.validLength)
    }
}
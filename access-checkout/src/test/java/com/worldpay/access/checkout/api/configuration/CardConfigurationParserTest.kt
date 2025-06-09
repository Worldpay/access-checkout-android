package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.testutils.CardConfigurationUtil.BASE_PATH
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.CARD_DEFAULTS
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.CVC_RULE
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.MATCHER
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.PAN_RULE
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CardConfigurationParserTest {

    private lateinit var cardConfigurationParser: CardConfigurationParser

    private val validCardConfiguration: String =
        CardConfigurationParserTest::class.java.getResource("remote_card_config.json")?.readText()!!

    @Before
    fun setUp() {
        cardConfigurationParser = CardConfigurationParser()
    }

    @Test
    fun `should return default card configuration where json string is blank`() {
        val expected = CardConfiguration(emptyList(), DefaultCardRules.CARD_DEFAULTS)
        assertEquals(expected, cardConfigurationParser.deserialize(""))
    }

    @Test
    fun `should return default card configuration where json string is empty array`() {
        val expected = CardConfiguration(emptyList(), DefaultCardRules.CARD_DEFAULTS)
        assertEquals(expected, cardConfigurationParser.deserialize("[]"))
    }

    @Test
    fun `should return default card configuration where json string starts with object notation`() {
        val expected = CardConfiguration(emptyList(), DefaultCardRules.CARD_DEFAULTS)
        assertEquals(expected, cardConfigurationParser.deserialize("{"))
        assertEquals(expected, cardConfigurationParser.deserialize("{]"))
        assertEquals(expected, cardConfigurationParser.deserialize("{}"))
        assertEquals(expected, cardConfigurationParser.deserialize("{\""))
    }

    @Test
    fun `should return default card configuration where json string is a not json`() {
        val expected = CardConfiguration(emptyList(), DefaultCardRules.CARD_DEFAULTS)
        assertEquals(expected, cardConfigurationParser.deserialize("abc"))
    }

    @Test
    fun `should return default card configuration where json string is a multi dimensional array`() {
        val expected = CardConfiguration(emptyList(), DefaultCardRules.CARD_DEFAULTS)
        assertEquals(expected, cardConfigurationParser.deserialize("[[]]"))
    }

    @Test
    fun `should be able to parse a single brand`() {
        val json = """
            [
                {
                    "name": "visa",
                    "pattern": "^(?!^493698\\d*${'$'})4\\d*${'$'}",
                    "panLengths": [
                        16,
                        18,
                        19
                    ],
                    "cvvLength": 3,
                    "images": [
                        {
                            "type": "image/png",
                            "url": "https://example.com/access-checkout/assets/visa.png"
                        },
                        {
                            "type": "image/svg+xml",
                            "url": "https://example.com/access-checkout/assets/visa.svg"
                        }
                    ]
                }
            ]
        """.trimIndent()

        val result = cardConfigurationParser.deserialize(json)

        assertEquals("visa", result.brands[0].name)

        assertEquals("^(?!^493698\\d*${'$'})4\\d*${'$'}", result.brands[0].pan.matcher)
        assertEquals(listOf(16, 18, 19), result.brands[0].pan.validLengths)

        assertEquals(MATCHER, result.brands[0].cvc.matcher)
        assertEquals(listOf(3), result.brands[0].cvc.validLengths)

        assertEquals("image/png", result.brands[0].images[0].type)
        assertEquals("https://example.com/access-checkout/assets/visa.png", result.brands[0].images[0].url)
        assertEquals("image/svg+xml", result.brands[0].images[1].type)
        assertEquals("https://example.com/access-checkout/assets/visa.svg", result.brands[0].images[1].url)
    }

    @Test
    fun `should be able to parse a multiple brands`() {
        assertEquals(CARD_CONFIG_BASIC, cardConfigurationParser.deserialize(validCardConfiguration))
    }

    @Test
    fun `should return empty brand name where json brand has missing 'name' property`() {
        val json = """
            [{
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": [15],
                "cvvLength": 4,
                "images": [
                     { "type": "image/png", "url": "$BASE_PATH/amex.png" },
                     { "type": "image/svg+xml", "url": "$BASE_PATH/amex.svg" }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("image/png", "$BASE_PATH/amex.png"),
            RemoteCardBrandImage("image/svg+xml", "$BASE_PATH/amex.svg")
        )

        val expectedBrand = RemoteCardBrand(
            name = "",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("^3[47]\\d*${'$'}", listOf(15))
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should use default matcher pattern when json brand has missing 'pattern' property`() {
        val json = """
            [{
                "name": "amex",
                "panLengths": [15],
                "cvvLength": 4,
                "images": [
                    { "type": "image/png", "url": "$BASE_PATH/amex.png" },
                    { "type": "image/svg+xml", "url": "$BASE_PATH/amex.svg" }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("image/png", "$BASE_PATH/amex.png"),
            RemoteCardBrandImage("image/svg+xml", "$BASE_PATH/amex.svg")
        )

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule(MATCHER, listOf(15))
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should not use default matcher pattern when json brand has empty 'pattern' property`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "",
                "panLengths": [15],
                "cvvLength": 4,
                "images": [
                    { "type": "image/png", "url": "$BASE_PATH/amex.png" },
                    { "type": "image/svg+xml", "url": "$BASE_PATH/amex.svg" }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("image/png", "$BASE_PATH/amex.png"),
            RemoteCardBrandImage("image/svg+xml", "$BASE_PATH/amex.svg")
        )

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("", listOf(15))
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should use default pan lengths when json brand has missing 'panLengths' property`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "^3[47]\\d*${'$'}",
                "cvvLength": 4,
                "images": [
                     { "type": "image/png", "url": "$BASE_PATH/amex.png" },
                     { "type": "image/svg+xml", "url": "$BASE_PATH/amex.svg" }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("image/png", "$BASE_PATH/amex.png"),
            RemoteCardBrandImage("image/svg+xml", "$BASE_PATH/amex.svg")
        )

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("^3[47]\\d*\$", CARD_DEFAULTS.pan.validLengths)
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should not use default pan lengths when json brand has empty 'panLengths' property`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": [],
                "cvvLength": 4,
                "images": [
                     { "type": "image/png", "url": "$BASE_PATH/amex.png" },
                     { "type": "image/svg+xml", "url": "$BASE_PATH/amex.svg" }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("image/png", "$BASE_PATH/amex.png"),
            RemoteCardBrandImage("image/svg+xml", "$BASE_PATH/amex.svg")
        )

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("^3[47]\\d*\$", emptyList())
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should use default cvc lengths when json brand has missing 'cvvLength' property`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": [15],
                "images": [
                     { "type": "image/png", "url": "$BASE_PATH/amex.png" },
                     { "type": "image/svg+xml", "url": "$BASE_PATH/amex.svg" }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("image/png", "$BASE_PATH/amex.png"),
            RemoteCardBrandImage("image/svg+xml", "$BASE_PATH/amex.svg")
        )

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, CARD_DEFAULTS.cvc.validLengths),
            pan = CardValidationRule("^3[47]\\d*\$", listOf(15))
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should have empty list when json brand has missing 'images' property`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": [15],
                "cvvLength": 4
            }]
        """.trimIndent()

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = emptyList(),
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("^3[47]\\d*\$", listOf(15))
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should have empty list when json brand has empty 'images' property`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": [15],
                "cvvLength": 4,
                "images": []
            }]
        """.trimIndent()

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = emptyList(),
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("^3[47]\\d*\$", listOf(15))
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should empty brand image type when json brand image has missing 'type' property`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": [12, 13, 14, 15, 16, 17, 18, 19],
                "cvvLength": 4,
                "images": [
                     { "url": "$BASE_PATH/amex.png" }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("", "$BASE_PATH/amex.png")
        )

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("^3[47]\\d*\$", PAN_RULE.validLengths)
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should have empty type value when json brand image has empty 'type' property`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": [15],
                "cvvLength": 4,
                "images": [
                     { "type": "", "url": "$BASE_PATH/amex.png" }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("", "$BASE_PATH/amex.png")
        )

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("^3[47]\\d*\$", listOf(15))
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should have empty brand image url when json brand image has missing 'url' property`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": [12, 13, 14, 15, 16, 17, 18, 19],
                "cvvLength": 4,
                "images": [
                     { "type": "image/png" }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("image/png", "")
        )

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("^3[47]\\d*\$", PAN_RULE.validLengths)
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should have empty url value when json brand image has empty 'url' property`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": [15],
                "cvvLength": 4,
                "images": [
                     { "type": "image/png", "url": "" }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("image/png", "")
        )

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("^3[47]\\d*\$", listOf(15))
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should have empty brand name when string property value is an int - 'name' property`() {
        val json = """
            [{
                "name": 0,
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": [12, 13, 14, 15, 16, 17, 18, 19],
                "cvvLength": 4,
                "images": [
                     { "type": "image/png", "url": "$BASE_PATH/amex.png" }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("image/png", "$BASE_PATH/amex.png")
        )

        val expectedBrand = RemoteCardBrand(
            name = "",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("^3[47]\\d*${'$'}", PAN_RULE.validLengths)
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should have default pattern when string property value is an int - "pattern" property`() {
        val json = """
            [{
                "name": "amex",
                "pattern": 0,
                "panLengths": [12, 13, 14, 15, 16, 17, 18, 19],
                "cvvLength": 4,
                "images": [
                     { "type": "image/png", "url": "$BASE_PATH/amex.png" }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("image/png", "$BASE_PATH/amex.png")
        )

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule(MATCHER, PAN_RULE.validLengths)
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should ignore value passed where 'panLengths' array property value is an object and use the default value`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": {},
                "cvvLength": 4,
                "images": [
                     { "type": "image/png", "url": "$BASE_PATH/amex.png" },
                     { "type": "image/svg+xml", "url": "$BASE_PATH/amex.svg" }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("image/png", "$BASE_PATH/amex.png"),
            RemoteCardBrandImage("image/svg+xml", "$BASE_PATH/amex.svg")
        )

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("^3[47]\\d*\$", PAN_RULE.validLengths)
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should use default pan length when int array property value is an array of strings - 'panLengths' property`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": ["should be an int not a string"],
                "cvvLength": 4,
                "images": [
                     { "type": "image/png", "url": "$BASE_PATH/amex.png" }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("image/png", "$BASE_PATH/amex.png")
        )

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("^3[47]\\d*\$", PAN_RULE.validLengths)
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should have default cvc length when int property value is a string - 'cvvLength' property`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": [12, 13, 14, 15, 16, 17, 18, 19],
                "cvvLength": "should be an int not a string",
                "images": [
                     { "type": "image/png", "url": "$BASE_PATH/amex.png" }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("image/png", "$BASE_PATH/amex.png")
        )

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, CVC_RULE.validLengths),
            pan = CardValidationRule("^3[47]\\d*\$", PAN_RULE.validLengths)
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should have empty list where 'images' array property value is an object`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": [15],
                "cvvLength": 4,
                "images": {}
            }]
        """.trimIndent()

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = emptyList(),
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("^3[47]\\d*${'$'}", listOf(15))
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))

        cardConfigurationParser.deserialize(json)
    }

    @Test
    fun `should have empty list where 'images' array property value is an multi dimensional array`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": [15],
                "cvvLength": 4,
                "images": [[]]
            }]
        """.trimIndent()

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = emptyList(),
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("^3[47]\\d*${'$'}", listOf(15))
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))

        cardConfigurationParser.deserialize(json)
    }

    @Test
    fun `should have empty brand image type when string property value is an int - 'image type' property`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": [12, 13, 14, 15, 16, 17, 18, 19],
                "cvvLength": 4,
                "images": [
                     { "type": 0, "url": "$BASE_PATH/amex.png" }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("", "$BASE_PATH/amex.png")
        )

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("^3[47]\\d*\$", PAN_RULE.validLengths)
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }

    @Test
    fun `should have empty brand image url when string property value is an int - 'image url' property`() {
        val json = """
            [{
                "name": "amex",
                "pattern": "^3[47]\\d*${'$'}",
                "panLengths": [12, 13, 14, 15, 16, 17, 18, 19],
                "cvvLength": 4,
                "images": [
                     { "type": "image/png", "url": 0 }
                ]
            }]
        """.trimIndent()

        val brandImages = listOf(
            RemoteCardBrandImage("image/png", "")
        )

        val expectedBrand = RemoteCardBrand(
            name = "amex",
            images = brandImages,
            cvc = CardValidationRule(MATCHER, listOf(4)),
            pan = CardValidationRule("^3[47]\\d*\$", PAN_RULE.validLengths)
        )

        val expectedConfig = CardConfiguration(
            brands = listOf(expectedBrand),
            defaults = CARD_DEFAULTS
        )

        assertEquals(expectedConfig, cardConfigurationParser.deserialize(json))
    }
}

package com.worldpay.access.checkout.config

import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDeserializationException
import com.worldpay.access.checkout.model.CardConfiguration
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
class CardConfigurationParserTest {

    private lateinit var cardConfigurationParser: CardConfigurationParser

    private val context = ShadowInstrumentation.getInstrumentation().targetContext.applicationContext

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
    fun givenAnEmptyFileShouldReturnAnEmptyCardConfiguration() {
        val emptyFile = getTestFile("emptyFile.json")

        assertEquals(CardConfiguration(), cardConfigurationParser.parse(emptyFile))
    }

    @Test
    fun givenAMalformedJsonInputShouldThrowAccessCheckoutDeserializationError() {
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Cannot interpret json: ABC")

        cardConfigurationParser.parse("ABC".byteInputStream())
    }

    @Test
    fun givenJsonWithMissingOptionalPropertiesShouldNotThrowAccessCheckoutDeserializationError() {
        val cardConfiguration =
            cardConfigurationParser.parse(getTestFile("card_configuration_with_optional_fields_missing.json"))

        assertEquals(1, cardConfiguration.brands!!.size)
        assertEquals(1, cardConfiguration.brands!![0].pans.size)
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

        cardConfigurationParser.parse(getTestFile("card_configuration_with_required_fields_missing.json"))
    }

    @Test
    fun givenJsonWithWrongTypeForStringPropertyThenShouldThrowAccessCheckoutDeserializationError() {
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Invalid property type: 'matcher', expected 'String'")

        cardConfigurationParser.parse(getTestFile("card_configuration_with_wrong_property_type_for_string.json"))
    }

    @Test
    fun givenJsonWithWrongTypeForIntPropertyThenShouldThrowAccessCheckoutDeserializationError() {
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Invalid property type: 'maxLength', expected 'Int'")

        cardConfigurationParser.parse(getTestFile("card_configuration_with_wrong_property_type_for_int.json"))
    }

    @Test
    fun givenANonEmptyConfigFileShouldReturnACompleteCardConfiguration() {
        val cardConfiguration =
            cardConfigurationParser.parse(getTestFile("card_configuration_file.json"))

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
        assertEquals("card_visa_logo", visa.image)
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
        assertEquals("card_mastercard_logo", mastercard.image)
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
        assertEquals("card_amex_logo", amex.image)
        assertEquals("^\\d{0,4}$", amex.cvv?.matcher)
        assertEquals(4, amex.cvv?.validLength)
        assertEquals(1, amex.pans.size)

        val amexPan = amex.pans[0]
        assertEquals("^3[47]\\d{0,13}$", amexPan.matcher)
        assertEquals(15, amexPan.validLength)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun getTestFile(fileName: String) = javaClass.classLoader.getResourceAsStream(fileName)
}
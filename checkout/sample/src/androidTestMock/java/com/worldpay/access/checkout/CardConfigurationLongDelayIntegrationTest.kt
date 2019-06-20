package com.worldpay.access.checkout

import android.support.test.rule.ActivityTestRule
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.worldpay.access.checkout.MockServer.stubCardConfiguration
import com.worldpay.access.checkout.MockServer.stubCardConfigurationWithDelay
import com.worldpay.access.checkout.UITestUtils.assertDisplaysResponseFromServer
import com.worldpay.access.checkout.UITestUtils.assertInProgressState
import com.worldpay.access.checkout.UITestUtils.closeKeyboard
import com.worldpay.access.checkout.UITestUtils.getFailColor
import com.worldpay.access.checkout.UITestUtils.getSuccessColor
import com.worldpay.access.checkout.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.matchers.BrandVectorImageMatcher
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.model.CardDefaults
import com.worldpay.access.checkout.model.CardValidationRule
import com.worldpay.access.checkout.views.resIdByName
import org.awaitility.Awaitility
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CardConfigurationLongDelayIntegrationTest {

    private val timeoutInMillis = 10000L

    private val luhnValidMastercardCard = "5555555555554444"
    private val luhnInvalidMastercardCard = "55555555555111"
    private val unknownCvv = "123456"
    private val month = "12"
    private val year = "99"

    private val mastercardCvvValidationRule: CardValidationRule = CardValidationRule("^\\d{0,3}$", null, null, 3)
    private val mastercardPANValidationRule: CardValidationRule = CardValidationRule("^5\\d{0,15}$", null, null, 16)
    private val brands = listOf(
        CardBrand(
            "mastercard",
            "card_mastercard_logo",
            mastercardCvvValidationRule,
            listOf(mastercardPANValidationRule)
        )
    )
    private val defaults = CardDefaults(null, null, null, null)
    private val cardConfiguration = CardConfiguration(brands, defaults)

    @get:Rule
    var cardConfigurationRule: CardConfigurationLongDelayRule =
        CardConfigurationLongDelayRule(cardConfiguration, timeoutInMillis, MainActivity::class.java)


    private lateinit var activity: MainActivity
    
    @Before
    fun setup() {
        activity = cardConfigurationRule.activity
    }

    @After
    fun tearDown() {
        stubCardConfiguration(activity)
    }

    @Test
    fun givenCardConfigurationCallIsDelayed_AndValidKnownCardDataIsInsertedAndUserPressesSubmit_ThenSuccessfulResponseIsReceived() {

        val cardText = uiObjectWithId(R.id.card_number_edit_text)
        val cvvText = uiObjectWithId(R.id.cardCVVText)
        val monthText = uiObjectWithId(R.id.month_edit_text)
        val yearText = uiObjectWithId(R.id.year_edit_text)
        val submitButton = uiObjectWithId(R.id.submit)

        val cardEditText: EditText = activity.findViewById(R.id.card_number_edit_text)
        val cvvEditText: EditText = activity.findViewById(R.id.cardCVVText)
        val monthEditText: EditText = activity.findViewById(R.id.month_edit_text)
        val yearEditText: EditText = activity.findViewById(R.id.year_edit_text)
        val submit: Button = activity.findViewById(R.id.submit)

        assertExpectedLogo("card_unknown_logo")

        cardText.text = luhnInvalidMastercardCard
        cvvText.click()
        cvvText.text = unknownCvv
        monthText.click()
        monthText.text = "13"
        yearText.click()
        yearText.text = year

        val failColor = getFailColor(activity)
        val successColor = getSuccessColor(activity)

        // Assert that with no configuration that the very basic validation is done on luhn of the card
        // and the date check
        assertEquals(failColor, cardEditText.currentTextColor)
        assertEquals(failColor, monthEditText.currentTextColor)
        assertEquals(failColor, yearEditText.currentTextColor)
        assertEquals(successColor, cvvEditText.currentTextColor)
        assertFalse(submit.isEnabled)

        // Re-verify that the card still cannot be identified (as no card configuration yet)
        assertExpectedLogo("card_unknown_logo")

        // Wait until the server replies so we can successfully identify the card as mastercard based on current input
        Awaitility.await().atMost(timeoutInMillis, MILLISECONDS).until {
            try {
                assertExpectedLogo("card_mastercard_logo")
                true
            } catch (ex: AssertionError) {
                // trigger an action on the UI
                cardText.click()
                cvvText.click()
                false
            }
        }

        // Assert that with now configuration has come back that the CVV is invalid for mastercard
        assertEquals(failColor, cardEditText.currentTextColor)
        assertEquals(failColor, monthEditText.currentTextColor)
        assertEquals(failColor, yearEditText.currentTextColor)
        assertEquals(failColor, cvvEditText.currentTextColor)
        assertFalse(submit.isEnabled)

        // Re-enter a luhn valid, mastercard identified card and valid date
        cardText.text = luhnValidMastercardCard
        monthText.click()
        monthText.text = month
        cvvText.click()

        // Attempt to type more than allowed input for CVV
        val validCVV = "123"
        cvvText.text = validCVV
        assertEquals(successColor, cvvEditText.currentTextColor)
        cvvText.text = "12345"
        assertEquals(validCVV, cvvEditText.text.toString())

        // Verify that all the fields are now in a success state and can be submitted
        closeKeyboard()
        assertEquals(successColor, cardEditText.currentTextColor)
        assertEquals(successColor, monthEditText.currentTextColor)
        assertEquals(successColor, yearEditText.currentTextColor)
        assertEquals(successColor, cvvEditText.currentTextColor)

        assertTrue(submitButton.isEnabled)

        submitButton.click()

        assertInProgressState()

        assertDisplaysResponseFromServer(activity.getString(R.string.session_reference), activity.window.decorView)
    }
    
    private fun assertExpectedLogo(logoResName: String) {
        val logoView = activity.findViewById<ImageView>(R.id.logo_view)
        val expectedLogoResourceId = activity.resIdByName(logoResName, "drawable")
        assertTrue { BrandVectorImageMatcher.equalsBrandVectorImage(logoView, expectedLogoResourceId) }
    }
}

class CardConfigurationLongDelayRule(private val cardConfiguration: CardConfiguration, private val timeoutMillis: Long,
                                     activityClass: Class<MainActivity>) : ActivityTestRule<MainActivity>(activityClass) {

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        // This card configuration rule adds stubs to mockserver to simulate a long delay condition on the card configuration endpoint.
        // On initialisation of our SDK, the SDK will trigger a card configuration call which will get back this delayed
        // response.
        stubCardConfigurationWithDelay(cardConfiguration, timeoutMillis.toInt())
    }
}
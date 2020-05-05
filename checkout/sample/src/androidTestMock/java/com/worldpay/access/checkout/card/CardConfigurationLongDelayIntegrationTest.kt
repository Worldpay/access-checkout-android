package com.worldpay.access.checkout.card

import android.widget.ImageView
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.CardConfigurationMockStub.stubCardConfiguration
import com.worldpay.access.checkout.CardConfigurationMockStub.stubCardConfigurationWithDelay
import com.worldpay.access.checkout.MainActivity
import com.worldpay.access.checkout.MockServer
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.card.testutil.CardBrand.MASTERCARD
import com.worldpay.access.checkout.card.testutil.CardFragmentTestUtils
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
import com.worldpay.access.checkout.model.*
import com.worldpay.access.checkout.testutil.UITestUtils.assertDisplaysResponseFromServer
import com.worldpay.access.checkout.testutil.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.views.PANLayout
import org.awaitility.Awaitility
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.test.assertEquals

class CardConfigurationLongDelayIntegrationTest {

    private val timeoutInMillis = 10000L

    @get:Rule
    var cardConfigRule = CardConfigurationLongDelayRule(timeoutInMillis, MainActivity::class.java)

    private lateinit var cardFragmentTestUtils: CardFragmentTestUtils

    private val luhnValidMastercardCard = "5555555555554444"
    private val luhnInvalidMastercardCard = "55555555555111"
    private val unknownCvv = "123456"
    private val month = "12"
    private val year = "99"

    @Before
    fun setup() {
        cardFragmentTestUtils = CardFragmentTestUtils(cardConfigRule)
    }

    @After
    fun tearDown() {
        stubCardConfiguration(cardConfigRule.activity)
    }

    @Test
    fun givenCardConfigurationCallIsDelayed_AndValidKnownCardDataIsInsertedAndUserPressesSubmit_ThenSuccessfulResponseIsReceived() {
        // Assert that with no configuration that the very basic validation is done on luhn of the card
        // and the date check and that the card still cannot be verified (no card configuration)
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = luhnInvalidMastercardCard, cvv = unknownCvv, month = "13", year = year)
            .validationStateIs(pan = false, cvv = true, month = false, year = false)
            .enabledStateIs(submitButton = false)
            .hasNoBrand()

        // Wait until the server replies so we can successfully identify the card as mastercard based on current input
        Awaitility.await().atMost(timeoutInMillis, MILLISECONDS).until {
            try {
                assertExpectedLogo(MASTERCARD.cardBrandName)
                true
            } catch (ex: AssertionError) {
                // trigger an action on the UI
                uiObjectWithId(R.id.card_number_edit_text).click()
                uiObjectWithId(R.id.card_flow_text_cvv).click()
                false
            }
        }

        // Assert that with now configuration has come back that the CVV is invalid for mastercard
        cardFragmentTestUtils
            .validationStateIs(pan = false, cvv = false, month = false, year = false)
            .enabledStateIs(submitButton = false)

        // Re-enter a luhn valid, mastercard identified card and valid date and submit
        cardFragmentTestUtils
            .enterCardDetails(pan = luhnValidMastercardCard, cvv = "123", month = month)
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enterCardDetails(cvv = "12345")
            .cardDetailsAre(cvv = "123")
            .validationStateIs(pan = true, cvv = true, month = true, year = true)
            .enabledStateIs(submitButton = true)
            .clickSubmitButton()
            .requestIsInProgress()

        assertDisplaysResponseFromServer(
            mapOf(VERIFIED_TOKEN_SESSION to cardConfigRule.activity.getString(R.string.verified_token_session_reference)).toString(),
            cardConfigRule.activity.window.decorView
        )
    }
    
    private fun assertExpectedLogo(logoResName: String) {
        val logoView = cardConfigRule.activity.findViewById<ImageView>(R.id.logo_view)
        assertEquals(logoResName, logoView.getTag(PANLayout.CARD_TAG))
    }

}

class CardConfigurationLongDelayRule(private val timeoutMillis: Long,
                                     activityClass: Class<MainActivity>) : ActivityTestRule<MainActivity>(activityClass) {

    private val mastercardCvvValidationRule: CardValidationRule = CardValidationRule("^\\d{0,3}$", null, null, 3)
    private val mastercardPANValidationRule: CardValidationRule = CardValidationRule("^5\\d{0,15}$", null, null, 16)

    private val brands = listOf(
        CardBrand(
            "mastercard",
            listOf(CardBrandImage("image/svg+xml", "${MockServer.getBaseUrl()}/access-checkout/assets/mastercard.svg")),
            mastercardCvvValidationRule,
            listOf(mastercardPANValidationRule)
        )
    )

    private val defaults = CardDefaults(null, null, null, null)

    private val cardConfiguration = CardConfiguration(brands, defaults)

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        // This card configuration rule adds stubs to mock server to simulate a long delay condition on the card configuration endpoint.
        // On initialisation of our SDK, the SDK will trigger a card configuration call which will get back this delayed response.
        stubCardConfigurationWithDelay(cardConfiguration, timeoutMillis.toInt())
    }

}

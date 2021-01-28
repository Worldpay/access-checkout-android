package com.worldpay.access.checkout.sample.card

import android.widget.ImageView
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.MASTERCARD
import com.worldpay.access.checkout.sample.card.standard.testutil.CardFragmentTestUtils
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.stubCardConfiguration
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.stubCardConfigurationWithDelay
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
            .enterCardDetails(pan = luhnInvalidMastercardCard, cvc = "1234", expiryDate = "0199")
            .validationStateIs(pan = false, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = false)
            .hasNoBrand()

        // Wait until the server replies so we can successfully identify the card as mastercard based on current input
         Awaitility.await().atMost(timeoutInMillis, MILLISECONDS).until {
            try {
                assertExpectedLogo(MASTERCARD.cardBrandName)
                true
            } catch (ex: AssertionError) {
                false
            }
        }

        // Assert that with now configuration has come back that the CVC is invalid for mastercard
        cardFragmentTestUtils
            .cardDetailsAre(pan = luhnInvalidMastercardCard, cvc = "123", expiryDate = "01/99")
            .validationStateIs(pan = false, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = false)

        // Re-enter a luhn valid, mastercard identified card and valid date and submit
        cardFragmentTestUtils
            .enterCardDetails(pan = luhnValidMastercardCard, expiryDate = "1299")
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enterCardDetails(cvc = "12345")
            .cardDetailsAre(cvc = "123")
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(CARD to cardConfigRule.activity.getString(R.string.verified_token_session_reference)).toString()
            )
    }
    
    private fun assertExpectedLogo(logoResName: String) {
        val logoView = cardConfigRule.activity.findViewById<ImageView>(R.id.card_flow_brand_logo)
        assertEquals(logoResName, logoView.getTag(R.integer.card_tag))
    }

}

class CardConfigurationLongDelayRule(private val timeoutMillis: Long,
                                     activityClass: Class<MainActivity>) : ActivityTestRule<MainActivity>(activityClass) {

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        // This card configuration rule adds stubs to mock server to simulate a long delay condition on the card configuration endpoint.
        // On initialisation of our SDK, the SDK will trigger a card configuration call which will get back this delayed response.
        stubCardConfigurationWithDelay(timeoutMillis.toInt())
    }

}

package com.worldpay.access.checkout

import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.MockServer.simulateCardConfigurationServerError
import com.worldpay.access.checkout.MockServer.stubCardConfiguration
import com.worldpay.access.checkout.UITestUtils.assertDisplaysResponseFromServer
import com.worldpay.access.checkout.UITestUtils.checkFieldInState
import com.worldpay.access.checkout.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.card.CardFragmentTestUtils.assertBrandImage
import com.worldpay.access.checkout.card.CardFragmentTestUtils.assertFieldsAlpha
import com.worldpay.access.checkout.card.CardFragmentTestUtils.assertInProgressState
import com.worldpay.access.checkout.card.CardFragmentTestUtils.assertValidInitialUIFields
import com.worldpay.access.checkout.card.CardFragmentTestUtils.cardNumberMatcher
import com.worldpay.access.checkout.card.CardFragmentTestUtils.checkSubmitInState
import com.worldpay.access.checkout.card.CardFragmentTestUtils.monthMatcher
import com.worldpay.access.checkout.card.CardFragmentTestUtils.typeFormInputs
import com.worldpay.access.checkout.card.CardFragmentTestUtils.updateMonthDetails
import com.worldpay.access.checkout.card.CardFragmentTestUtils.updatePANDetails
import com.worldpay.access.checkout.card.CardFragmentTestUtils.yearMatcher
import org.junit.After
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

class CardConfigurationIntegrationTest {

    private val luhnValidUnknownCard = "000000"
    private val luhnValidMastercardCard = "5555555555554444"
    private val luhnInvalidUnknownCard = "111234"
    private val unknownCvv = "123456"
    private val month = "12"
    private val year = "99"

    @get:Rule
    var cardConfigurationErrorRule: CardConfigurationErrorRule =
        CardConfigurationErrorRule(MainActivity::class.java)

    @After
    fun tearDown() {
        stubCardConfiguration(cardConfigurationErrorRule.activity)
    }

    @Test
    fun givenCardConfigurationCallFails_AndValidUnknownCardDataIsInsertedAndUserPressesSubmit_ThenSuccessfulResponseIsReceived() {
        assertValidInitialUIFields()

        typeFormInputs(luhnInvalidUnknownCard, unknownCvv, "13", year, true)

        assertBrandImage(R.drawable.card_unknown_logo)
        checkFieldInState(false, cardNumberMatcher, cardConfigurationErrorRule.activity)
        checkFieldInState(false, monthMatcher, cardConfigurationErrorRule.activity)
        checkFieldInState(false, yearMatcher, cardConfigurationErrorRule.activity)
        checkSubmitInState(false)

        updatePANDetails(luhnValidUnknownCard)
        assertBrandImage(R.drawable.card_unknown_logo)
        checkFieldInState(true, cardNumberMatcher, cardConfigurationErrorRule.activity)
        updateMonthDetails(month)

        updatePANDetails(luhnValidMastercardCard)
        assertBrandImage(R.drawable.card_unknown_logo)
        checkFieldInState(true, cardNumberMatcher, cardConfigurationErrorRule.activity)

        checkFieldInState(true, cardNumberMatcher, cardConfigurationErrorRule.activity)
        checkFieldInState(true, monthMatcher, cardConfigurationErrorRule.activity)
        checkFieldInState(true, yearMatcher, cardConfigurationErrorRule.activity)

        checkSubmitInState(true)

        assertFieldsAlpha(1.0f)
        assertTrue(uiObjectWithId(R.id.card_flow_btn_submit).exists())
        uiObjectWithId(R.id.card_flow_btn_submit).click()

        assertInProgressState()

        assertDisplaysResponseFromServer(cardConfigurationErrorRule.activity.getString(R.string.session_reference), cardConfigurationErrorRule.activity.window.decorView)
    }
}

class CardConfigurationErrorRule(activityClass: Class<MainActivity>) : ActivityTestRule<MainActivity>(activityClass) {

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        // This card configuration rule adds stubs to mockserver to simulate a server error condition on the card configuration endpoint.
        // On initialisation of our SDK, the SDK will trigger a card configuration call which will get back this error
        // response.
        simulateCardConfigurationServerError()
    }
}
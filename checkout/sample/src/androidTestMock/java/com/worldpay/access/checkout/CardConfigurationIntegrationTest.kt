package com.worldpay.access.checkout

import android.support.test.rule.ActivityTestRule
import com.worldpay.access.checkout.MockServer.simulateCardConfigurationServerError
import com.worldpay.access.checkout.UITestUtils.assertBrandImage
import com.worldpay.access.checkout.UITestUtils.assertDisplaysResponseFromServer
import com.worldpay.access.checkout.UITestUtils.assertFieldsAlpha
import com.worldpay.access.checkout.UITestUtils.assertInProgressState
import com.worldpay.access.checkout.UITestUtils.assertValidInitialUIFields
import com.worldpay.access.checkout.UITestUtils.cardNumberMatcher
import com.worldpay.access.checkout.UITestUtils.checkFieldInState
import com.worldpay.access.checkout.UITestUtils.checkSubmitInState
import com.worldpay.access.checkout.UITestUtils.monthMatcher
import com.worldpay.access.checkout.UITestUtils.moveToField
import com.worldpay.access.checkout.UITestUtils.typeFormInputs
import com.worldpay.access.checkout.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.UITestUtils.updateMonthDetails
import com.worldpay.access.checkout.UITestUtils.updatePANDetails
import com.worldpay.access.checkout.UITestUtils.yearMatcher
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
    var cardConfigurationRule: CardConfigurationRule =
        CardConfigurationRule(MainActivity::class.java)

    @Test
    fun givenCardConfigurationCallFails_AndValidUnknownCardDataIsInsertedAndUserPressesSubmit_ThenSuccessfulResponseIsReceived() {
        assertValidInitialUIFields()

        typeFormInputs(luhnInvalidUnknownCard, unknownCvv, "13", year, true)

        assertBrandImage(R.drawable.card_unknown)
        checkFieldInState(false, cardNumberMatcher, cardConfigurationRule.activity)
        checkFieldInState(false, monthMatcher, cardConfigurationRule.activity)
        checkFieldInState(false, yearMatcher, cardConfigurationRule.activity)
        checkSubmitInState(false)

        updatePANDetails(luhnValidUnknownCard)
        assertBrandImage(R.drawable.card_unknown)
        checkFieldInState(true, cardNumberMatcher, cardConfigurationRule.activity)
        updateMonthDetails(month)

        updatePANDetails(luhnValidMastercardCard)
        assertBrandImage(R.drawable.card_unknown)
        checkFieldInState(true, cardNumberMatcher, cardConfigurationRule.activity)

        checkFieldInState(true, cardNumberMatcher, cardConfigurationRule.activity)
        checkFieldInState(true, monthMatcher, cardConfigurationRule.activity)
        checkFieldInState(true, yearMatcher, cardConfigurationRule.activity)

        checkSubmitInState(true)

        assertFieldsAlpha(1.0f)
        assertTrue(uiObjectWithId(R.id.submit).exists())
        uiObjectWithId(R.id.submit).click()

        assertInProgressState()

        assertDisplaysResponseFromServer(cardConfigurationRule.activity.getString(R.string.session_reference), cardConfigurationRule.activity.window.decorView)
    }
}

class CardConfigurationRule(private val activityClass: Class<MainActivity>) : ActivityTestRule<MainActivity>(activityClass) {

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        // This card configuration rule adds stubs to mockserver to simulate a server error condition on the card configuration endpoint.
        // On initialisation of our SDK, the SDK will trigger a card configuration call which will get back this error
        // response.
        simulateCardConfigurationServerError()
    }
}
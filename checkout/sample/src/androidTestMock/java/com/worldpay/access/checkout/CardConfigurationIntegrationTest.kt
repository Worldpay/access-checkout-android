package com.worldpay.access.checkout

import android.support.test.rule.ActivityTestRule
import com.worldpay.access.checkout.UITestUtils.assertDisplaysResponseFromServer
import com.worldpay.access.checkout.UITestUtils.assertFieldsAlpha
import com.worldpay.access.checkout.UITestUtils.assertInProgressState
import com.worldpay.access.checkout.UITestUtils.assertValidInitialUIFields
import com.worldpay.access.checkout.UITestUtils.cardNumberMatcher
import com.worldpay.access.checkout.UITestUtils.checkFieldIsValidState
import com.worldpay.access.checkout.UITestUtils.monthMatcher
import com.worldpay.access.checkout.UITestUtils.typeFormInputs
import com.worldpay.access.checkout.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.UITestUtils.updateMonthDetails
import com.worldpay.access.checkout.UITestUtils.updatePANDetails
import com.worldpay.access.checkout.UITestUtils.yearMatcher
import org.junit.Rule
import org.junit.Test

class CardConfigurationIntegrationTest {

    private val luhnValidUnknownCard = "0000000"
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

        typeFormInputs(luhnInvalidUnknownCard, unknownCvv, "13", year)

        checkFieldIsValidState(false, cardNumberMatcher, cardConfigurationRule.activity)
        checkFieldIsValidState(false, monthMatcher, cardConfigurationRule.activity)
        checkFieldIsValidState(false, yearMatcher, cardConfigurationRule.activity)

        updatePANDetails(luhnValidUnknownCard)
        updateMonthDetails(month)

        assertFieldsAlpha(1.0f)
        uiObjectWithId(R.id.submit).exists()
        uiObjectWithId(R.id.submit).click()

        assertInProgressState()

        assertDisplaysResponseFromServer(cardConfigurationRule.activity.getString(R.string.session_reference), cardConfigurationRule.activity.window.decorView)
    }
}

class CardConfigurationRule(activityClass: Class<MainActivity>) : ActivityTestRule<MainActivity>(activityClass) {

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        // This card configuration rule adds stubs to mockserver to simulate a server error condition on the card configuration endpoint.
        // On initialisation of our SDK, the SDK will trigger a card configuration call which will get back this error
        // response.
        MockServer.simulateCardConfigurationServerError()
    }
}
package com.worldpay.access.checkout.cvv

import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.CardConfigurationMockStub.simulateCardConfigurationServerError
import com.worldpay.access.checkout.CardConfigurationMockStub.stubCardConfiguration
import com.worldpay.access.checkout.MainActivity
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.cvv.testutil.CvvFragmentTestUtils.assertFieldsAlpha
import com.worldpay.access.checkout.cvv.testutil.CvvFragmentTestUtils.assertInProgressState
import com.worldpay.access.checkout.cvv.testutil.CvvFragmentTestUtils.assertValidInitialUIFields
import com.worldpay.access.checkout.cvv.testutil.CvvFragmentTestUtils.checkSubmitInState
import com.worldpay.access.checkout.cvv.testutil.CvvFragmentTestUtils.typeFormInputs
import com.worldpay.access.checkout.testutil.UITestUtils.assertDisplaysResponseFromServer
import com.worldpay.access.checkout.testutil.UITestUtils.uiObjectWithId
import org.junit.After
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

class CvvConfigurationIntegrationTest {

    private val unknownCvv = "123456"

    @get:Rule
    var cardConfigurationErrorRule: CardConfigurationErrorRule =
        CardConfigurationErrorRule(MainActivity::class.java)

    @After
    fun tearDown() {
        stubCardConfiguration(cardConfigurationErrorRule.activity)
    }

    @Test
    fun givenCardConfigurationCallFails_AndValidUnknownCVVDataIsInsertedAndUserPressesSubmit_ThenSuccessfulResponseIsReceived() {
        assertValidInitialUIFields()

        typeFormInputs(unknownCvv, true)

        checkSubmitInState(false)

        assertFieldsAlpha(1.0f)
        assertTrue(uiObjectWithId(R.id.cvv_flow_btn_submit).exists())
        uiObjectWithId(R.id.cvv_flow_btn_submit).click()

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
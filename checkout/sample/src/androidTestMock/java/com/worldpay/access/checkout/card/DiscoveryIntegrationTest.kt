package com.worldpay.access.checkout.card

import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.MainActivity
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.RootResourseMockStub.simulateRootResourceTemporaryServerError
import com.worldpay.access.checkout.card.testutil.CardFragmentTestUtils.assertFieldsAlpha
import com.worldpay.access.checkout.card.testutil.CardFragmentTestUtils.assertInProgressState
import com.worldpay.access.checkout.card.testutil.CardFragmentTestUtils.assertValidInitialUIFields
import com.worldpay.access.checkout.card.testutil.CardFragmentTestUtils.typeFormInputs
import com.worldpay.access.checkout.testutil.UITestUtils.assertDisplaysResponseFromServer
import com.worldpay.access.checkout.testutil.UITestUtils.uiObjectWithId
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DiscoveryIntegrationTest {

    private val amexCard = "343434343434343"
    private val amexCvv = "1234"
    private val month = "12"
    private val year = "99"

    @get:Rule
    var discoveryRule: DiscoveryRule =
        DiscoveryRule(MainActivity::class.java)

    @Test
    fun givenInitialDiscoveryFails_AndValidDataIsInsertedAndUserPressesSubmit_ThenDiscoveryIsReattempted_AndSuccessfulResponseIsReceived() {
        assertValidInitialUIFields()
        typeFormInputs(amexCard, amexCvv, month, year)
        assertFieldsAlpha(1.0f)
        assertTrue(uiObjectWithId(R.id.card_flow_btn_submit).exists())
        uiObjectWithId(R.id.card_flow_btn_submit).click()

        assertInProgressState()

        assertDisplaysResponseFromServer(discoveryRule.activity.getString(R.string.verified_token_session_reference), discoveryRule.activity.window.decorView)
    }
}

class DiscoveryRule(activityClass: Class<MainActivity>) : ActivityTestRule<MainActivity>(activityClass) {

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        // This discovery rule adds stubs to mockserver to simulate a server error condition on the discovery endpoint.
        // On initialisation of our SDK, the SDK will trigger a discovery call which will get back this error
        // response. On the next call to the same endpoint (when making the payment request), a successful stubbed response will then be
        // returned by mockserver
        simulateRootResourceTemporaryServerError()
    }
}
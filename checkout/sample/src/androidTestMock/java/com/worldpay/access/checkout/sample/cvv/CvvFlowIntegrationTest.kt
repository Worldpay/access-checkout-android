package com.worldpay.access.checkout.sample.cvv

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.client.session.model.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.cvv.testutil.CvvFragmentTestUtils
import com.worldpay.access.checkout.sample.testutil.UITestUtils.navigateTo
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotatePortrait
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CvvFlowIntegrationTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    private lateinit var cvvFragmentTestUtils: CvvFragmentTestUtils

    @Before
    fun setup() {
        cvvFragmentTestUtils = CvvFragmentTestUtils(activityRule)
        rotatePortrait(activityRule)
        navigateTo(R.id.nav_cvv_flow)
    }

    @Test
    fun shouldBeReturningValidResponse_whenEnteringValidCvv() {
        cvvFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvv = "123")
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(PAYMENTS_CVC_SESSION to activityRule.activity.getString(R.string.payments_cvc_session_reference)).toString()
            )
            .closeDialog()
            .cardDetailsAre(cvv = "")
            .enabledStateIs(submitButton = false)
    }

}

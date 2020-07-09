package com.worldpay.access.checkout.sample.cvc

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.client.session.model.SessionType.CVC
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.cvc.testutil.CvcFragmentTestUtils
import com.worldpay.access.checkout.sample.testutil.UITestUtils.navigateTo
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotatePortrait
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CvcFlowIntegrationTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    private lateinit var cvcFragmentTestUtils: CvcFragmentTestUtils

    @Before
    fun setup() {
        cvcFragmentTestUtils = CvcFragmentTestUtils(activityRule)
        rotatePortrait(activityRule)
        navigateTo(R.id.nav_cvc_flow)
    }

    @Test
    fun shouldBeReturningValidResponse_whenEnteringValidCvc() {
        cvcFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvc = "123")
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(CVC to activityRule.activity.getString(R.string.payments_cvc_session_reference)).toString()
            )
            .closeDialog()
            .cardDetailsAre(cvc = "")
            .enabledStateIs(submitButton = false)
    }

}

package com.worldpay.access.checkout.sample.cvc.testutil

import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.MockServer.defaultStubMappings
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.testutil.UITestUtils.navigateTo
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotatePortrait
import org.junit.Before
import org.junit.Rule

abstract class AbstractCvcFragmentTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    lateinit var cvcFragmentTestUtils: CvcFragmentTestUtils

    @Before
    fun setup() {
        cvcFragmentTestUtils = CvcFragmentTestUtils(activityRule)
        defaultStubMappings(activityRule.activity)
        navigateTo(R.id.nav_cvc_flow)
        closeSoftKeyboard()
        rotatePortrait(activityRule)
    }

}

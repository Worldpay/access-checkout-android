package com.worldpay.access.checkout.sample.cvv.testutil

import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.MockServer.defaultStubMappings
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.testutil.UITestUtils.navigateTo
import org.junit.Before
import org.junit.Rule

abstract class AbstractCvvFragmentTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    lateinit var cvvFragmentTestUtils: CvvFragmentTestUtils

    @Before
    fun setup() {
        cvvFragmentTestUtils = CvvFragmentTestUtils(activityRule)
        defaultStubMappings(activityRule.activity)
        navigateTo(R.id.nav_cvv_flow)
        closeSoftKeyboard()
    }

}
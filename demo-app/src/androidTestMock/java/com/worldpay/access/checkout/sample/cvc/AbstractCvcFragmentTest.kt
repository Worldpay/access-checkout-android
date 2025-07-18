package com.worldpay.access.checkout.sample.cvc

import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.MockServer.defaultStubMappings
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.testutil.UITestUtils.navigateTo
import org.awaitility.kotlin.await
import org.junit.Before
import org.junit.Rule
import java.util.concurrent.TimeUnit

abstract class AbstractCvcFragmentTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    lateinit var cvcFragmentTestUtils: CvcFragmentTestUtils

    @Before
    fun setup() {
        cvcFragmentTestUtils = CvcFragmentTestUtils(activityRule)
        defaultStubMappings(activityRule.activity)
        navigateTo(R.id.nav_cvc_flow)

        // When starting a new tests the below introduce unnecessary delays therefore I have left them commented out for now in case we ever need to do this.
        // But it would be better to test this behaviour only when necessary on a per test-case basis
        // closeSoftKeyboard()
        // rotatePortrait(activityRule)
    }

    fun restartApp() {
        with(activityRule) {
            finishActivity()
            launchActivity(null)
        }

        await.atMost(5, TimeUnit.SECONDS).until {
            applicationIsVisible()
        }
    }

    private fun applicationIsVisible(): Boolean {
        return try {
            navigateTo(R.id.nav_cvc_flow)
            cvcFragmentTestUtils.isInInitialState()
            true
        } catch (e: Exception) {
            false
        }
    }
}

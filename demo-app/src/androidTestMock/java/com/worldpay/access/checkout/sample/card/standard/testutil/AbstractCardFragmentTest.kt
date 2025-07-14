package com.worldpay.access.checkout.sample.card.standard.testutil

import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.MockServer.defaultStubMappings
import com.worldpay.access.checkout.test.mocks.AccessWPServiceWiremock
import java.util.concurrent.TimeUnit
import org.awaitility.kotlin.await
import org.junit.Before
import org.junit.Rule

abstract class AbstractCardFragmentTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    lateinit var cardFragmentTestUtils: CardFragmentTestUtils

    @Before
    fun setup() {
        cardFragmentTestUtils = CardFragmentTestUtils(activityRule)
        defaultStubMappings(activityRule.activity, AccessWPServiceWiremock.server!!)
//        closeSoftKeyboard()
//        rotatePortrait(activityRule)
    }

    fun clearPan() {
        cardFragmentTestUtils.clearCardDetails(pan = true)
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
            cardFragmentTestUtils.isInInitialState()
            true
        } catch (e: Exception) {
            false
        }
    }
}

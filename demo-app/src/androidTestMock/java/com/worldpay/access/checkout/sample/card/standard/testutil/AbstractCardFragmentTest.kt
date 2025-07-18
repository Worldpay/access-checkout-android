package com.worldpay.access.checkout.sample.card.standard.testutil

import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.MockServer.defaultStubMappings
import org.awaitility.kotlin.await
import org.junit.Before
import org.junit.Rule
import java.util.concurrent.TimeUnit

abstract class AbstractCardFragmentTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    lateinit var cardFragmentTestUtils: CardFragmentTestUtils

    @Before
    fun setup() {
        cardFragmentTestUtils = CardFragmentTestUtils(activityRule)
        defaultStubMappings(activityRule.activity)

        // When starting a new tests the below introduce unnecessary delays therefore I have left them commented out for now in case we ever need to do this.
        // But it would be better to test this behaviour only when necessary on a per test-case basis
        // closeSoftKeyboard()
        // rotatePortrait(activityRule)
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

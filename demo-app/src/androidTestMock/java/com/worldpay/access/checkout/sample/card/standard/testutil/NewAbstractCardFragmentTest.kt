package com.worldpay.access.checkout.sample.card.standard.testutil

import android.content.Intent
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.MockServer.defaultStubMappings
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotatePortrait
import java.util.concurrent.TimeUnit
import org.awaitility.kotlin.await
import org.junit.Before
import org.junit.Rule

abstract class NewAbstractCardFragmentTest {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    lateinit var cardFragmentTestUtils: NewCardFragmentTestUtils

    @Before
    fun setup() {
        cardFragmentTestUtils = NewCardFragmentTestUtils(activityRule)
        activityRule.scenario.onActivity { activity ->
            defaultStubMappings(activity)
            closeSoftKeyboard()
            rotatePortrait(activityRule)
        }
    }

    fun clearPan() {
        cardFragmentTestUtils.clearCardDetails(pan = true)
    }

    fun restartApp() {
        activityRule.scenario.onActivity { activity ->
            val intent = Intent(activity::class.java.name)
            activity.finish()
            activity.startActivity(intent)
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

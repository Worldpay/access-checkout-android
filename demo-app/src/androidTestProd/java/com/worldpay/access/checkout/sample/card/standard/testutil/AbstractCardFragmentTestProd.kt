package com.worldpay.access.checkout.sample.card.standard.testutil

import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotatePortrait
import java.util.concurrent.TimeUnit
import org.awaitility.kotlin.await
import org.junit.Before
import org.junit.Rule

abstract class AbstractCardFragmentTestProd {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    lateinit var cardFragmentTestUtils: CardFragmentTestUtils

    @Before
    fun setup() {
        cardFragmentTestUtils = CardFragmentTestUtils(activityRule)
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

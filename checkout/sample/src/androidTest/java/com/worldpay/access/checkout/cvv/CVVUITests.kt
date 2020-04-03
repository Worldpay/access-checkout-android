package com.worldpay.access.checkout.cvv

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.worldpay.access.checkout.AbstractUITest
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.UITestUtils.checkFieldText
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CVVUITests: AbstractUITest() {

    @Before
    fun before() {
        navigateToCVVFlow(R.id.nav_cvv_flow)
    }

    @Test
    fun shouldHaveCvvInputVisibleAndEnabled() {
        onView(withId(R.id.cvv_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
    }

    @Test
    fun shouldHaveSubmitButtonVisibleAndDisabled() {
        onView(withId(R.id.cvv_flow_btn_submit))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))
    }

    @Test
    fun shouldKeepDisabledSubmitButtonOn1digitEntered() {
        onView(withId(R.id.cvv_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1"))

        onView(withId(R.id.cvv_flow_btn_submit))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        checkFieldText(withId(R.id.cvv_flow_text_cvv), "1")
    }

    @Test
    fun shouldKeepDisabledSubmitButtonOn2digitsEntered() {
        onView(withId(R.id.cvv_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("11"))

        onView(withId(R.id.cvv_flow_btn_submit))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        checkFieldText(withId(R.id.cvv_flow_text_cvv), "11")
    }

    @Test
    fun shouldEnableSubmitButtonOn3digitsEntered() {
        onView(withId(R.id.cvv_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("111"))

        onView(withId(R.id.cvv_flow_btn_submit))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        checkFieldText(withId(R.id.cvv_flow_text_cvv), "111")
    }

    @Test
    fun shouldEnableSubmitButtonOn4digitsEntered() {
        onView(withId(R.id.cvv_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1111"))

        onView(withId(R.id.cvv_flow_btn_submit))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        checkFieldText(withId(R.id.cvv_flow_text_cvv), "1111")
    }

    @Test
    fun shouldIgnoreLettersInCvvInput() {
        onView(withId(R.id.cvv_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("sdovidsiv23odfvj0d"))

        onView(withId(R.id.cvv_flow_btn_submit))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        checkFieldText(withId(R.id.cvv_flow_text_cvv), "230")
    }
    
    @Test
    fun shouldKeepStateOnRotationAfterEnteringValidCVV() {
        onView(withId(R.id.cvv_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("1111"))

        checkFieldText(withId(R.id.cvv_flow_text_cvv), "1111")

        onView(withId(R.id.cvv_flow_btn_submit))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        activityRule.activity.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE

        onView(withId(R.id.cvv_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        checkFieldText(withId(R.id.cvv_flow_text_cvv), "1111")

        onView(withId(R.id.cvv_flow_btn_submit))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
    }

    @Test
    fun shouldKeepStateOnRotationAfterEnteringInvalidCVV() {
        onView(withId(R.id.cvv_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("11"))

        checkFieldText(withId(R.id.cvv_flow_text_cvv), "11")

        onView(withId(R.id.cvv_flow_btn_submit))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        activityRule.activity.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE

        onView(withId(R.id.cvv_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        checkFieldText(withId(R.id.cvv_flow_text_cvv), "11")

        onView(withId(R.id.cvv_flow_btn_submit))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))
    }
    
    @Test
    fun shouldOnlyKeepMaxLengthUponPastingLengthyValue() {
        onView(withId(R.id.cvv_flow_text_cvv))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(replaceText("11111111111111"))
            .check(matches(withText("1111")))

        onView(withId(R.id.cvv_flow_btn_submit))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
    }
    
}

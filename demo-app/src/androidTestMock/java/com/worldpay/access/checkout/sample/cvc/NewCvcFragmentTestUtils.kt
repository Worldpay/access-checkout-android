package com.worldpay.access.checkout.sample.cvc

import android.R
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.testutil.NewAbstractFragmentTestUtils
import com.worldpay.access.checkout.sample.testutil.UITestUtils

class NewCvcFragmentTestUtils(activityRule: ActivityScenarioRule<MainActivity>) : NewAbstractFragmentTestUtils(activityRule) {
    fun isInInitialState() = also {
        progressBarNotVisible()
        enabledStateIs(cvc = true, submitButton = false)
        cardDetailsAre(cvc = "")
    }

    fun requestIsInProgress() = also {
        progressBarIsVisible()
        enabledStateIs(cvc = false, submitButton = false)
    }

    fun hasResponseDialogWithMessage(response: String) = also {
        dialogHasText(response)
    }

    fun closeDialog() = also {
        Espresso.onView(ViewMatchers.withId(R.id.button1)).perform(ViewActions.click())
    }

    fun enabledStateIs(cvc: Boolean? = null, submitButton: Boolean? = null) = also {
        val visibleMsg = "visibility state"
        val enableMsg = "enabled state"

        if (cvc != null) {
            waitForAssertion { Espresso.onView(ViewMatchers.withId(com.worldpay.access.checkout.sample.R.id.cvc_flow_text_cvc))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed())) }
            waitForAssertion { Espresso.onView(ViewMatchers.withId(com.worldpay.access.checkout.sample.R.id.cvc_flow_text_cvc))
                .check(ViewAssertions.matches(ViewMatchers.isEnabled())) }
        }

        if (submitButton != null) {
            waitForAssertion { Espresso.onView(ViewMatchers.withId(com.worldpay.access.checkout.sample.R.id.cvc_flow_btn_submit))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed())) }
            waitForAssertion { Espresso.onView(ViewMatchers.withId(com.worldpay.access.checkout.sample.R.id.cvc_flow_btn_submit))
                .check(ViewAssertions.matches(ViewMatchers.isEnabled())) }
        }
    }

    fun validationStateIs(cvv: Boolean) = also {
        checkValidationState(withParent(withId(com.worldpay.access.checkout.sample.R.id.cvc_flow_text_cvc)), cvv, "cvv")
    }

    fun focusOff() = also {
        Espresso.onView(ViewMatchers.withId(com.worldpay.access.checkout.sample.R.id.cvc_flow_text_cvc))
            .perform()
    }

    fun clickSubmitButton() = also {
        enabledStateIs(submitButton = true)
        UITestUtils.uiObjectWithId(com.worldpay.access.checkout.sample.R.id.cvc_flow_btn_submit).click()
    }

    fun enterCardDetails(cvc: String, assertText: Boolean = false) = also {
        enterText(com.worldpay.access.checkout.sample.R.id.cvc_flow_text_cvc, cvc)
        if (assertText) {
            cardDetailsAre(cvc)
        }
    }

    fun cardDetailsAre(cvc: String) = also {
        waitForAssertion { Espresso.onView(ViewMatchers.withId(com.worldpay.access.checkout.sample.R.id.cvc_flow_text_cvc))
            .check(ViewAssertions.matches(ViewMatchers.withText(cvc))) }
    }
}
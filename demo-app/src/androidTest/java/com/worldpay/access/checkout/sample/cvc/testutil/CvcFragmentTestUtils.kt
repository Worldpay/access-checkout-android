package com.worldpay.access.checkout.sample.cvc.testutil

import android.widget.Button
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.testutil.AbstractFragmentTestUtils
import com.worldpay.access.checkout.sample.testutil.UITestUtils.uiObjectWithId
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CvcFragmentTestUtils(activityRule: ActivityTestRule<MainActivity>) : AbstractFragmentTestUtils(activityRule) {

    private fun cvcInput() = findById<EditText>(R.id.cvc_flow_text_cvc)
    private fun submitButton() = findById<Button>(R.id.cvc_flow_btn_submit)

    fun isInInitialState(): CvcFragmentTestUtils {
        progressBarNotVisible()
        enabledStateIs(cvc = true, submitButton = false)
        cardDetailsAre(cvc = "")
        return this
    }

    fun requestIsInProgress(): CvcFragmentTestUtils {
        progressBarIsVisible()
        enabledStateIs(cvc = false, submitButton = false)
        return this
    }

    fun hasResponseDialogWithMessage(response: String): CvcFragmentTestUtils {
        dialogHasText(response)
        return this
    }

    fun closeDialog(): CvcFragmentTestUtils {
        onView(withId(android.R.id.button1)).perform(click())
        return this
    }

    fun enabledStateIs(cvc: Boolean? = null, submitButton: Boolean? = null): CvcFragmentTestUtils {
        val visibleMsg = "visibility state"
        val enableMsg = "enabled state"

        if (cvc != null) {
            wait { assertTrue("CVC Input - $visibleMsg") { cvcInput().isVisible } }
            wait { assertEquals(cvc, cvcInput().isEnabled, "CVC Input - $enableMsg") }
        }

        if (submitButton != null) {
            wait { assertTrue("Submit Button - $visibleMsg") { this.submitButton().isVisible } }
            wait { assertEquals(submitButton, this.submitButton().isEnabled, "Submit Button - $enableMsg") }
        }

        return this
    }

    fun validationStateIs(cvv: Boolean): CvcFragmentTestUtils {
        checkValidationState(cvcInput(), cvv, "cvv")
        return this
    }

    fun focusOff() :CvcFragmentTestUtils {
        cvcInput().onFocusChangeListener.onFocusChange(cvcInput(), false)
        return this
    }

    fun clickSubmitButton(): CvcFragmentTestUtils {
        enabledStateIs(submitButton = true)
        uiObjectWithId(R.id.cvc_flow_btn_submit).click()
        return this
    }

    fun enterCardDetails(cvc: String, assertText: Boolean = false): CvcFragmentTestUtils {
        enterText(cvcInput(), cvc)

        if (assertText) {
            cardDetailsAre(cvc)
        }

        return this
    }

    fun cardDetailsAre(cvc: String): CvcFragmentTestUtils {
        wait { assertEquals(cvc, cvcInput().text.toString()) }
        return this
    }

}

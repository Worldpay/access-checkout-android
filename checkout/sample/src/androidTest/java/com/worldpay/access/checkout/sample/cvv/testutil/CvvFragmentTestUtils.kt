package com.worldpay.access.checkout.sample.cvv.testutil

import android.widget.Button
import androidx.core.view.isVisible
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.testutil.AbstractFragmentTestUtils
import com.worldpay.access.checkout.sample.testutil.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.views.CardCVVText
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CvvFragmentTestUtils(activityRule: ActivityTestRule<MainActivity>) : AbstractFragmentTestUtils(activityRule) {

    private fun cvvInput() = findById<CardCVVText>(R.id.cvv_flow_text_cvv)
    private fun submitButton() = findById<Button>(R.id.cvv_flow_btn_submit)

    fun isInInitialState(): CvvFragmentTestUtils {
        progressBarNotVisible()
        enabledStateIs(cvv = true, submitButton = false)
        cardDetailsAre(cvv = "")
        return this
    }

    fun requestIsInProgress(): CvvFragmentTestUtils {
        progressBarIsVisible()
        enabledStateIs(cvv = false, submitButton = false)
        return this
    }

    fun hasResponseDialogWithMessage(response: String): CvvFragmentTestUtils {
        dialogHasText(response)
        return this
    }

    fun hasErrorDialogWithMessage(error: String): CvvFragmentTestUtils {
        dialogHasText(error)
        return this
    }

    fun closeDialog(): CvvFragmentTestUtils {
        onView(withId(android.R.id.button1)).perform(click())
        return this
    }

    fun enabledStateIs(cvv: Boolean? = null, submitButton: Boolean? = null): CvvFragmentTestUtils {
        val visibleMsg = "visibility state"
        val enableMsg = "enabled state"

        if (cvv != null) {
            wait { assertTrue("CVV Input - $visibleMsg") { cvvInput().isVisible } }
            wait { assertEquals(cvv, cvvInput().isEnabled, "CVV Input - $enableMsg") }
        }

        if (submitButton != null) {
            wait { assertTrue("Submit Button - $visibleMsg") { this.submitButton().isVisible } }
            wait { assertEquals(submitButton, this.submitButton().isEnabled, "Submit Button - $enableMsg") }
        }

        return this
    }

    fun clickSubmitButton(): CvvFragmentTestUtils {
        enabledStateIs(submitButton = true)
        uiObjectWithId(R.id.cvv_flow_btn_submit).click()
        return this
    }

    fun enterCardDetails(cvv: String, assertText: Boolean = false): CvvFragmentTestUtils {
        enterText(cvvInput(), cvv)

        if (assertText) {
            cardDetailsAre(cvv)
        }

        return this
    }

    fun cardDetailsAre(cvv: String): CvvFragmentTestUtils {
        wait { assertEquals(cvv, cvvInput().text.toString()) }
        return this
    }

}
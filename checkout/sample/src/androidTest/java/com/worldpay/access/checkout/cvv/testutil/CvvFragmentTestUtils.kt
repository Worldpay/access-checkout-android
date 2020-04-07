package com.worldpay.access.checkout.cvv.testutil

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.uiautomator.UiObject
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.testutil.UITestUtils
import com.worldpay.access.checkout.testutil.UITestUtils.assertUiObjectExistsAndIsDisabled
import com.worldpay.access.checkout.testutil.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.testutil.matchers.AlphaMatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.Assert

object CvvFragmentTestUtils {

    private val cvvMatcher: Matcher<View> = ViewMatchers.withId(R.id.cvv_flow_text_cvv)
    private val buttonMatcher: Matcher<View> = ViewMatchers.withId(R.id.cvv_flow_btn_submit)
    private val progressMatcher: Matcher<View> = ViewMatchers.withId(R.id.loading_bar)

    fun assertInProgressState() {
        Assert.assertTrue(
            progressBar()
                .exists())
        UITestUtils.closeKeyboard()
        assertFieldsAndSubmitButtonIsDisabled()
    }

    fun assertValidInitialUIFields() {
        onView(cvvMatcher)
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))

        checkSubmitInState(enabled = false)

        onView(progressMatcher)
            .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    }

    fun typeFormInputs(cvv: String, assertInsertedCompleteText: Boolean = false) {
        typeCVVDetails(
            cvv
        )

        if (assertInsertedCompleteText) {
            UITestUtils.checkFieldText(cvvMatcher, cvv)
        }
    }

    fun updateCVVDetails(cvv: String) {
        onView(cvvMatcher)
            .perform(ViewActions.click(), ViewActions.replaceText(cvv), ViewActions.closeSoftKeyboard())
    }

    fun checkSubmitInState(enabled: Boolean) {
        val enabledMatcher: Matcher<View> =
            if (enabled) ViewMatchers.isEnabled() else CoreMatchers.not(ViewMatchers.isEnabled())
        onView(buttonMatcher)
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(enabledMatcher))
    }

    fun assertFieldsAlpha(alpha: Float) {
        onView(cvvMatcher)
            .check(ViewAssertions.matches(AlphaMatcher.withAlpha(alpha)))
        onView(buttonMatcher)
            .check(ViewAssertions.matches(AlphaMatcher.withAlpha(alpha)))
    }

    private fun typeCVVDetails(cvv: String) {
        onView(cvvMatcher)
            .perform(ViewActions.typeText(cvv), ViewActions.closeSoftKeyboard())
    }

    private fun progressBar(): UiObject =
        uiObjectWithId(
            R.id.loading_bar
        )

    private fun assertFieldsAndSubmitButtonIsDisabled() {
        assertUiObjectExistsAndIsDisabled(R.id.cvv_flow_text_cvv)
        assertUiObjectExistsAndIsDisabled(R.id.cvv_flow_btn_submit)
    }


}
package com.worldpay.access.checkout.sample.card.standard.testutil

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.isNotEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.testutil.NewAbstractFragmentTestUtils
import com.worldpay.access.checkout.sample.testutil.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import org.hamcrest.Matcher
import org.hamcrest.Matchers

class NewCardFragmentTestUtils(activityRule: ActivityScenarioRule<MainActivity>) : NewAbstractFragmentTestUtils(activityRule) {

    enum class Input {
        PAN, CVC, EXPIRY_DATE
    }

    fun isInInitialState() = apply {
        progressBarNotVisible()
        enabledStateIs(pan = true, cvc = true, expiryDate = true, submitButton = false)
        cardDetailsAre(pan = "", cvc = "", expiryDate = "")
        hasNoBrand()
        paymentsCvcSessionCheckedState(checked = false)
    }

    fun setPaymentsCvcSwitchState(checked: Boolean) = apply {
        wait2 { onView(withId(R.id.card_flow_payments_cvc_switch)).check(matches(isDisplayed())) }

        if (checked != withId(R.id.card_flow_payments_cvc_switch).matches(isChecked()))
            uiObjectWithId(R.id.card_flow_payments_cvc_switch).click()
    }

    fun paymentsCvcSessionCheckedState(checked: Boolean) = apply {
        val checkedViewMatcher = if (checked) isChecked() else isNotChecked()
        wait2 { onView(withId(R.id.card_flow_payments_cvc_switch)).check(matches(isDisplayed())) }
        wait2 { onView(withId(R.id.card_flow_payments_cvc_switch)).check(matches(checkedViewMatcher)) }
    }

    fun requestIsInProgress() = apply {
        progressBarIsVisible()
        enabledStateIs(pan = false, cvc = false, expiryDate = false, paymentsCvcSwitch = false, submitButton = false)
    }

    fun hasResponseDialogWithMessage(response: String) = apply {
        dialogHasText(response)
    }

    fun hasErrorDialogWithMessage(error: String) = apply {
        dialogHasText(error)
    }

    fun closeDialog() = apply {
        onView(withId(android.R.id.button1)).perform(click())
    }

    fun isInErrorState(pan: String? = null, cvc: String? = null, expiryDate: String? = null) = apply {
        progressBarNotVisible()
        enabledStateIs(pan = true, cvc = true, expiryDate = true, paymentsCvcSwitch = true, submitButton = true)
        cardDetailsAre(pan, cvc, expiryDate)
    }

    fun enabledStateIs(
        pan: Boolean? = null,
        cvc: Boolean? = null,
        expiryDate: Boolean? = null,
        paymentsCvcSwitch: Boolean? = null,
        submitButton: Boolean? = null
    ) = apply {
        lateinit var enabledViewMatcher: Matcher<View>
        lateinit var viewMatcher: Matcher<View>

        val accessCheckoutFieldViewMatcher: AccessCheckoutFieldViewMatcher = when {
            pan != null -> AccessCheckoutFieldViewMatcher.PanViewMatcher(pan)
            cvc != null -> AccessCheckoutFieldViewMatcher.CvcViewMatcher(cvc)
            expiryDate != null -> AccessCheckoutFieldViewMatcher.ExpiryDateViewMatcher(expiryDate)
            paymentsCvcSwitch != null -> AccessCheckoutFieldViewMatcher.SwitchViewMatcher(paymentsCvcSwitch)
            submitButton != null -> AccessCheckoutFieldViewMatcher.SubmitButtonViewMatcher(submitButton)
            else -> throw RuntimeException("field view matcher not recognised")
        }

        wait2 { onView(accessCheckoutFieldViewMatcher.viewMatcher).check(matches(isDisplayed())) }
        wait2 { onView(accessCheckoutFieldViewMatcher.viewMatcher).check(matches(accessCheckoutFieldViewMatcher.enabledViewMatcher)) }

    }

    fun clickSubmitButton() = apply {
        enabledStateIs(submitButton = true)
        uiObjectWithId(R.id.card_flow_btn_submit).click()
    }

    fun focusOn(input: Input) = apply {
        when (input) {
            Input.PAN -> uiObjectWithId(R.id.card_flow_text_pan).click()
            Input.CVC -> uiObjectWithId(R.id.cvc_flow_text_cvc).click()
            Input.EXPIRY_DATE -> uiObjectWithId(R.id.card_flow_expiry_date).click()
        }
    }

    fun enterCardDetails(
        pan: String? = null,
        cvc: String? = null,
        expiryDate: String? = null,
        assertText: Boolean = false
    ) = apply {
        if (pan != null) enterText(R.id.card_flow_text_pan, pan)
        if (cvc != null) enterText(R.id.card_flow_text_cvc, cvc)
        if (expiryDate != null) enterText(R.id.card_flow_expiry_date, expiryDate)

        if (assertText) {
            cardDetailsAre(pan, cvc, expiryDate)
        }
    }

    fun enterPanCvcExpirydate(pan: String, cvc: String, expiryDate: String) = apply {
        assertViewIsVisible(R.id.card_flow_text_pan)
        assertViewIsVisible(R.id.card_flow_text_cvc)
        assertViewIsVisible(R.id.card_flow_expiry_date)
        enterTextOnViewWithId(pan, R.id.card_flow_text_pan)
        enterTextOnViewWithId(cvc, R.id.card_flow_text_cvc)
        enterTextOnViewWithId(expiryDate, R.id.card_flow_expiry_date)
    }

    fun clearCardDetails(
        pan: Boolean? = null,
        cvc: Boolean? = null,
        expiryDate: Boolean? = null
    ) {
        if (pan == true) clearText(R.id.card_flow_text_pan)
        if (expiryDate == true) clearText(R.id.card_flow_expiry_date)
        if (cvc == true) clearText(R.id.card_flow_text_cvc)
    }

    fun setCursorPositionOnPan(position: Int) = apply {
        setCursorPosition(R.id.card_flow_text_pan, position, position)
    }

    fun cursorPositionIs(position: Int) = apply {
        wait2 { onView(withId(R.id.card_flow_text_pan)).check { view, noViewFoundException ->
            val isPosition = (view as AccessCheckoutEditText).selectionEnd == position
            isPosition
            }
        }
    }

    fun cardDetailsAre(pan: String? = null, cvc: String? = null, expiryDate: String? = null) = apply {
        if (pan != null) wait2 { onView(withText(pan)).check(matches(isDisplayed())) }
        if (pan != null) wait2 { onView(withText(cvc)).check(matches(isDisplayed())) }
        if (pan != null) wait2 { onView(withText(expiryDate)).check(matches(isDisplayed())) }
    }

    fun validationStateIs(
        pan: Boolean? = null,
        cvc: Boolean? = null,
        expiryDate: Boolean? = null
    ) = apply {
//        if (pan != null) checkValidationState(R.id.card_flow_text_pan, pan, "pan")
        if (pan != null) checkValidationState(withParent(withId(R.id.card_flow_text_pan)), pan, "pan")
        if (cvc != null) checkValidationState(withParent(withId(R.id.card_flow_text_cvc)), cvc, "cvc")
        if (expiryDate != null) checkValidationState(withParent(withId(R.id.card_flow_expiry_date)), expiryDate, "expiry date")
    }

    fun hasNoBrand() = apply {
        var resourceEntryName = ""
        activityRule.scenario.onActivity(ActivityScenario.ActivityAction { activity ->
            resourceEntryName = activity.resources.getResourceEntryName(R.drawable.card_unknown_logo)
        })
        wait2 { onView(withId(R.id.card_flow_brand_logo)).check(matches(withTagValue(Matchers.`is`(resourceEntryName)))) }
    }

    fun hasBrand(cardBrand: CardBrand) = apply {
        wait2(20000) { onView(withId(R.id.card_flow_brand_logo)).check(matches(withTagValue(Matchers.`is`(cardBrand.cardBrandName)))) }
    }

    sealed class AccessCheckoutFieldViewMatcher(val viewMatcher: Matcher<View>, val isEnabled: Boolean) {
        val enabledViewMatcher: Matcher<View> = if (isEnabled) isEnabled() else isNotEnabled()
        class PanViewMatcher(isEnabled: Boolean) : AccessCheckoutFieldViewMatcher(withParent(withId(R.id.card_flow_text_pan)), isEnabled)
        class CvcViewMatcher(isEnabled: Boolean) : AccessCheckoutFieldViewMatcher(withParent(withId(R.id.card_flow_text_cvc)), isEnabled)
        class ExpiryDateViewMatcher(isEnabled: Boolean) : AccessCheckoutFieldViewMatcher(withParent(withId(R.id.card_flow_expiry_date)), isEnabled)
        class SwitchViewMatcher(isEnabled: Boolean) : AccessCheckoutFieldViewMatcher(withId(R.id.card_flow_payments_cvc_switch), isEnabled)
        class SubmitButtonViewMatcher(isEnabled: Boolean) : AccessCheckoutFieldViewMatcher(withId(R.id.card_flow_btn_submit), isEnabled)
    }

}

package com.worldpay.access.checkout.sample.card.testutil

import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import androidx.core.view.isVisible
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.testutil.CardFragmentTestUtils.Input.YEAR
import com.worldpay.access.checkout.sample.testutil.AbstractFragmentTestUtils
import com.worldpay.access.checkout.sample.testutil.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.views.CardCVVText
import com.worldpay.access.checkout.views.CardExpiryTextLayout
import com.worldpay.access.checkout.views.PANLayout
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CardFragmentTestUtils(activityRule: ActivityTestRule<MainActivity>) : AbstractFragmentTestUtils(activityRule) {

    private fun panInput() = findById<PANLayout>(R.id.card_flow_text_pan)
    private fun cvvInput() = findById<CardCVVText>(R.id.card_flow_text_cvv)
    private fun expiryDateInput() = findById<CardExpiryTextLayout>(R.id.card_flow_text_exp)
    private fun submitButton() = findById<Button>(R.id.card_flow_btn_submit)
    private fun brandLogo() = findById<ImageView>(R.id.logo_view)
    private fun paymentsCvcSwitch() = findById<Switch>(R.id.card_flow_payments_cvc_switch)

    enum class Input {
        PAN, CVV, MONTH, YEAR
    }

    fun isInInitialState(): CardFragmentTestUtils {
        progressBarNotVisible()
        enabledStateIs(pan = true, cvv = true, expiryMonth = true, expiryYear = true, submitButton = false)
        cardDetailsAre(pan = "", cvv = "", month = "", year = "")
        hasNoBrand()
        paymentsCvcSessionCheckedState(checked = false)
        return this
    }

    fun setPaymentsCvcSwitchState(checked: Boolean): CardFragmentTestUtils {
        wait { assertTrue("Payments Cvc Switch visibility") { paymentsCvcSwitch().isVisible } }
        if (checked != paymentsCvcSwitch().isChecked) {
            uiObjectWithId(R.id.card_flow_payments_cvc_switch).click()
        }
        return this
    }

    fun paymentsCvcSessionCheckedState(checked: Boolean): CardFragmentTestUtils {
        wait { assertTrue("Payments Cvc Switch visibility") { paymentsCvcSwitch().isVisible } }
        wait { assertEquals(checked, paymentsCvcSwitch().isChecked, "Payments Cvc Switch checked") }
        return this
    }

    fun requestIsInProgress(): CardFragmentTestUtils {
        progressBarIsVisible()
        enabledStateIs(pan = false, cvv = false, expiryMonth = false, expiryYear = false, paymentsCvcSwitch = false, submitButton = false)
        return this
    }

    fun hasResponseDialogWithMessage(response: String): CardFragmentTestUtils {
        dialogHasText(response)
        return this
    }

    fun hasErrorDialogWithMessage(error: String): CardFragmentTestUtils {
        dialogHasText(error)
        return this
    }

    fun closeDialog(): CardFragmentTestUtils {
        onView(withId(android.R.id.button1)).perform(click())
        return this
    }

    fun isInErrorState(pan: String? = null, cvv: String? = null, month: String? = null, year: String? = null): CardFragmentTestUtils {
        progressBarNotVisible()
        enabledStateIs(pan = true, cvv = true, expiryMonth = true, expiryYear = true, paymentsCvcSwitch = true, submitButton = true)
        cardDetailsAre(pan, cvv, month, year)
        return this
    }

    fun enabledStateIs(pan: Boolean? = null, cvv: Boolean? = null, expiryMonth: Boolean? = null,
                       expiryYear: Boolean? = null, paymentsCvcSwitch: Boolean? = null,
                       submitButton: Boolean? = null): CardFragmentTestUtils {
        val visibleMsg = "visibility state"
        val enableMsg = "enabled state"

        if (pan != null) {
            wait { assertTrue("PAN Input - $visibleMsg") { panInput().mEditText.isVisible } }
            wait { assertEquals(pan, panInput().mEditText.isEnabled, "PAN Input - $enableMsg") }
        }

        if (cvv != null) {
            wait { assertTrue("CVV Input - $visibleMsg") { cvvInput().isVisible } }
            wait { assertEquals(cvv, cvvInput().isEnabled, "CVV Input - $enableMsg") }
        }

        if (expiryMonth != null) {
            wait { assertTrue("Exp Month Input - $visibleMsg") { expiryDateInput().monthEditText.isVisible } }
            wait { assertEquals(expiryMonth, expiryDateInput().monthEditText.isEnabled, "Exp Month Input - $enableMsg") }
        }

        if (expiryYear != null) {
            wait { assertTrue("Exp Year Input - $visibleMsg") { expiryDateInput().yearEditText.isVisible } }
            wait { assertEquals(expiryYear, expiryDateInput().yearEditText.isEnabled, "Exp Year Input - $enableMsg") }
        }

        if (paymentsCvcSwitch != null) {
            wait { assertTrue("Payments CVC Switch - $visibleMsg") { paymentsCvcSwitch().isVisible } }
            wait { assertEquals(paymentsCvcSwitch, paymentsCvcSwitch().isEnabled, "Payments CVC Switch - $enableMsg") }
        }

        if (submitButton != null) {
            wait { assertTrue("Submit Button - $visibleMsg") { this.submitButton().isVisible } }
            wait { assertEquals(submitButton, this.submitButton().isEnabled, "Submit Button - $enableMsg") }
        }

        return this
    }

    fun clickSubmitButton(): CardFragmentTestUtils {
        enabledStateIs(submitButton = true)
        uiObjectWithId(R.id.card_flow_btn_submit).click()
        return this
    }

    fun focusOn(input: Input): CardFragmentTestUtils {
        when (input) {
            Input.PAN -> uiObjectWithId(panInput().mEditText.id).click()
            Input.CVV -> uiObjectWithId(cvvInput().id).click()
            Input.MONTH -> uiObjectWithId(expiryDateInput().monthEditText.id).click()
            YEAR -> uiObjectWithId(expiryDateInput().yearEditText.id).click()
        }
        return this
    }

    fun enterCardDetails(pan: String? = null, cvv: String? = null, month: String? = null, year: String? = null, assertText: Boolean = false): CardFragmentTestUtils {
        if (pan != null) enterText(panInput().mEditText, pan)
        if (cvv != null) enterText(cvvInput(), cvv)
        if (month != null) enterText(expiryDateInput().monthEditText, month)
        if (year != null) enterText(expiryDateInput().yearEditText, year)

        if (assertText) {
            cardDetailsAre(pan, cvv, month, year)
        }

        return this
    }

    fun cardDetailsAre(pan: String? = null, cvv: String? = null, month: String? = null, year: String? = null): CardFragmentTestUtils {
        if (pan != null) wait { assertEquals(pan, panInput().mEditText.text.toString()) }
        if (cvv != null) wait { assertEquals(cvv, cvvInput().text.toString()) }
        if (month != null) wait { assertEquals(month, expiryDateInput().monthEditText.text.toString()) }
        if (year != null) wait { assertEquals(year, expiryDateInput().yearEditText.text.toString()) }
        return this
    }

    fun validationStateIs(pan: Boolean? = null, cvv: Boolean? = null, month: Boolean? = null, year: Boolean? = null): CardFragmentTestUtils {
        if (pan != null) checkValidationState(panInput().mEditText, pan, "pan")
        if (cvv != null) checkValidationState(cvvInput(), cvv, "cvv")
        if (month != null) checkValidationState(expiryDateInput().monthEditText, month, "month")
        if (year != null) checkValidationState(expiryDateInput().yearEditText, year, "year")
        return this
    }

    fun hasNoBrand(): CardFragmentTestUtils {
        val resourceEntryName = activity().resources.getResourceEntryName(R.drawable.card_unknown_logo)
        wait { assertEquals(resourceEntryName, brandLogo().getTag(PANLayout.CARD_TAG)) }
        return this
    }

    fun hasBrand(cardBrand: CardBrand): CardFragmentTestUtils {
        wait { assertEquals(cardBrand.cardBrandName, brandLogo().getTag(PANLayout.CARD_TAG)) }
        return this
    }

}
package com.worldpay.access.checkout.sample.card.standard.testutil

import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.testutil.AbstractFragmentTestUtils
import com.worldpay.access.checkout.sample.testutil.UITestUtils.retrieveEnteredText
import com.worldpay.access.checkout.sample.testutil.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CardFragmentTestUtils(activityRule: ActivityTestRule<MainActivity>) : AbstractFragmentTestUtils(activityRule) {

    private fun panInput() = findById<AccessCheckoutEditText>(R.id.card_flow_text_pan)
    private fun cvcInput() = findById<AccessCheckoutEditText>(R.id.card_flow_text_cvc)
    private fun expiryDateInput() = findById<AccessCheckoutEditText>(R.id.card_flow_expiry_date)
    private fun submitButton() = findById<Button>(R.id.card_flow_btn_submit)
    private fun brandLogo() = findById<ImageView>(R.id.card_flow_brand_logo)
    private fun paymentsCvcSwitch() = findById<SwitchCompat>(R.id.card_flow_payments_cvc_switch)

    enum class Input {
        PAN, CVC, EXPIRY_DATE
    }

    fun isInInitialState(): CardFragmentTestUtils {
        attemptToCloseDialogIfPresent()
        progressBarNotVisible()
        enabledStateIs(pan = true, cvc = true, expiryDate = true, submitButton = false)
        cardDetailsAre(pan = "", cvc = "", expiryDate = "")
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
        enabledStateIs(pan = false, cvc = false, expiryDate = false, paymentsCvcSwitch = false, submitButton = false)
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

    fun attemptToCloseDialogIfPresent(): CardFragmentTestUtils {
        try {
            onView(withId(android.R.id.button1))
                .perform(click())
        } catch (e:Exception) {
            // no dialog is present
        }
        return this
    }

    fun closeDialog(): CardFragmentTestUtils {
        onView(withId(android.R.id.button1)).perform(click())
        return this
    }

    fun isInErrorState(pan: String? = null, cvc: String? = null, expiryDate: String? = null): CardFragmentTestUtils {
        progressBarNotVisible()
        enabledStateIs(pan = true, cvc = true, expiryDate = true, paymentsCvcSwitch = true, submitButton = true)
        cardDetailsAre(pan, cvc, expiryDate)
        return this
    }

    fun enabledStateIs(
        pan: Boolean? = null,
        cvc: Boolean? = null,
        expiryDate: Boolean? = null,
        paymentsCvcSwitch: Boolean? = null,
        submitButton: Boolean? = null
    ): CardFragmentTestUtils {
        val visibleMsg = "visibility state"
        val enableMsg = "enabled state"

        if (pan != null) {
            wait { assertTrue("PAN Input - $visibleMsg") { panInput().isVisible } }
            wait { assertEquals(pan, panInput().isEnabled, "PAN Input - $enableMsg") }
        }

        if (cvc != null) {
            wait { assertTrue("CVC Input - $visibleMsg") { cvcInput().isVisible } }
            wait { assertEquals(cvc, cvcInput().isEnabled, "CVC Input - $enableMsg") }
        }

        if (expiryDate != null) {
            wait { assertTrue("Exp Month Input - $visibleMsg") { expiryDateInput().isVisible } }
            wait { assertEquals(expiryDate, expiryDateInput().isEnabled, "Exp Date Input - $enableMsg") }
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
            Input.PAN -> uiObjectWithId(panInput().id).click()
            Input.CVC -> uiObjectWithId(cvcInput().id).click()
            Input.EXPIRY_DATE -> uiObjectWithId(expiryDateInput().id).click()
        }
        return this
    }

    fun enterCardDetails(
        pan: String? = null,
        cvc: String? = null,
        expiryDate: String? = null,
        assertText: Boolean = false
    ): CardFragmentTestUtils {
        if (pan != null) enterText(panInput(), pan)
        if (cvc != null) enterText(cvcInput(), cvc)
        if (expiryDate != null) enterText(expiryDateInput(), expiryDate)

        if (assertText) {
            cardDetailsAre(pan, cvc, expiryDate)
        }

        return this
    }

    fun clearCardDetails(
        pan: Boolean? = null,
        cvc: Boolean? = null,
        expiryDate: Boolean? = null
    ) {
        if (pan == true) clearText(panInput())
        if (expiryDate == true) clearText(expiryDateInput())
        if (cvc == true) clearText(cvcInput())
    }

    fun setCursorPositionOnPan(position: Int): CardFragmentTestUtils {
        setCursorPosition(panInput(), position, position)
        return this
    }

    fun cursorPositionIs(position: Int): CardFragmentTestUtils {
        wait { assertEquals(position, panInput().selectionEnd) }
        return this
    }

    fun cardDetailsAre(pan: String? = null, cvc: String? = null, expiryDate: String? = null): CardFragmentTestUtils {
        if (pan != null) wait { assertEquals(pan, retrieveEnteredText(panInput())) }
        if (cvc != null) wait { assertEquals(cvc, retrieveEnteredText(cvcInput())) }
        if (expiryDate != null) wait { assertEquals(expiryDate, retrieveEnteredText(expiryDateInput())) }
        return this
    }

    fun validationStateIs(
        pan: Boolean? = null,
        cvc: Boolean? = null,
        expiryDate: Boolean? = null
    ): CardFragmentTestUtils {
        if (pan != null) checkValidationState(panInput(), pan, "pan")
        if (cvc != null) checkValidationState(cvcInput(), cvc, "cvc")
        if (expiryDate != null) checkValidationState(expiryDateInput(), expiryDate, "expiry date")
        return this
    }

    fun hasNoBrand(): CardFragmentTestUtils {
        val resourceEntryName = activity().resources.getResourceEntryName(R.drawable.card_unknown_logo)
//        wait { assertEquals(resourceEntryName, brandLogo().getTag(R.integer.card_tag)) }
        wait { assertEquals(resourceEntryName, brandLogo().tag) }
        return this
    }

    fun hasBrand(cardBrand: CardBrand): CardFragmentTestUtils {
//        wait(maxWaitTimeInMillis = 20000) { assertEquals(cardBrand.cardBrandName, brandLogo().getTag(R.integer.card_tag)) }
        wait(maxWaitTimeInMillis = 20000) { assertEquals(cardBrand.cardBrandName, brandLogo().tag) }
        return this
    }
}

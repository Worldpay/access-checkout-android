package com.worldpay.access.checkout.card.testutil

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.view.isVisible
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.logging.LoggingUtils
import com.worldpay.access.checkout.testutil.UITestUtils.closeKeyboard
import com.worldpay.access.checkout.testutil.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.views.CardCVVText
import com.worldpay.access.checkout.views.CardExpiryTextLayout
import com.worldpay.access.checkout.views.PANLayout
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CardFragmentTestUtils(private val activity: Activity) {

    private val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

    private val panInput = activity.findViewById<PANLayout>(R.id.card_flow_text_pan)
    private val cvvInput = activity.findViewById<CardCVVText>(R.id.card_flow_text_cvv)
    private val expiryDateInput = activity.findViewById<CardExpiryTextLayout>(R.id.card_flow_text_exp)
    private val submitButton = activity.findViewById<Button>(R.id.card_flow_btn_submit)
    private val progressBar = uiObjectWithId(R.id.loading_bar)
    private val brandLogo = activity.findViewById<ImageView>(R.id.logo_view)

    private val successColor = getColor(activity.resources, R.color.SUCCESS, activity.theme)
    private val failColor = getColor(activity.resources, R.color.FAIL, activity.theme)

    fun isInInitialState(): CardFragmentTestUtils {
        progressBarNotVisible()
        enabledStateIs(pan = true, cvv = true, expiryMonth = true, expiryYear = true, submitButton = false)
        cardDetailsAre(pan = "", cvv = "", month = "", year = "")
        hasNoBrand()
        return this
    }

    fun requestIsInProgress(): CardFragmentTestUtils {
        progressBarIsVisible()
        enabledStateIs(pan = false, cvv = false, expiryMonth = false, expiryYear = false, submitButton = false)
        return this
    }

    fun isInErrorState(pan: String? = null, cvv: String? = null, month: String? = null, year: String? = null): CardFragmentTestUtils {
        progressBarNotVisible()
        enabledStateIs(pan = true, cvv = true, expiryMonth = true, expiryYear = true, submitButton = true)
        cardDetailsAre(pan, cvv, month, year)
        return this
    }

    fun enabledStateIs(pan: Boolean? = null, cvv: Boolean? = null, expiryMonth: Boolean? = null, expiryYear: Boolean? = null, submitButton: Boolean? = null): CardFragmentTestUtils {
        val visibleMsg = "visibility state"
        val enableMsg = "enabled state"

        if (pan != null) {
            wait { assertTrue("PAN Input - $visibleMsg") { panInput.mEditText.isVisible } }
            wait { assertEquals(pan, panInput.mEditText.isEnabled, "PAN Input - $enableMsg") }
        }

        if (cvv != null) {
            wait { assertTrue("CVV Input - $visibleMsg") { cvvInput.isVisible } }
            wait { assertEquals(cvv, cvvInput.isEnabled, "CVV Input - $enableMsg") }
        }

        if (expiryMonth != null) {
            wait { assertTrue("Exp Month Input - $visibleMsg") { expiryDateInput.monthEditText.isVisible } }
            wait { assertEquals(expiryMonth, expiryDateInput.monthEditText.isEnabled, "Exp Month Input - $enableMsg") }
        }

        if (expiryYear != null) {
            wait { assertTrue("Exp Year Input - $visibleMsg") { expiryDateInput.yearEditText.isVisible } }
            wait { assertEquals(expiryYear, expiryDateInput.yearEditText.isEnabled, "Exp Year Input - $enableMsg") }
        }

        if (submitButton != null) {
            wait { assertTrue("Submit Button - $visibleMsg") { this.submitButton.isVisible } }
            wait { assertEquals(submitButton, this.submitButton.isEnabled, "Submit Button - $enableMsg") }
        }

        return this
    }

    fun clickSubmitButton(): CardFragmentTestUtils {
        enabledStateIs(submitButton = true)
        uiObjectWithId(R.id.card_flow_btn_submit).click()
        return this
    }

    fun enterCardDetails(pan: String? = null, cvv: String? = null, month: String? = null, year: String? = null, assertText: Boolean = false): CardFragmentTestUtils {
        if (pan != null) enterText(panInput.mEditText, pan)
        if (cvv != null) enterText(cvvInput, cvv)
        if (month != null) enterText(expiryDateInput.monthEditText, month)
        if (year != null) enterText(expiryDateInput.yearEditText, year)

        if (assertText) {
            cardDetailsAre(pan, cvv, month, year)
        }

        return this
    }

    fun cardDetailsAre(pan: String? = null, cvv: String? = null, month: String? = null, year: String? = null): CardFragmentTestUtils {
        if (pan != null) wait { assertEquals(pan, panInput.mEditText.text.toString()) }
        if (cvv != null) wait { assertEquals(cvv, cvvInput.text.toString()) }
        if (month != null) wait { assertEquals(month, expiryDateInput.monthEditText.text.toString()) }
        if (year != null) wait { assertEquals(year, expiryDateInput.yearEditText.text.toString()) }
        return this
    }

    fun validationStateIs(pan: Boolean? = null, cvv: Boolean? = null, month: Boolean? = null, year: Boolean? = null): CardFragmentTestUtils {
        if (pan != null) checkValidationState(panInput.mEditText, pan)
        if (cvv != null) checkValidationState(cvvInput, cvv)
        if (month != null) checkValidationState(expiryDateInput.monthEditText, month)
        if (year != null) checkValidationState(expiryDateInput.yearEditText, year)
        return this
    }

    fun hasNoBrand(): CardFragmentTestUtils {
        val resourceEntryName = activity.resources.getResourceEntryName(R.drawable.card_unknown_logo)
        assertEquals(resourceEntryName, brandLogo.getTag(PANLayout.CARD_TAG))
        return this
    }

    fun hasBrand(cardBrand: CardBrand): CardFragmentTestUtils {
        for (i in 0..2) {
            try {
                assertEquals(cardBrand.cardBrandName, brandLogo.getTag(PANLayout.CARD_TAG))
            } catch (e: AssertionError) {
                Thread.sleep(100)
                continue
            }
            break
        }
        return this
    }

    private fun progressBarIsVisible(): CardFragmentTestUtils {
        assertTrue(progressBar.waitForExists(3000))
        closeKeyboard()
        return this
    }

    private fun progressBarNotVisible(): CardFragmentTestUtils {
        wait { assertTrue(progressBar.waitUntilGone(3000)) }
        return this
    }

    private fun checkValidationState(editText: EditText, isValid: Boolean) {
        if (isValid) {
            wait { assertEquals(successColor, editText.currentTextColor) }
        } else {
            wait { assertEquals(failColor, editText.currentTextColor) }
        }
    }

    private fun enterText(editText: EditText, text: String) {
        wait { assertTrue("${editText.id} - visibility state") { editText.isVisible } }
        wait { assertTrue("${editText.id} - enabled state") { editText.isEnabled } }
        wait { assertEquals(1.0f, editText.alpha, "${editText.id} - alpha state") }

        val editTextUI = uiObjectWithId(editText.id)
        editTextUI.click()
        editTextUI.text = text

        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun wait(maxSecondsInMillis: Int = 500, assertions: () -> Unit) {
        val pauseInterval = 100
        val maxTimes = maxSecondsInMillis / pauseInterval

        for (i in 0..maxTimes) {
            try {
                assertions()
            } catch (e: AssertionError) {
                Thread.sleep(100)
                LoggingUtils.debugLog(javaClass.simpleName,"Retrying assertion $assertions")
                continue
            }
            break
        }
    }

}
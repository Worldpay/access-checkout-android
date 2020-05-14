package com.worldpay.access.checkout.sample.card.testutil

import android.app.Activity.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.view.isVisible
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.testutil.CardFragmentTestUtils.Input.YEAR
import com.worldpay.access.checkout.sample.testutil.UITestUtils.closeKeyboard
import com.worldpay.access.checkout.sample.testutil.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.util.logging.LoggingUtils
import com.worldpay.access.checkout.views.CardCVVText
import com.worldpay.access.checkout.views.CardExpiryTextLayout
import com.worldpay.access.checkout.views.PANLayout
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CardFragmentTestUtils(private val activityRule: ActivityTestRule<MainActivity>) {

    private fun panInput() = findById<PANLayout>(R.id.card_flow_text_pan)
    private fun cvvInput() = findById<CardCVVText>(R.id.card_flow_text_cvv)
    private fun expiryDateInput() = findById<CardExpiryTextLayout>(R.id.card_flow_text_exp)
    private fun submitButton() = findById<Button>(R.id.card_flow_btn_submit)
    private fun progressBar() = uiObjectWithId(R.id.loading_bar)
    private fun brandLogo() = findById<ImageView>(R.id.logo_view)

    enum class Input {
        PAN, CVV, MONTH, YEAR
    }

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

    private fun progressBarIsVisible(): CardFragmentTestUtils {
        wait { assertTrue(progressBar().waitForExists(3000)) }
        closeKeyboard()
        return this
    }

    private fun progressBarNotVisible(): CardFragmentTestUtils {
        wait { assertTrue(progressBar().waitUntilGone(3000)) }
        return this
    }

    private fun checkValidationState(editText: EditText, isValid: Boolean, field: String) {
        if (isValid) {
            wait { assertEquals(color(R.color.SUCCESS), editText.currentTextColor, "$field field expected to be valid") }
        } else {
            wait { assertEquals(color(R.color.FAIL), editText.currentTextColor, "$field field expected to be invalid") }
        }
    }

    private fun enterText(editText: EditText, text: String) {
        wait { assertTrue("${editText.id} - visibility state") { editText.isVisible } }
        wait { assertTrue("${editText.id} - enabled state") { editText.isEnabled } }
        wait { assertEquals(1.0f, editText.alpha, "${editText.id} - alpha state") }

        val editTextUI = uiObjectWithId(editText.id)
        editTextUI.click()
        if (editTextUI.text != text) {
            editTextUI.text = text
        }

        val im = activity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun activity() = activityRule.activity

    private fun color(colorId: Int) = getColor(activity().resources, colorId, activity().theme)

    private fun <T: View> findById(id: Int): T {
        wait { assertNotNull(activity().findViewById<T>(id)) }
        return activity().findViewById(id)
    }

    private fun wait(maxWaitTimeInMillis: Int = 1000, assertions: () -> Unit) {
        val pauseInterval = 100
        val maxTimes = maxWaitTimeInMillis / pauseInterval

        for (i in 0..maxTimes) {
            try {
                assertions()
            } catch (exception: AssertionError) {
                if (i == maxTimes) {
                    val seconds = maxWaitTimeInMillis / 1000
                    throw AssertionError("Failed assertion after waiting $seconds seconds: ${exception.message}", exception)
                } else {
                    Thread.sleep(pauseInterval.toLong())
                    LoggingUtils.debugLog(javaClass.simpleName, "Retrying assertion $assertions")
                    continue
                }
            }
            break
        }
    }

}
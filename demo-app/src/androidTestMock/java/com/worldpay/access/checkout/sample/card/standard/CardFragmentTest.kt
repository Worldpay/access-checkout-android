package com.worldpay.access.checkout.sample.card.standard

import android.view.KeyEvent
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.pressKey
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.standard.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.VISA
import com.worldpay.access.checkout.sample.card.standard.testutil.CardFragmentTestUtils.Input.CVC
import com.worldpay.access.checkout.sample.card.standard.testutil.CardFragmentTestUtils.Input.EXPIRY_DATE
import com.worldpay.access.checkout.sample.card.standard.testutil.CardFragmentTestUtils.Input.PAN
import com.worldpay.access.checkout.sample.testutil.UITestUtils.onCardPanView
import com.worldpay.access.checkout.sample.testutil.UITestUtils.onCvcView
import com.worldpay.access.checkout.sample.testutil.UITestUtils.onExpiryDateView
import com.worldpay.access.checkout.sample.testutil.UITestUtils.reopenApp
import com.worldpay.access.checkout.sample.testutil.UITestUtils.repeatAction
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardFragmentTest : AbstractCardFragmentTest() {

    @Test
    fun shouldDisplayAllExpectedElementsInCorrectEnabledState() {
        cardFragmentTestUtils
            .isInInitialState()
            .enabledStateIs(pan = true, cvc = true, expiryDate = true, submitButton = false)
    }

    @Test
    fun shouldEnableSubmitButton_onValidCardData() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = "4111111111111111", cvc = "123", expiryDate = "1140")
            .cardDetailsAre(pan = "4111 1111 1111 1111", cvc = "123", expiryDate = "11/40")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldHaveCorrectAutofillHints() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasAutofillHints(R.id.card_flow_text_pan, arrayOf("creditCardNumber"))
            .hasAutofillHints(R.id.card_flow_text_cvc, arrayOf("creditCardSecurityCode"))
            .hasAutofillHints(R.id.card_flow_expiry_date, arrayOf("creditCardExpirationDate"))
    }

    @Test
    fun shouldMoveFocusToNextFieldUsingActionButton() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetail(PAN, "4111111111111111", false)

        onCardPanView().perform(pressImeActionButton())

        cardFragmentTestUtils
            .hasFocus(EXPIRY_DATE)
            .enterCardDetail(EXPIRY_DATE, "1140", false)

        onExpiryDateView().perform(pressImeActionButton())

        cardFragmentTestUtils
            .hasFocus(CVC)
            .enterCardDetail(CVC, "123", false)

        onCvcView().perform(pressImeActionButton())

        cardFragmentTestUtils.keyboardIsClosed()
    }

    @Test
    fun shouldMoveFocusToNextFieldWithoutEnteringDetailsUsingActionButton() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasFocus(PAN)

        onCardPanView().perform(pressImeActionButton())

        cardFragmentTestUtils
            .hasFocus(EXPIRY_DATE)

        onExpiryDateView().perform(pressImeActionButton())

        cardFragmentTestUtils
            .hasFocus(CVC)

        onCvcView().perform(pressImeActionButton())

        cardFragmentTestUtils.keyboardIsClosed()
    }

    @Test
    fun shouldMoveFocusToNextFieldWhenPartialDetailsAreEnteredUsingActionButton() {
        // enter partial details in PAN field
        cardFragmentTestUtils
            .isInInitialState()
            .hasFocus(PAN)
            .enterCardDetail(PAN, "4111111", false)

        // move to expiry date field via next action button
        onCardPanView().perform(pressImeActionButton())

        // verify focus is now on expiry date field and PAN field retains partial details
        cardFragmentTestUtils
            .validationStateIs(pan = false)
            .hasFocus(EXPIRY_DATE)
            .focusOn(PAN)

        // move to expiry date field via next action button again
        onCardPanView().perform(pressImeActionButton())

        // verify focus is now on expiry date field
        cardFragmentTestUtils
            .hasFocus(EXPIRY_DATE)

        // continue to CVC field without entering expiry date details
        onExpiryDateView().perform(pressImeActionButton())

        cardFragmentTestUtils
            .hasFocus(CVC)

        onCvcView().perform(pressImeActionButton())

        cardFragmentTestUtils.keyboardIsClosed()
    }

    @Test
    fun shouldMoveFocusToNextFieldWithoutEnteringDetailsUsingDirectionPad() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasFocus(PAN)

        onCardPanView().perform(pressKey(KeyEvent.KEYCODE_DPAD_DOWN))

        cardFragmentTestUtils
            .hasFocus(EXPIRY_DATE)

        onExpiryDateView().perform(pressKey(KeyEvent.KEYCODE_DPAD_RIGHT))

        cardFragmentTestUtils
            .hasFocus(CVC)

        onCvcView().perform(pressKey(KeyEvent.KEYCODE_DPAD_UP))

        cardFragmentTestUtils
            .hasFocus(PAN)

        onCardPanView().perform(pressKey(KeyEvent.KEYCODE_DPAD_RIGHT))

        cardFragmentTestUtils
            .hasFocus(EXPIRY_DATE)

        onExpiryDateView().perform(pressKey(KeyEvent.KEYCODE_DPAD_DOWN))

        cardFragmentTestUtils
            .hasFocus(CVC)

        onCvcView().perform(pressKey(KeyEvent.KEYCODE_DPAD_LEFT))

        cardFragmentTestUtils
            .hasFocus(EXPIRY_DATE)
    }

    @Test
    fun shouldMoveFocusToPreviousFieldWhenCaretIsAtTheStartUsingDirectionPad() {
        val cvc = "4111"
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetail(CVC, cvc, false)

        val count = cvc.length + 1 // move cursor to the start and one more to attempt to move focus

        repeatAction(times = count) {
            onCvcView().perform(pressKey(KeyEvent.KEYCODE_DPAD_LEFT))
        }

        cardFragmentTestUtils
            .hasFocus(EXPIRY_DATE)
    }

    @Test
    fun shouldMoveFocusToNextFieldWhenCaretIsAtTheEndUsingDirectionPad() {
        val pan = "4111 111"
        cardFragmentTestUtils
            .isInInitialState()
            .hasFocus(PAN)
            .enterCardDetail(PAN, pan, false)
            .setCursorPositionOnPan(0)

        val count = pan.length + 1 // move cursor to end and one more to attempt to move focus

        repeatAction(times = count) {
            onCardPanView().perform(pressKey(KeyEvent.KEYCODE_DPAD_RIGHT))
        }

        cardFragmentTestUtils
            .hasFocus(EXPIRY_DATE)
    }

    @Test
    fun shouldMoveFocusToNextFieldWhenPressingTabKey() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasFocus(PAN)

        onCardPanView().perform(pressKey(KeyEvent.KEYCODE_TAB))

        cardFragmentTestUtils
            .hasFocus(EXPIRY_DATE)

        onExpiryDateView().perform(pressKey(KeyEvent.KEYCODE_TAB))

        cardFragmentTestUtils
            .hasFocus(CVC)

        onCvcView().perform(pressKey(KeyEvent.KEYCODE_TAB))

        cardFragmentTestUtils
            .hasFocus(PAN)
    }

    @Test
    fun shouldDisableSubmitButton_onInvalidCardData() {
        // invalid expiry month
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = "4111111111111111", cvc = "123", expiryDate = "6040")
            .cardDetailsAre(pan = "4111 1111 1111 1111", cvc = "123", expiryDate = "06/04")
            .focusOn(CVC)
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvc = true, expiryDate = false)
            .enabledStateIs(submitButton = false)

        // partial pan number
        cardFragmentTestUtils
            .enterCardDetails(pan = "411111111111111", cvc = "123", expiryDate = "1140")
            .cardDetailsAre(pan = "4111 1111 1111 111", cvc = "123", expiryDate = "11/40")
            .hasBrand(VISA)
            .validationStateIs(pan = false, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = false)

        // invalid cvc
        cardFragmentTestUtils
            .enterCardDetails(pan = "4111111111111111", cvc = "12", expiryDate = "1140")
            .cardDetailsAre(pan = "4111 1111 1111 1111", cvc = "12", expiryDate = "11/40")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvc = false, expiryDate = true)
            .enabledStateIs(submitButton = false)
    }

    @Test
    fun shouldDisableSubmitButton_onInvalidCardData_afterValidDataIsAltered() {
        // enter correct card data
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = "4111111111111111", cvc = "123", expiryDate = "1140")
            .cardDetailsAre(pan = "4111 1111 1111 1111", cvc = "123", expiryDate = "11/40")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)

        // enter incorrect card data
        cardFragmentTestUtils
            .enterCardDetails(pan = "4111111111111111", cvc = "123", expiryDate = "6040")
            .cardDetailsAre(pan = "4111 1111 1111 1111", cvc = "123", expiryDate = "06/04")
            .hasBrand(VISA)
            .validationStateIs(pan = true, cvc = true, expiryDate = false)
            .enabledStateIs(submitButton = false)
    }

    @Test
    fun shouldKeepValidationStateOnFieldsWhenAppIsReopened() {

        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = "4", cvc = "12", expiryDate = "129")
            .focusOn(CVC)
            .validationStateIs(pan = false, cvc = false, expiryDate = false)
            .hasBrand(VISA)
            .enabledStateIs(submitButton = false)

        reopenApp()

        cardFragmentTestUtils
            .validationStateIs(pan = false, cvc = false, expiryDate = false)
            .hasBrand(VISA)
            .enabledStateIs(submitButton = false)
    }
}

package com.worldpay.access.checkout.sample.card.standard

import android.view.KeyEvent.KEYCODE_DEL
import androidx.test.espresso.action.ViewActions.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.worldpay.access.checkout.sample.card.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.AMEX_PAN_FORMATTED
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VALID_UNKNOWN_LUHN_FORMATTED
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.sample.card.standard.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.AMEX
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.VISA
import com.worldpay.access.checkout.sample.testutil.UITestUtils.onCardPanView
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PANFormattingUITest : AbstractCardFragmentTest() {

    @Test
    fun shouldFormatRecognisedNonAmexPan() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "41111")
            .hasBrand(VISA)
            .cardDetailsAre(pan = "4111 1")
            .enterCardDetails(pan = VISA_PAN)
            .validationStateIs(pan = true)
            .cardDetailsAre(pan = "4111 1111 1111 1111")
    }

    @Test
    fun shouldFormatUnrecognisedPan() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "11111")
            .hasNoBrand()
            .cardDetailsAre(pan = "1111 1")
            .enterCardDetails(pan = VALID_UNKNOWN_LUHN)
            .validationStateIs(pan = true)
            .hasNoBrand()
            .cardDetailsAre(pan = VALID_UNKNOWN_LUHN_FORMATTED)
    }

    @Test
    fun shouldFormatAmexPan() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "37178")
            .hasBrand(AMEX)
            .cardDetailsAre(pan = "3717 8")
            .enterCardDetails(pan = AMEX_PAN)
            .validationStateIs(pan = true)
            .cardDetailsAre(pan = AMEX_PAN_FORMATTED)
    }

    @Test
    fun shouldFormatAmexPanAndPlaceCursor_whenTyping() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "37177")
            .cardDetailsAre(pan = "3717 7")
            .setCursorPositionOnPan(3)

        onCardPanView()
            .perform(
                typeTextIntoFocusedView("2345678")
            )

        cardFragmentTestUtils
            .cardDetailsAre(pan = "3712 345678 77")
            .cursorPositionIs(12)
            .setCursorPositionOnPan(8)

        onCardPanView()
            .perform(
                pressKey(KEYCODE_DEL),
                pressKey(KEYCODE_DEL),
                pressKey(KEYCODE_DEL),
                pressKey(KEYCODE_DEL),
                pressKey(KEYCODE_DEL),
                pressImeActionButton()
            )

        cardFragmentTestUtils
            .cardDetailsAre(pan = "3767 877")
            .cursorPositionIs(2)
    }

    @Test
    fun shouldFormatNonAmexPanAndPlaceCursor_whenTyping() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "1234")
            .setCursorPositionOnPan(3)

        onCardPanView()
            .perform(
                typeTextIntoFocusedView("12345678")
            )

        cardFragmentTestUtils
            .cardDetailsAre(pan = "1231 2345 6784")
            .cursorPositionIs(13)
            .setCursorPositionOnPan(8)

        onCardPanView()
            .perform(
                pressKey(KEYCODE_DEL),
                pressKey(KEYCODE_DEL),
                pressKey(KEYCODE_DEL),
                pressKey(KEYCODE_DEL),
                pressKey(KEYCODE_DEL),
                pressImeActionButton()
            )

        cardFragmentTestUtils
            .cardDetailsAre(pan = "1256 784")
            .cursorPositionIs(2)
    }

    @Test
    fun shouldDeleteDigitBeforeSpace_whenDeletingSpace() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "12345")
            .cardDetailsAre(pan = "1234 5")
            .setCursorPositionOnPan(5)

        onCardPanView()
            .perform(
                pressKey(KEYCODE_DEL),
                pressImeActionButton()
            )

        cardFragmentTestUtils
            .cardDetailsAre(pan = "1235")
            .cursorPositionIs(3)
    }

    @Test
    fun shouldMoveCursorToAfterTheSpace_whenTypingInMiddleOfPanThatRequiresReformatting() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "12345")
            .cardDetailsAre(pan = "1234 5")
            .setCursorPositionOnPan(3)

        onCardPanView()
            .perform(
                typeTextIntoFocusedView("8")
            )

        cardFragmentTestUtils
            .cardDetailsAre(pan = "1238 45")
            .cursorPositionIs(5)
    }

    @Test
    fun shouldKeepCursorAfterNewlyEnteredDigit_whenTypingInMiddleOfPanThatDoesNotRequireReformatting() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "1234567")
            .cardDetailsAre(pan = "1234 567")
            .setCursorPositionOnPan(7)

        onCardPanView()
            .perform(
                typeTextIntoFocusedView("8")
            )

        cardFragmentTestUtils
            .cardDetailsAre(pan = "1234 5687")
            .cursorPositionIs(8)
    }

    @Test
    fun shouldKeepCursorAfterNewlyEnteredDigit_whenTypingAtTheEndOfPan() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "12345")
            .cardDetailsAre(pan = "1234 5")

        onCardPanView()
            .perform(
                typeTextIntoFocusedView("8")
            )

        cardFragmentTestUtils
            .cardDetailsAre(pan = "1234 58")
            .cursorPositionIs("1234 58".length)
    }
}

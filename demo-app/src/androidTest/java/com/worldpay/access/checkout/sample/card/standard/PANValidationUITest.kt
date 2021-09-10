package com.worldpay.access.checkout.sample.card.standard

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.worldpay.access.checkout.sample.card.CardNumberUtil.INVALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.PARTIAL_VISA
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.sample.card.standard.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.AMEX
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.VISA
import com.worldpay.access.checkout.sample.card.standard.testutil.CardFragmentTestUtils.Input.CVC
import com.worldpay.access.checkout.sample.card.standard.testutil.CardFragmentTestUtils.Input.EXPIRY_DATE
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PANValidationUITest : AbstractCardFragmentTest() {

    @Test
    fun shouldValidateValidVisaAsGreenTextWithVisaBrandIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = VISA_PAN)
            .validationStateIs(pan = true)
            .hasBrand(VISA)
    }

    @Test
    fun shouldValidateInvalidVisaAsRedTextWithVisaBrandIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "4024001728904375123")
            .hasBrand(VISA)
            .focusOn(CVC)
            .validationStateIs(pan = false)
    }

    @Test
    fun shouldValidateValidUnknownLuhnAsGreenTextWithNoBrandIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = VALID_UNKNOWN_LUHN)
            .validationStateIs(pan = true)
            .hasNoBrand()
    }

    @Test
    fun shouldLimitToMaxLengthWhenPastingLongString() {
        val pastedText = "123456789012345678901234567890"
        val pastedTextWith19DigitsAndSpaces = "1234 5678 9012 3456 789"

        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = pastedText)
            .cardDetailsAre(pan = pastedTextWith19DigitsAndSpaces)
            .cursorPositionIs(23)
    }

    @Test
    fun shouldValidatePanWhenFocusIsLostAndDisplayBrandImage() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "343434343434341")
            .hasBrand(AMEX)
            .focusOn(CVC)
            .validationStateIs(pan = false)
    }

    @Test
    fun shouldValidatePanWhenFocusIsLostAndDisplayBrandImage_unknownInvalidLuhn() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = INVALID_UNKNOWN_LUHN)
            .hasNoBrand()
            .focusOn(CVC)
            .validationStateIs(pan = false)
    }

    @Test
    fun shouldValidatePanAsFalseWhenPartialVisaEnteredAndFocusIsLost() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = PARTIAL_VISA)
            .hasBrand(VISA)
            .focusOn(EXPIRY_DATE)
            .validationStateIs(pan = false)
            .hasBrand(VISA)
    }
}

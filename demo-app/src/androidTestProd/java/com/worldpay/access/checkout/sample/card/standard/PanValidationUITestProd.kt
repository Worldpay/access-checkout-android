package com.worldpay.access.checkout.sample.card.standard

import com.worldpay.access.checkout.sample.card.CardNumberUtil.VISA_PAN_13_DIGITS
import com.worldpay.access.checkout.sample.card.standard.testutil.AbstractCardFragmentTestProd
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.VISA
import org.junit.Test

class PanValidationUITestProd : AbstractCardFragmentTestProd() {

    @Test
    fun shouldValidateValidVisa13DigitsAsGreenTextWithVisaBrandIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = VISA_PAN_13_DIGITS)
            .validationStateIs(pan = true)
            .hasBrand(VISA)
    }
}

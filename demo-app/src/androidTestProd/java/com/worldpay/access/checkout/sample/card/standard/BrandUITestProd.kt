package com.worldpay.access.checkout.sample.card.standard

import com.worldpay.access.checkout.sample.card.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.DINERS_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.DISCOVER_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.INVALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.JCB_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.MAESTRO_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.MASTERCARD_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.asPartial
import com.worldpay.access.checkout.sample.card.standard.testutil.AbstractCardFragmentTestProd
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.AMEX
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.DINERS
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.DISCOVER
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.JCB
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.MAESTRO
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.MASTERCARD
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.VISA
import com.worldpay.access.checkout.sample.card.standard.testutil.CardFragmentTestUtils.Input.CVC
import org.junit.Test

class BrandUITestProd : AbstractCardFragmentTestProd() {

    @Test
    fun shouldHaveNoBrandForPartialUnknownLuhnOnFocusChange() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = asPartial(INVALID_UNKNOWN_LUHN))
            .hasNoBrand()
            .focusOn(CVC)
            .validationStateIs(pan = false)
            .hasNoBrand()
    }

    @Test
    fun shouldChangeBrandImageWhenPanChanges() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = asPartial(MAESTRO_PAN))
            .hasBrand(MAESTRO)
            .enterCardDetails(pan = asPartial(JCB_PAN))
            .hasBrand(JCB)
    }

    // AMEX
    @Test
    fun shouldDisplayBrandImage_AMEX_ForCompletePan() = checkBrandImageFor(AMEX_PAN, isBrand = AMEX)

    @Test
    fun shouldDisplayBrandImage_AMEX_ForRange_34() {
        checkBrandImageFor("3400", isBrand = AMEX)
        checkBrandImageFor("3499", isBrand = AMEX)
    }

    @Test
    fun shouldDisplayBrandImage_AMEX_ForRange_37() {
        checkBrandImageFor("3700", isBrand = AMEX)
        checkBrandImageFor("3799", isBrand = AMEX)
    }

    // DINERS
    @Test
    fun shouldDisplayBrandImage_DINERS_ForCompletePan() = checkBrandImageFor(DINERS_PAN, isBrand = DINERS)

    @Test
    fun shouldDisplayBrandImage_DINERS_ForRange_300_305() {
        checkBrandImageFor("3000", isBrand = DINERS)
        checkBrandImageFor("3059", isBrand = DINERS)
    }

    @Test
    fun shouldDisplayBrandImage_DINERS_ForRange_3095() {
        checkBrandImageFor("30950", isBrand = DINERS)
        checkBrandImageFor("30959", isBrand = DINERS)
    }

    @Test
    fun shouldDisplayBrandImage_DINERS_ForRange_36() {
        checkBrandImageFor("360", isBrand = DINERS)
        checkBrandImageFor("369", isBrand = DINERS)
    }

    @Test
    fun shouldDisplayBrandImage_DINERS_ForRange_38() {
        checkBrandImageFor("380", isBrand = DINERS)
        checkBrandImageFor("389", isBrand = DINERS)
    }

    @Test
    fun shouldDisplayBrandImage_DINERS_ForRange_39() {
        checkBrandImageFor("390", isBrand = DINERS)
        checkBrandImageFor("399", isBrand = DINERS)
    }

    // DISCOVER
    @Test
    fun shouldDisplayBrandImage_DISCOVER_ForCompletePan() = checkBrandImageFor(DISCOVER_PAN, isBrand = DISCOVER)

    @Test
    fun shouldDisplayBrandImage_DISCOVER_ForRange_6011() {
        checkBrandImageFor("60110", isBrand = DISCOVER)
        checkBrandImageFor("60119", isBrand = DISCOVER)
    }

    @Test
    fun shouldDisplayBrandImage_DISCOVER_ForRange_644_649() {
        checkBrandImageFor("6440", isBrand = DISCOVER)
        checkBrandImageFor("6499", isBrand = DISCOVER)
    }

    @Test
    fun shouldDisplayBrandImage_DISCOVER_ForRange_65() {
        checkBrandImageFor("650", isBrand = DISCOVER)
        checkBrandImageFor("659", isBrand = DISCOVER)
    }

    // JCB
    @Test
    fun shouldDisplayBrandImage_JCB_ForCompletePan() = checkBrandImageFor(JCB_PAN, isBrand = JCB)

    @Test
    fun shouldDisplayBrandImage_JCB_ForRange_1800() {
        checkBrandImageFor("18000", isBrand = JCB)
        checkBrandImageFor("18009", isBrand = JCB)
    }

    @Test
    fun shouldDisplayBrandImage_JCB_ForRange_2131() {
        checkBrandImageFor("21310", isBrand = JCB)
        checkBrandImageFor("21319", isBrand = JCB)
    }

    @Test
    fun shouldDisplayBrandImage_JCB_ForRange_3088_3094() {
        checkBrandImageFor("30880", isBrand = JCB)
        checkBrandImageFor("30949", isBrand = JCB)
    }

    @Test
    fun shouldDisplayBrandImage_JCB_ForRange_3096_3102() {
        checkBrandImageFor("30960", isBrand = JCB)
        checkBrandImageFor("31029", isBrand = JCB)
    }

    @Test
    fun shouldDisplayBrandImage_JCB_ForRange_3112_3120() {
        checkBrandImageFor("31120", isBrand = JCB)
        checkBrandImageFor("31209", isBrand = JCB)
    }

    @Test
    fun shouldDisplayBrandImage_JCB_ForRange_3158_3159() {
        checkBrandImageFor("31580", isBrand = JCB)
        checkBrandImageFor("31599", isBrand = JCB)
    }

    @Test
    fun shouldDisplayBrandImage_JCB_ForRange_3337_3349() {
        checkBrandImageFor("33370", isBrand = JCB)
        checkBrandImageFor("33499", isBrand = JCB)
    }
    @Test
    fun shouldDisplayBrandImage_JCB_ForRange_352_358() {
        checkBrandImageFor("3520", isBrand = JCB)
        checkBrandImageFor("3589", isBrand = JCB)
    }

    // MAESTRO
    @Test
    fun shouldDisplayBrandImage_MAESTRO_ForCompletePan() = checkBrandImageFor(MAESTRO_PAN, isBrand = MAESTRO)

    @Test
    fun shouldDisplayBrandImage_MAESTRO_ForRange_493698() {
        checkBrandImageFor("4936980", isBrand = MAESTRO)
        checkBrandImageFor("4936989", isBrand = MAESTRO)
    }

    @Test
    fun shouldDisplayBrandImage_MAESTRO_ForRange_50000_50599() {
        checkBrandImageFor("500000", isBrand = MAESTRO)
        checkBrandImageFor("505999", isBrand = MAESTRO)
    }

    @Test
    fun shouldDisplayBrandImage_MAESTRO_ForRange_5060_5065() {
        checkBrandImageFor("50600", isBrand = MAESTRO)
        checkBrandImageFor("50659", isBrand = MAESTRO)
    }

    @Test
    fun shouldDisplayBrandImage_MAESTRO_ForRange_5066() {
        checkBrandImageFor("50660", isBrand = MAESTRO)
        checkBrandImageFor("50669", isBrand = MAESTRO)
    }

    @Test
    fun shouldDisplayBrandImage_MAESTRO_ForRange_50677_50679() {
        checkBrandImageFor("506770", isBrand = MAESTRO)
        checkBrandImageFor("506799", isBrand = MAESTRO)
    }

    @Test
    fun shouldDisplayBrandImage_MAESTRO_ForRange_50680_50699() {
        checkBrandImageFor("506800", isBrand = MAESTRO)
        checkBrandImageFor("506999", isBrand = MAESTRO)
    }

    @Test
    fun shouldDisplayBrandImage_MAESTRO_ForRange_50700_50899() {
        checkBrandImageFor("507000", isBrand = MAESTRO)
        checkBrandImageFor("507999", isBrand = MAESTRO)
    }

    @Test
    fun shouldDisplayBrandImage_MAESTRO_ForRange_56_59() {
        checkBrandImageFor("560", isBrand = MAESTRO)
        checkBrandImageFor("599", isBrand = MAESTRO)
    }

    @Test
    fun shouldDisplayBrandImage_MAESTRO_ForRange_63() {
        checkBrandImageFor("630", isBrand = MAESTRO)
        checkBrandImageFor("639", isBrand = MAESTRO)
    }

    @Test
    fun shouldDisplayBrandImage_MAESTRO_ForRange_67() {
        checkBrandImageFor("670", isBrand = MAESTRO)
        checkBrandImageFor("679", isBrand = MAESTRO)
    }

    // MASTERCARD
    @Test
    fun shouldDisplayBrandImage_MASTERCARD_ForCompletePan() = checkBrandImageFor(MASTERCARD_PAN, isBrand = MASTERCARD)

    @Test
    fun shouldDisplayBrandImage_MASTERCARD_ForRange_51_55() {
        checkBrandImageFor("510", isBrand = MASTERCARD)
        checkBrandImageFor("559", isBrand = MASTERCARD)
    }

    @Test
    fun shouldDisplayBrandImage_MASTERCARD_ForRange_22_27() {
        checkBrandImageFor("220", isBrand = MASTERCARD)
        checkBrandImageFor("279", isBrand = MASTERCARD)
    }

    // VISA
    @Test
    fun shouldDisplayBrandImage_VISA_ForCompletePan() = checkBrandImageFor(VISA_PAN, isBrand = VISA)

    @Test
    fun shouldDisplayBrandImage_VISA_ForRange4() {
        checkBrandImageFor("40", isBrand = VISA)
        checkBrandImageFor("49", isBrand = VISA)
    }

    // This test covers an edge case of a partial pan that looks like a visa pan
    @Test
    fun shouldDisplayBrandImage_MAESTRO_For493698() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "493698")
            .hasBrand(MAESTRO)
    }

    private fun checkBrandImageFor(pan: String, isBrand: CardBrand) {
        clearPan()

        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = pan)
            .hasBrand(isBrand)
    }
}

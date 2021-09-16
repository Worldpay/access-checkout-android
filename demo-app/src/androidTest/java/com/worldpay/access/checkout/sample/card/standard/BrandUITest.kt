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
import com.worldpay.access.checkout.sample.card.standard.testutil.AbstractCardFragmentTest
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

class BrandUITest : AbstractCardFragmentTest() {

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
    fun shouldDisplayAmexBrandImage() = checkBrandImageFor(AMEX_PAN, AMEX)

    @Test
    fun shouldDisplayVisaBrandImage() = checkBrandImageFor(VISA_PAN, VISA)

    @Test
    fun shouldDisplayMastercardBrandImage() = checkBrandImageFor(MASTERCARD_PAN, MASTERCARD)

    @Test
    fun shouldDisplayDinersBrandImage() = checkBrandImageFor(DINERS_PAN, DINERS)

    @Test
    fun shouldDisplayDiscoverBrandImage() = checkBrandImageFor(DISCOVER_PAN, DISCOVER)

    @Test
    fun shouldDisplayJCBBrandImage() = checkBrandImageFor(JCB_PAN, JCB)

    @Test
    fun shouldDisplayMaestroBrandImage() = checkBrandImageFor(MAESTRO_PAN, MAESTRO)

    @Test
    fun shouldDisplayAmexBrandImageForPartialPan() = checkBrandImageFor(asPartial(AMEX_PAN), AMEX)

    @Test
    fun shouldDisplayVisaBrandImageForPartialPan() = checkBrandImageFor(asPartial(VISA_PAN), VISA)

    @Test
    fun shouldDisplayMastercardBrandImageForPartialPan() = checkBrandImageFor(asPartial(MASTERCARD_PAN), MASTERCARD)

    @Test
    fun shouldDisplayDinersBrandImageForPartialPan() = checkBrandImageFor(asPartial(DINERS_PAN), DINERS)

    @Test
    fun shouldDisplayDiscoverBrandImageForPartialPan() = checkBrandImageFor(asPartial(DISCOVER_PAN), DISCOVER)

    @Test
    fun shouldDisplayJCBBrandImageForPartialPan() = checkBrandImageFor(asPartial(JCB_PAN), JCB)

    @Test
    fun shouldDisplayMaestroBrandImageForPartialPan() = checkBrandImageFor(asPartial(MAESTRO_PAN), MAESTRO)

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

    private fun checkBrandImageFor(pan: String, brand: CardBrand) {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = pan)
            .hasBrand(brand)
    }
}

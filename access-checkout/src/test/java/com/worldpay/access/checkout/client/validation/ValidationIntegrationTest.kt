package com.worldpay.access.checkout.client.validation

import com.nhaarman.mockitokotlin2.verify
import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.toCardBrand
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import org.junit.Test

class ValidationIntegrationTest: AbstractValidationIntegrationTest() {

    @Test
    fun `should call each listener function as each input is filled and then finally call the onValidationSuccess function`() {
        initialiseWithoutAcceptedCardBrands()

        pan.setText(VISA_PAN)
        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandChange(toCardBrand(VISA_BRAND))

        cvc.setText("1234")
        verify(cardValidationListener).onCvcValidated(true)

        expiryDate.setText("1229")
        verify(cardValidationListener).onExpiryDateValidated(true)

        verify(cardValidationListener).onValidationSuccess()
    }

}

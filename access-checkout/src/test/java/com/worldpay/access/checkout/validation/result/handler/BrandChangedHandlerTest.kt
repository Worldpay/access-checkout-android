package com.worldpay.access.checkout.validation.result.handler

import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutBrandChangedListener
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.validation.transformers.ToCardBrandTransformer
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class BrandChangedHandlerTest {

    private lateinit var brandChangedHandler: BrandChangedHandler

    private lateinit var validationListener: AccessCheckoutBrandChangedListener
    private lateinit var toCardBrandTransformer: ToCardBrandTransformer

    @Before
    fun setup() {
        validationListener = mock()
        toCardBrandTransformer = ToCardBrandTransformer()

        brandChangedHandler = BrandChangedHandler(
            validationListener = validationListener,
            toCardBrandTransformer = toCardBrandTransformer
        )
    }

    @Test
    fun `should notify brand changed when null is passed`() {
        brandChangedHandler.handle(null)

        verify(validationListener).onBrandChange(null)
    }

    @Test
    fun `should notify brand changed when brand is passed`() {
        val cardBrand = toCardBrandTransformer.transform(VISA_BRAND)

        brandChangedHandler.handle(VISA_BRAND)

        verify(validationListener).onBrandChange(cardBrand)
    }
}

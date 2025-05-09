package com.worldpay.access.checkout.validation.result.handler

import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutBrandsChangedListener
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.validation.transformers.ToCardBrandTransformer
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class BrandsChangedHandlerTest {

    private lateinit var brandsChangedHandler: BrandsChangedHandler

    private lateinit var validationListener: AccessCheckoutBrandsChangedListener
    private lateinit var toCardBrandTransformer: ToCardBrandTransformer

    @Before
    fun setup() {
        validationListener = mock()
        toCardBrandTransformer = ToCardBrandTransformer()

        brandsChangedHandler = BrandsChangedHandler(
            validationListener = validationListener,
            toCardBrandTransformer = toCardBrandTransformer
        )
    }

    @Test
    fun `should notify brand changed when emptylist is passed`() {
        brandsChangedHandler.handle(emptyList())

        verify(validationListener).onBrandsChange(emptyList())
    }

    @Test
    fun `should notify brand changed when brand is passed`() {
        val cardBrand = toCardBrandTransformer.transform(VISA_BRAND)

        brandsChangedHandler.handle(listOf(VISA_BRAND))

        verify(validationListener).onBrandsChange(listOf(cardBrand!!))
    }
}

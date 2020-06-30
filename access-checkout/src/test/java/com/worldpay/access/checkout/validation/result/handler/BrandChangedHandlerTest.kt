package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutBrandChangedListener
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.validation.transformers.ToCardBrandTransformer
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito

class BrandChangedHandlerTest {

    private lateinit var brandChangedHandler: BrandChangedHandler

    private lateinit var validationListener: AccessCheckoutBrandChangedListener
    private val lifecycleOwner = mock<LifecycleOwner>()
    private val lifecycle = mock<Lifecycle>()
    private lateinit var toCardBrandTransformer : ToCardBrandTransformer

    @Before
    fun setup() {
        BDDMockito.given(lifecycleOwner.lifecycle).willReturn(lifecycle)
        validationListener = mock()
        toCardBrandTransformer = ToCardBrandTransformer()

        brandChangedHandler = BrandChangedHandler(
            validationListener = validationListener,
            toCardBrandTransformer = toCardBrandTransformer,
            lifecycleOwner = lifecycleOwner
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

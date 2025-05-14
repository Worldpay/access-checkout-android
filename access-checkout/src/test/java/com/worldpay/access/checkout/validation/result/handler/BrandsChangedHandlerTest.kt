package com.worldpay.access.checkout.validation.result.handler

import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutBrandsChangedListener
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.validation.transformers.ToCardBrandTransformer
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class BrandsChangedHandlerTest {

    private lateinit var brandsChangedHandler: BrandsChangedHandler
    private lateinit var validationListener: AccessCheckoutBrandsChangedListener
    private lateinit var toCardBrandTransformer: ToCardBrandTransformer

    @Before
    fun setup() {
        validationListener = mock()
        toCardBrandTransformer = mock()

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
        val remoteCardBrand = mock<RemoteCardBrand>()
        val cardBrand = mock<CardBrand>()
        whenever(toCardBrandTransformer.transform(remoteCardBrand)).thenReturn(cardBrand)

        val remoteBrands = listOf(remoteCardBrand)
        brandsChangedHandler.handle(remoteBrands)

        verify(validationListener).onBrandsChange(listOf(cardBrand))
    }

    @Test
    fun `should notify brand changed with emptyList when brand transformer returns null`() {
        val remoteCardBrand = mock<RemoteCardBrand>()
        whenever(toCardBrandTransformer.transform(remoteCardBrand)).thenReturn(null)

        val remoteBrands = listOf(remoteCardBrand)
        brandsChangedHandler.handle(remoteBrands)

        verify(validationListener).onBrandsChange(emptyList())
    }
}

package com.worldpay.access.checkout.validation.result.handler

import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutBrandsChangedListener
import com.worldpay.access.checkout.validation.transformers.ToCardBrandTransformer

internal class BrandsChangedHandler(
    private val validationListener: AccessCheckoutBrandsChangedListener,
    private val toCardBrandTransformer: ToCardBrandTransformer
) {

    fun handle(remoteCardBrands: List<RemoteCardBrand>) {
        val cardBrands = remoteCardBrands.mapNotNull { toCardBrandTransformer.transform(it)}
        validationListener.onBrandsChange(cardBrands)
    }
}

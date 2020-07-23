package com.worldpay.access.checkout.validation.result.handler

import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutBrandChangedListener
import com.worldpay.access.checkout.validation.transformers.ToCardBrandTransformer

internal class BrandChangedHandler(
    private val validationListener: AccessCheckoutBrandChangedListener,
    private val toCardBrandTransformer: ToCardBrandTransformer
) {

    fun handle(remoteCardBrand : RemoteCardBrand?) {
        val cardBrand = toCardBrandTransformer.transform(remoteCardBrand)
        validationListener.onBrandChange(cardBrand)
    }

}

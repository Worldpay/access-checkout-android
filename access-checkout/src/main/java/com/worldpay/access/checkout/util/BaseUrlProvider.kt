package com.worldpay.access.checkout.util

interface IBaseUrlProvider {
    val CARD_BIN_SERVICE: String
}

class BaseUrlProviders : IBaseUrlProvider {
    override val CARD_BIN_SERVICE: String
        get() = "https://hpp-sandbox.worldpay.com"
}

internal object BaseUrlProvider {
    var instance: IBaseUrlProvider = BaseUrlProviders()
}
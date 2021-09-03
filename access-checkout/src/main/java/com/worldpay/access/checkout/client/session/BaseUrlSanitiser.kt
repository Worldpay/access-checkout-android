package com.worldpay.access.checkout.client.session

internal object BaseUrlSanitiser {

    fun sanitise(baseUrl: String?): String? {
        if (baseUrl == null || baseUrl.takeLast(1) != "/") {
            return baseUrl
        }

        return baseUrl.dropLast(1)
    }
}

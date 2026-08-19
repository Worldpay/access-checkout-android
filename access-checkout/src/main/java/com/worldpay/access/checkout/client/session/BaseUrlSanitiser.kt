package com.worldpay.access.checkout.client.session

import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException

internal object BaseUrlSanitiser {

    private val ALLOWED_PATTERNS = listOf(
        Regex("^https://([a-zA-Z0-9\\-]+.)*worldpay\\.com$"),
        Regex("^https?://localhost(:\\d+)?$"),
        Regex("^https?://127\\.0\\.0\\.1(:\\d+)?$")
    )

    fun sanitise(baseUrl: String?): String? {
        if (baseUrl == null) {
            return baseUrl
        }

        val urlWithoutTrailingSlash = baseUrl.trimEnd('/')

        val matches = ALLOWED_PATTERNS.any { it.matches(urlWithoutTrailingSlash) }
        if (!matches) {
            throw AccessCheckoutException(
                "base url '$baseUrl' is not permitted"
            )
        }

        return urlWithoutTrailingSlash
    }
}

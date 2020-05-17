package com.worldpay.access.checkout.session.api.client

import org.junit.Test
import kotlin.test.assertEquals

class HeadersTest {

    @Test
    fun `should return expected values for headers`() {
        assertEquals("application/vnd.worldpay.verified-tokens-v1.hal+json", VERIFIED_TOKENS_MEDIA_TYPE)
        assertEquals("application/vnd.worldpay.sessions-v1.hal+json", SESSIONS_MEDIA_TYPE)
        assertEquals("access-checkout-android/", PRODUCT_NAME)
        assertEquals("Content-Type", CONTENT_TYPE_HEADER)
        assertEquals("Accept", ACCEPT_HEADER)
        assertEquals("X-WP-SDK", WP_SDK_PRODUCT_HEADER)
    }

}
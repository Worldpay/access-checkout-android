package com.worldpay.access.checkout.cardbin.client

import com.worldpay.access.checkout.cardbin.api.client.WP_API_VERSION
import com.worldpay.access.checkout.cardbin.api.client.WP_API_VERSION_VALUE
import com.worldpay.access.checkout.cardbin.api.client.WP_CALLER_ID
import com.worldpay.access.checkout.cardbin.api.client.WP_CALLER_ID_VALUE
import kotlin.test.assertEquals
import org.junit.Test

class HeadersTest {

    @Test
    fun `should return expected values for headers`() {
        assertEquals("WP-Api-Version", WP_API_VERSION)
        assertEquals("1", WP_API_VERSION_VALUE)
        assertEquals("WP-CallerId", WP_CALLER_ID)
        assertEquals("checkout", WP_CALLER_ID_VALUE)
    }
}

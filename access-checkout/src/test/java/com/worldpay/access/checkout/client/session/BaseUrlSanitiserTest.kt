package com.worldpay.access.checkout.client.session

import org.junit.Assert
import org.junit.Test

class BaseUrlSanitiserTest {

    @Test
    fun `should be able to remove trailing slash`() {
        Assert.assertEquals("http://localhost:123", BaseUrlSanitiser.sanitise("http://localhost:123/"))
    }

    @Test
    fun `should be able to accept null baseUrl`() {
        Assert.assertEquals(null, BaseUrlSanitiser.sanitise(null))
    }

    @Test
    fun `should be able to not change correct url`() {
        Assert.assertEquals("http://localhost:123", BaseUrlSanitiser.sanitise("http://localhost:123"))
    }
}

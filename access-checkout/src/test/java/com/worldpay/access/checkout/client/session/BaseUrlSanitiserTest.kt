package com.worldpay.access.checkout.client.session

import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.session.BaseUrlSanitiser.sanitise
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BaseUrlSanitiserTest {

    @Test
    fun `should accept null baseUrl`() {
        assertEquals(null, sanitise(null))
    }

    @Test
    fun `should remove trailing slash from valid localhost URL`() {
        assertEquals("http://localhost:123", sanitise("http://localhost:123/"))
    }

    @Test
    fun `should remove trailing slash from valid loopback URL`() {
        assertEquals("http://127.0.0.1:123", sanitise("http://127.0.0.1:123/"))
    }

    @Test
    fun `should remove trailing slash from valid worldpay URL`() {
        assertEquals("https://access.worldpay.com", sanitise("https://access.worldpay.com/"))
    }

    @Test
    fun `should not change valid url`() {
        assertEquals("http://localhost:123", sanitise("http://localhost:123"))
    }

    // tests for whitelisting of worldpay.com domains (https only, any subdomain)
    @Test
    fun `should accept https with single subdomain on worldpay dot com`() {
        val url = "https://access.worldpay.com"
        assertEquals(url, sanitise(url))
    }

    @Test
    fun `should accept https with multi-part subdomain on worldpay dot com`() {
        val url = "https://try.access.worldpay.com"
        assertEquals(url, sanitise(url))
    }

    @Test
    fun `should accept https with hyphenated subdomain on worldpay dot com`() {
        val url = "https://my-service.worldpay.com"
        assertEquals(url, sanitise(url))
    }

    @Test
    fun `should accept worldpay dot com without subdomain`() {
        val url = "https://worldpay.com"
        assertEquals(url, sanitise(url))
    }

    @Test
    fun `should reject non http protocol on worldpay dot com`() {
        val ex = assertFailsWith<AccessCheckoutException> {
            sanitise("ftp://access.worldpay.com")
        }
        assertEquals("base url 'ftp://access.worldpay.com' is not permitted", ex.message)
    }

    @Test
    fun `should reject http on subdomain of worldpay dot com`() {
        val ex = assertFailsWith<AccessCheckoutException> {
            sanitise("http://access.worldpay.com")
        }
        assertEquals("base url 'http://access.worldpay.com' is not permitted", ex.message)
    }

    @Test
    fun `should reject subdomain of worldpay dot com with a path`() {
        val ex = assertFailsWith<AccessCheckoutException> {
            sanitise("http://access.worldpay.com/some-path")
        }
        assertEquals("base url 'http://access.worldpay.com/some-path' is not permitted", ex.message)
    }

    @Test
    fun `should reject a different top level domain that contains worldpay dot com`() {
        val ex = assertFailsWith<AccessCheckoutException> {
            sanitise("https://evil.worldpay.com.attacker.com")
        }
        assertEquals(
            "base url 'https://evil.worldpay.com.attacker.com' is not permitted",
            ex.message
        )
    }

    // tests for whitelisting of localhost (http or https, any port) ----
    @Test
    fun `should accept http localhost with port`() {
        val url = "http://localhost:8080"
        assertEquals(url, sanitise(url))
    }

    @Test
    fun `should accept https localhost with port`() {
        val url = "https://localhost:8443"
        assertEquals(url, sanitise(url))
    }

    @Test
    fun `should accept localhost min and max numeric port`() {
        var url = "http://localhost:1"
        assertEquals(url, sanitise(url))

        url = "https://localhost:65535"
        assertEquals(url, sanitise(url))
    }

    @Test
    fun `should accept localhost without port using http`() {
        val url = "http://localhost"
        assertEquals(url, sanitise(url))
    }

    @Test
    fun `should accept localhost without port using https`() {
        val url = "https://localhost"
        assertEquals(url, sanitise(url))
    }

    @Test
    fun `should reject URL where the domain contains localhost`() {
        val ex = assertFailsWith<AccessCheckoutException> {
            sanitise("http://some-url-localhost-and-something-else")
        }
        assertEquals("base url 'http://some-url-localhost-and-something-else' is not permitted", ex.message)
    }

    @Test
    fun `should reject localhost with a path`() {
        val ex = assertFailsWith<AccessCheckoutException> {
            sanitise("http://localhost/some-path")
        }
        assertEquals("base url 'http://localhost/some-path' is not permitted", ex.message)
    }

    @Test
    fun `should reject localhost with non-numeric port`() {
        val ex = assertFailsWith<AccessCheckoutException> {
            sanitise("http://localhost:abc")
        }
        assertEquals("base url 'http://localhost:abc' is not permitted", ex.message)
    }

    // ---- 127.0.0.1 ----
    @Test
    fun `should reject URL where the domain contains loopback address`() {
        val ex = assertFailsWith<AccessCheckoutException> {
            sanitise("http://some-url-127.0.0.1-and-something-else")
        }
        assertEquals("base url 'http://some-url-127.0.0.1-and-something-else' is not permitted", ex.message)
    }

    @Test
    fun `should accept http loopback address with port`() {
        val url = "http://127.0.0.1:8080"
        assertEquals(url, sanitise(url))
    }

    @Test
    fun `should accept https loopback address with port`() {
        val url = "https://127.0.0.1:8443"
        assertEquals(url, sanitise(url))
    }

    @Test
    fun `should accept loopback address min and max numeric port`() {
        var url = "http://127.0.0.1:1"
        assertEquals(url, sanitise(url))

        url = "https://127.0.0.1:65535"
        assertEquals(url, sanitise(url))
    }

    @Test
    fun `should accept loopback address without port using http`() {
        val url = "http://127.0.0.1"
        assertEquals(url, sanitise(url))
    }

    @Test
    fun `should accept loopback address without port using https`() {
        val url = "https://127.0.0.1"
        assertEquals(url, sanitise(url))
    }

    @Test
    fun `should reject loopback address with a path`() {
        val ex = assertFailsWith<AccessCheckoutException> {
            sanitise("http://127.0.0.1/some-path")
        }
        assertEquals("base url 'http://127.0.0.1/some-path' is not permitted", ex.message)
    }

    @Test
    fun `should reject loopback address with non-numeric port`() {
        val ex = assertFailsWith<AccessCheckoutException> {
            sanitise("http://127.0.0.1:abc")
        }
        assertEquals("base url 'http://127.0.0.1:abc' is not permitted", ex.message)
    }
}
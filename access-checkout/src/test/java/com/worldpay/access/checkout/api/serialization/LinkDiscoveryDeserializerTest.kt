package com.worldpay.access.checkout.api.serialization

import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class LinkDiscoveryDeserializerTest {

    private val namespace = "namespace"

    private val linkDiscoveryRootDeserializer = LinkDiscoveryDeserializer(namespace)

    @Test
    fun givenEmptyResponseThenShouldThrowDeserializationException() {
        val exception = assertFailsWith<AccessCheckoutException> {
            linkDiscoveryRootDeserializer.deserialize("")
        }

        assertEquals("Cannot deserialize empty string", exception.message)
    }

    @Test
    fun givenBadJsonStringThenShouldThrowDeserializationException() {
        val json = "abc"

        val exception = assertFailsWith<AccessCheckoutException> {
            linkDiscoveryRootDeserializer.deserialize(json)
        }

        assertEquals("Cannot interpret json: $json", exception.message)
    }

    @Test
    fun givenJsonStringWithMissingObjectThenShouldThrowDeserializationException() {
        val json = "{ }"

        val exception = assertFailsWith<AccessCheckoutException> {
            linkDiscoveryRootDeserializer.deserialize(json)
        }

        assertEquals("Missing object: '_links'", exception.message)
    }

    @Test
    fun givenJsonStringWithMissingPropertyThenShouldThrowDeserializationException() {
        val json = "{ \"_links\": { \"$namespace\": { } } }"

        val exception = assertFailsWith<AccessCheckoutException> {
            linkDiscoveryRootDeserializer.deserialize(json)
        }

        assertEquals("Missing property: 'href'", exception.message)
    }

    @Test
    fun givenJsonStringWithInvalidStringTypeThenShouldThrowDeserializationException() {
        val json = "{ \"_links\": { \"$namespace\": { \"href\": true } } }"

        val exception = assertFailsWith<AccessCheckoutException> {
            linkDiscoveryRootDeserializer.deserialize(json)
        }

        assertEquals("Invalid property type: 'href', expected 'String'", exception.message)
    }

    @Test
    fun givenValidRootResponseStringThenShouldSuccessfullyDeserializeToVerifiedTokensServiceURL() {
        val rootResponse =
            """{
                    "_links": {
                        "payments:authorize": {
                            "href": "https://localhost:8443/payments/authorizations"
                        },
                        "service:payments": {
                            "href": "https://localhost:8443/payments"
                        },
                        "service:tokens": {
                            "href": "https://localhost:8443/tokens"
                        },
                        "service:verifiedTokens": {
                            "href": "https://localhost:8443/verifiedTokens"
                        },
                        "curies": [
                            {
                                "href": "https://localhost:8443/rels/payments/{rel}",
                                "name": "payments",
                                "templated": true
                            }
                        ]
                    }
                }"""

        val deserializedResponse = LinkDiscoveryDeserializer("service:verifiedTokens").deserialize(rootResponse)

        assertEquals("https://localhost:8443/verifiedTokens", deserializedResponse)
    }

    @Test
    fun givenValidServiceRootResponseStringThenShouldSuccessfullyDeserializeToSessionsURL() {
        val topLevelServiceResourceResponse = """{
                "_links": {
                    "verifiedTokens:recurring": {
                        "href": "https://localhost:8443/verifiedTokens/recurring"
                    },
                    "verifiedTokens:cardOnFile": {
                        "href": "https://localhost:8443/verifiedTokens/cardOnFile"
                    },
                    "verifiedTokens:sessions": {
                        "href": "https://localhost:8443/verifiedTokens/sessions"
                    },
                "resourceTree": {
                    "href": "https://localhost:8443/rels/verifiedTokens/resourceTree.json"
                },
                "curies": [{
                    "href": "https://localhost:8443/rels/verifiedTokens/{rel}.json",
                    "name": "verifiedTokens",
                    "templated": true
                }]
            }
        }"""

        val deserializedResponse =
            LinkDiscoveryDeserializer("verifiedTokens:sessions").deserialize(topLevelServiceResourceResponse)

        assertEquals("https://localhost:8443/verifiedTokens/sessions", deserializedResponse)
    }
}

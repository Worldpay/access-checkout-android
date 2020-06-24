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
                            "href": "http://localhost/payments/authorizations"
                        },
                        "service:payments": {
                            "href": "http://localhost/payments"
                        },
                        "service:tokens": {
                            "href": "http://localhost/tokens"
                        },
                        "service:verifiedTokens": {
                            "href": "http://localhost/verifiedTokens"
                        },
                        "curies": [
                            {
                                "href": "http://localhost/rels/payments/{rel}",
                                "name": "payments",
                                "templated": true
                            }
                        ]
                    }
                }"""

        val deserializedResponse = LinkDiscoveryDeserializer("service:verifiedTokens").deserialize(rootResponse)

        assertEquals("http://localhost/verifiedTokens", deserializedResponse)
    }

    @Test
    fun givenValidServiceRootResponseStringThenShouldSuccessfullyDeserializeToSessionsURL() {
        val topLevelServiceResourceResponse = """{
                "_links": {
                    "verifiedTokens:recurring": {
                        "href": "http://localhost/verifiedTokens/recurring"
                    },
                    "verifiedTokens:cardOnFile": {
                        "href": "http://localhost/verifiedTokens/cardOnFile"
                    },
                    "verifiedTokens:sessions": {
                        "href": "http://localhost/verifiedTokens/sessions"
                    },
                "resourceTree": {
                    "href": "http://localhost/rels/verifiedTokens/resourceTree.json"
                },
                "curies": [{
                    "href": "http://localhost/rels/verifiedTokens/{rel}.json",
                    "name": "verifiedTokens",
                    "templated": true
                }]
            }
        }"""

        val deserializedResponse =
            LinkDiscoveryDeserializer("verifiedTokens:sessions").deserialize(topLevelServiceResourceResponse)

        assertEquals("http://localhost/verifiedTokens/sessions", deserializedResponse)
    }
}

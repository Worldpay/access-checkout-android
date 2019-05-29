package com.worldpay.access.checkout.api.serialization

import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDeserializationException
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LinkDiscoveryDeserializerTest {

    private val namespace = "namespace"

    private val linkDiscoveryRootDeserializer = LinkDiscoveryDeserializer(namespace)

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun givenEmptyResponseThenShouldThrowDeserializationException() {
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Cannot deserialize empty string")

        linkDiscoveryRootDeserializer.deserialize("")
    }

    @Test
    fun givenBadJsonStringThenShouldThrowDeserializationException() {
        val json = "abc"
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Cannot interpret json: $json")

        linkDiscoveryRootDeserializer.deserialize(json)
    }

    @Test
    fun givenJsonStringWithMissingObjectThenShouldThrowDeserializationException() {
        val json = "{ }"
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Missing object: '_links'")

        linkDiscoveryRootDeserializer.deserialize(json)
    }

    @Test
    fun givenJsonStringWithMissingPropertyThenShouldThrowDeserializationException() {
        val json = "{ \"_links\": { \"$namespace\": { } } }"
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Missing property: 'href'")

        linkDiscoveryRootDeserializer.deserialize(json)
    }

    @Test
    fun givenJsonStringWithInvalidStringTypeThenShouldThrowDeserializationException() {
        val json = "{ \"_links\": { \"$namespace\": { \"href\": true } } }"
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Invalid property type: 'href', expected 'String'")

        linkDiscoveryRootDeserializer.deserialize(json)
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
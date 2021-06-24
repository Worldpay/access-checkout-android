package com.worldpay.access.checkout.session.api.serialization

import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.session.api.response.SessionResponse.Links
import com.worldpay.access.checkout.session.api.response.SessionResponse.Links.Curies
import com.worldpay.access.checkout.session.api.response.SessionResponse.Links.Endpoints
import kotlin.test.assertFailsWith
import org.junit.Assert.assertEquals
import org.junit.Test

class CardSessionResponseDeserializerTest {

    private val sessionResponseDeserializer = CardSessionResponseDeserializer()

    @Test
    fun givenEmptyResponseThenShouldThrowDeserializationException() {
        val exception = assertFailsWith<AccessCheckoutException> {
            sessionResponseDeserializer.deserialize("")
        }

        assertEquals("Cannot deserialize empty string", exception.message)
    }

    @Test
    fun givenBadJsonStringThenShouldThrowDeserializationException() {
        val json = "abc"

        val exception = assertFailsWith<AccessCheckoutException> {
            sessionResponseDeserializer.deserialize(json)
        }

        assertEquals("Cannot interpret json: $json", exception.message)
    }

    @Test
    fun givenJsonStringWithMissingObjectThenShouldThrowDeserializationException() {
        val json = "{ }"

        val exception = assertFailsWith<AccessCheckoutException> {
            sessionResponseDeserializer.deserialize(json)
        }

        assertEquals("Missing object: '_links'", exception.message)
    }

    @Test
    fun givenJsonStringWithMissingPropertyThenShouldThrowDeserializationException() {
        val json = "{ \"_links\": { \"verifiedTokens:session\": { } } }"

        val exception = assertFailsWith<AccessCheckoutException> {
            sessionResponseDeserializer.deserialize(json)
        }

        assertEquals("Missing property: 'href'", exception.message)
    }

    @Test
    fun givenJsonStringWithInvalidStringTypeThenShouldThrowDeserializationException() {
        val json = "{ \"_links\": { \"verifiedTokens:session\": { \"href\": true } } }"

        val exception = assertFailsWith<AccessCheckoutException> {
            sessionResponseDeserializer.deserialize(json)
        }

        assertEquals("Invalid property type: 'href', expected 'String'", exception.message)
    }

    @Test
    fun givenJsonStringWithInvalidBooleanTypeThenShouldThrowDeserializationException() {
        val badJson =
            """{
                  "_links": {
                    "verifiedTokens:session": {
                      "href": "https://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"
                    },
                    "curies": [
                      {
                        "href": "https://access.worldpay.com/rels/verifiedTokens{rel}.json",
                        "name": "verifiedTokens",
                        "templated": "true"
                      }
                    ]
                  }
                }"""

        val exception = assertFailsWith<AccessCheckoutException> {
            sessionResponseDeserializer.deserialize(badJson)
        }

        assertEquals("Invalid property type: 'templated', expected 'Boolean'", exception.message)
    }

    @Test
    fun givenValidSessionResponseStringThenShouldSuccessfullyDeserializeToSessionResponse() {
        val jsonResponse =
            """{
                  "_links": {
                    "verifiedTokens:session": {
                      "href": "https://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"
                    },
                    "curies": [
                      {
                        "href": "https://access.worldpay.com/rels/verifiedTokens{rel}.json",
                        "name": "verifiedTokens",
                        "templated": true
                      }
                    ]
                  }
                }"""

        val deserializedResponse = sessionResponseDeserializer.deserialize(jsonResponse)

        val expectedCuries =
            arrayOf(Curies("https://access.worldpay.com/rels/verifiedTokens{rel}.json", "verifiedTokens", true))
        val expectedLinks = Links(
            Endpoints("https://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"),
            expectedCuries
        )
        val expectedResponse =
            SessionResponse(
                expectedLinks
            )

        assertEquals(expectedResponse, deserializedResponse)
    }
}

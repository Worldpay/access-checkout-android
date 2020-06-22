package com.worldpay.access.checkout.session.api.serialization

import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.session.api.response.SessionResponse.Links
import com.worldpay.access.checkout.session.api.response.SessionResponse.Links.Endpoints
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class CVCSessionResponseDeserializerTest {

    private val sessionResponseDeserializer =
        CVCSessionResponseDeserializer()

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun givenEmptyResponseThenShouldThrowDeserializationException() {
        expectedException.expect(AccessCheckoutException::class.java)
        expectedException.expectMessage("Cannot deserialize empty string")

        sessionResponseDeserializer.deserialize("")
    }

    @Test
    fun givenBadJsonStringThenShouldThrowDeserializationException() {
        val json = "abc"
        expectedException.expect(AccessCheckoutException::class.java)
        expectedException.expectMessage("Cannot interpret json: $json")

        sessionResponseDeserializer.deserialize(json)
    }

    @Test
    fun givenJsonStringWithMissingObjectThenShouldThrowDeserializationException() {
        val json = "{ }"
        expectedException.expect(AccessCheckoutException::class.java)
        expectedException.expectMessage("Missing object: '_links'")

        sessionResponseDeserializer.deserialize(json)
    }

    @Test
    fun givenJsonStringWithMissingPropertyThenShouldThrowDeserializationException() {
        val json = "{ \"_links\": { \"sessions:session\": { } } }"
        expectedException.expect(AccessCheckoutException::class.java)
        expectedException.expectMessage("Missing property: 'href'")

        sessionResponseDeserializer.deserialize(json)
    }

    @Test
    fun givenJsonStringWithInvalidStringTypeThenShouldThrowDeserializationException() {
        val json = "{ \"_links\": { \"sessions:session\": { \"href\": true } } }"
        expectedException.expect(AccessCheckoutException::class.java)
        expectedException.expectMessage("Invalid property type: 'href', expected 'String'")

        sessionResponseDeserializer.deserialize(json)
    }

    @Test
    fun givenJsonStringWithInvalidBooleanTypeThenShouldThrowDeserializationException() {
        val badJson =
            """{
                  "_links": {
                    "sessions:session": {
                      "href": "http://access.worldpay.com/sessions/<encrypted-data>"
                    },
                    "curies": [
                      {
                        "href": "https://access.worldpay.com/rels/sessions{rel}.json",
                        "name": "sessions",
                        "templated": "true"
                      }
                    ]
                  }
                }"""
        expectedException.expect(AccessCheckoutException::class.java)
        expectedException.expectMessage("Invalid property type: 'templated'")

        sessionResponseDeserializer.deserialize(badJson)
    }

    @Test
    fun givenValidSessionResponseStringThenShouldSuccessfullyDeserializeToSessionResponse() {
        val jsonResponse =
            """{
                  "_links": {
                    "sessions:session": {
                      "href": "http://access.worldpay.com/sessions/<encrypted-data>"
                    },
                    "curies": [
                      {
                        "href": "https://access.worldpay.com/rels/sessions{rel}.json",
                        "name": "sessions",
                        "templated": true
                      }
                    ]
                  }
                }"""

        val deserializedResponse = sessionResponseDeserializer.deserialize(jsonResponse)


        val expectedCuries =
            arrayOf(
                Links.Curies(
                    "https://access.worldpay.com/rels/sessions{rel}.json",
                    "sessions",
                    true
                )
            )
        val expectedLinks = Links(
            Endpoints("http://access.worldpay.com/sessions/<encrypted-data>"),
            expectedCuries
        )
        val expectedResponse =
            SessionResponse(
                expectedLinks
            )

        assertEquals(expectedResponse, deserializedResponse)
    }
}

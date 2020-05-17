package com.worldpay.access.checkout.session.api.serialization

import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDeserializationException
import com.worldpay.access.checkout.session.api.SessionResponse
import com.worldpay.access.checkout.session.api.SessionResponse.Links
import com.worldpay.access.checkout.session.api.SessionResponse.Links.Endpoints
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CVVSessionResponseDeserializerTest {

    private val sessionResponseDeserializer =
        CVVSessionResponseDeserializer()

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun givenEmptyResponseThenShouldThrowDeserializationException() {
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Cannot deserialize empty string")

        sessionResponseDeserializer.deserialize("")
    }

    @Test
    fun givenBadJsonStringThenShouldThrowDeserializationException() {
        val json = "abc"
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Cannot interpret json: $json")

        sessionResponseDeserializer.deserialize(json)
    }

    @Test
    fun givenJsonStringWithMissingObjectThenShouldThrowDeserializationException() {
        val json = "{ }"
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Missing object: '_links'")

        sessionResponseDeserializer.deserialize(json)
    }

    @Test
    fun givenJsonStringWithMissingPropertyThenShouldThrowDeserializationException() {
        val json = "{ \"_links\": { \"sessions:session\": { } } }"
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Missing property: 'href'")

        sessionResponseDeserializer.deserialize(json)
    }

    @Test
    fun givenJsonStringWithInvalidStringTypeThenShouldThrowDeserializationException() {
        val json = "{ \"_links\": { \"sessions:session\": { \"href\": true } } }"
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
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
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
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
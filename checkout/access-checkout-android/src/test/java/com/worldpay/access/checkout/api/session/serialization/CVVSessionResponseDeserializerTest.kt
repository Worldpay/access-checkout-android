package com.worldpay.access.checkout.api.session.serialization

import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDeserializationException
import com.worldpay.access.checkout.api.session.SessionResponse
import com.worldpay.access.checkout.api.session.SessionResponse.Links
import com.worldpay.access.checkout.api.session.SessionResponse.Links.VerifiedTokensSession
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
    fun givenValidSessionResponseStringThenShouldSuccessfullyDeserializeToSessionResponse() {
        val jsonResponse =
            """{
                  "_links": {
                    "sessions:session": {
                      "href": "http://access.worldpay.com/sessions/<encrypted-data>"
                    }
                  }
                }"""

        val deserializedResponse = sessionResponseDeserializer.deserialize(jsonResponse)


        val expectedLinks = Links(
            VerifiedTokensSession("http://access.worldpay.com/sessions/<encrypted-data>"),
            emptyArray()
        )
        val expectedResponse =
            SessionResponse(
                expectedLinks
            )

        assertEquals(expectedResponse, deserializedResponse)
    }
}
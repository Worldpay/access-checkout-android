package com.worldpay.access.checkout.cardbin.serialization

import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinResponseDeserializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.session.api.response.SessionResponse.Links
import com.worldpay.access.checkout.session.api.response.SessionResponse.Links.Curies
import com.worldpay.access.checkout.session.api.response.SessionResponse.Links.Endpoints
import kotlin.test.assertFailsWith
import org.junit.Assert.assertEquals
import org.junit.Test

class CardBinResponseDeserializerTest {

    private val cardBinResponseDeserializer = CardBinResponseDeserializer()

    @Test
    fun `should throw deserialization exception for an empty response`() {
        val exception = assertFailsWith<AccessCheckoutException> {
            cardBinResponseDeserializer.deserialize("")
        }

        assertEquals("Cannot deserialize empty string", exception.message)
    }

    @Test
    fun `should throw deserialization exception for a bad JSON string`() {
        val json = "abc"

        val exception = assertFailsWith<AccessCheckoutException> {
            cardBinResponseDeserializer.deserialize(json)
        }

        assertEquals("Cannot interpret json: $json", exception.message)
    }

    @Test
    fun `should throw deserialization exception for a JSON string with missing object`() {
        val json = "{ }"

        val exception = assertFailsWith<AccessCheckoutException> {
            cardBinResponseDeserializer.deserialize(json)
        }

        assertEquals("Missing array: 'brand'", exception.message)
    }

    @Test
    fun `should throw deserialization exception for a JSON string with missing property`() {
        val json =
            """
                      {
                        "brand": [
                            "visa"
                        ],
                        "luhnCompliant": true
                    }   
                """

        val exception = assertFailsWith<AccessCheckoutException> {
            cardBinResponseDeserializer.deserialize(json)
        }

        assertEquals("Missing property: 'fundingType'", exception.message)
    }

    @Test
    fun `should throw deserialization exception a for JSON string with invalid string type`() {
        val json =
            """
                      {
                        "brand": [
                            "visa"
                        ],
                        "fundingType": true,
                        "luhnCompliant": true
                    }   
                """

        val exception = assertFailsWith<AccessCheckoutException> {
            cardBinResponseDeserializer.deserialize(json)
        }

        assertEquals("Invalid property type: 'fundingType', expected 'String'", exception.message)
    }

    @Test
    fun `should throw deserialization exception for a JSON string with invalid boolean type`() {
        val badJson =
            """
                      {
                        "brand": [
                            "visa"
                        ],
                        "fundingType": "debit",
                        "luhnCompliant": "true"
                    }   
                """

        val exception = assertFailsWith<AccessCheckoutException> {
            cardBinResponseDeserializer.deserialize(badJson)
        }

        assertEquals("Invalid property type: 'luhnCompliant', expected 'Boolean'", exception.message)
    }

    @Test
    fun `should successfully deserialize a valid card bin response string to card response object`() {
        val brand = listOf("visa")
        val fundingType = "debit"
        val luhnCompliant = true

        val jsonResponse =
            """
                      {
                        "brand": [
                            ${brand[0]}
                        ],
                        "fundingType": $fundingType,
                        "luhnCompliant": $luhnCompliant
                    }   
                """
        val deserializedResponse = cardBinResponseDeserializer.deserialize(jsonResponse)


        val expectedResponse =
            CardBinResponse(
                brand,
                fundingType,
                luhnCompliant
            )

        assertEquals(expectedResponse, deserializedResponse)
    }
}

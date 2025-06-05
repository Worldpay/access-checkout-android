package com.worldpay.access.checkout.api

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern
import com.worldpay.access.checkout.api.MockServer.getBaseUrl
import com.worldpay.access.checkout.api.MockServer.getStringBaseUrl
import com.worldpay.access.checkout.api.MockServer.startWiremock
import com.worldpay.access.checkout.api.MockServer.stopWiremock
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
import com.worldpay.access.checkout.cardbin.api.client.WP_API_VERSION
import com.worldpay.access.checkout.cardbin.api.client.WP_API_VERSION_VALUE
import com.worldpay.access.checkout.cardbin.api.client.WP_CALLER_ID
import com.worldpay.access.checkout.cardbin.api.client.WP_CALLER_ID_VALUE
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequest
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinRequestSerializer
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinResponseDeserializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.fail

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CardBinClientIntegrationTest {
    private val cardBinEndpoint = "public/card/bindetails"
    private val cardNumber = "444433332222"
    private val checkoutId = "some-checkout-id"

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val applicationContext: Context = getInstrumentation().context.applicationContext
    private val cardBinClient = CardBinClient(getStringBaseUrl(), HttpsClient(),
        CardBinResponseDeserializer(), CardBinRequestSerializer()
    )

    @Before
    fun setup() {
        startWiremock(applicationContext, 8443)
    }

    @After
    fun tearDown() {
        stopWiremock()
    }

    @Test
    fun givenValid12DigitPan_shouldReturnSuccessfulResponse() = runBlocking {
        val request = """{
                "cardNumber": "$cardNumber",
                "checkoutId": "$checkoutId"
                }
                """
        val response =
            """
                      {
                        "brand": [
                            "visa"
                        ],
                        "fundingType": "debit",
                        "luhnCompliant": true
                    }   
                """


        stubFor(
            postRequest(request)
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)
                )
        )

        val cardBinReq =
            CardBinRequest(
                cardNumber = cardNumber,
                checkoutId = checkoutId
            )

        val cardBinResponse = cardBinClient.getCardBinResponse(cardBinReq)
        val expectedResponse = CardBinResponseDeserializer().deserialize(response)

        assertEquals(expectedResponse, cardBinResponse)
    }

    @Test
    fun shouldThrowExceptionWhenFailingToGetCardBrand() = runBlocking {
        val request =
            """{
            "cardNumber": "$cardNumber",
            "checkoutId": "$checkoutId"
            }
            """

        stubFor(
            postRequest(request)
                .willReturn(
                    aResponse()
                        .withStatus(500)
                        .withStatusMessage("Server Error")
                )
        )
        val cardBinReq =
            CardBinRequest(
                cardNumber = cardNumber,
                checkoutId = checkoutId
            )

        val result = runCatching {
            cardBinClient.getCardBinResponse(cardBinReq)
        }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AccessCheckoutException)
        assertEquals("Error message was: Server Error", result.exceptionOrNull()?.message)
    }

    private fun postRequest(request: String): MappingBuilder {
        return post(urlEqualTo("/$cardBinEndpoint"))
            .withHeader(WP_API_VERSION, equalTo(WP_API_VERSION_VALUE))
            .withHeader(WP_CALLER_ID, equalTo(WP_CALLER_ID_VALUE))
            .withRequestBody(EqualToJsonPattern(request, true, true))
    }
}

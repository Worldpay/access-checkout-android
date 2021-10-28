package com.worldpay.access.checkout.api

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern
import com.worldpay.access.checkout.api.MockServer.getBaseUrl
import com.worldpay.access.checkout.api.MockServer.startWiremock
import com.worldpay.access.checkout.api.MockServer.stopWiremock
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.api.exception.ValidationRule
import com.worldpay.access.checkout.client.session.AccessCheckoutClientBuilder
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.session.api.client.VERIFIED_TOKENS_MEDIA_TYPE
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import org.awaitility.Awaitility.await
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

class AccessCheckoutClientIntegrationTest {

    private val verifiedTokensEndpoint = "verifiedTokens/sessions"
    private val cvc = "123"
    private val cardNumber = "1111222233334444"
    private val expiryDate = "1220"
    private val merchantId = "identity"

    private val applicationContext: Context = getInstrumentation().context.applicationContext
    private val lifecycleOwner: LifecycleOwner = mock(LifecycleOwner::class.java)

    private var lifecycleRegistry = LifecycleRegistry(lifecycleOwner)

    @Before
    fun setup() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycleRegistry)

        getInstrumentation().runOnMainSync {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }

        startWiremock(applicationContext, 8443)
    }

    @After
    fun tearDown() {
        getInstrumentation().runOnMainSync {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
        stopWiremock()
    }

    @Test
    fun givenValidRequest_shouldReturnSuccessfulResponse() {
        val cardDetails = getCardDetails()
        val request = getExpectedRequest(cardDetails)

        val expectedSessionReference = "https://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"

        val response = (
            """{
                  "_links": {
                    "verifiedTokens:session": {
                      "href": "$expectedSessionReference"
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
            )

        stubFor(
            postRequest(request)
                .willReturn(
                    aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withHeader(
                            "Location",
                            "https://access.worldpay.com/$verifiedTokensEndpoint/sessions/<encrypted-data>"
                        )
                        .withBody(response)
                )
        )

        var assertResponse = false
        val responseListener = object : SessionResponseListener {
            override fun onSuccess(sessionResponseMap: Map<SessionType, String>) {
                assertEquals(mapOf(CARD to expectedSessionReference), sessionResponseMap)
                assertResponse = true
            }

            override fun onError(error: AccessCheckoutException) {}
        }

        getInstrumentation().runOnMainSync {
            val accessCheckoutClient = AccessCheckoutClientBuilder()
                .baseUrl(getBaseUrl().toString())
                .merchantId(merchantId)
                .sessionResponseListener(responseListener)
                .context(applicationContext)
                .lifecycleOwner(lifecycleOwner)
                .build()

            accessCheckoutClient.generateSessions(cardDetails, listOf(CARD))
        }

        await().atMost(5, TimeUnit.SECONDS).until { assertResponse }
    }

    @Test
    fun shouldReturnError_whenHttpResponseHasValidationErrors() {
        val cardDetails = getCardDetails()
        val request = getExpectedRequest(cardDetails)

        val response = """{
                   "errorName": "bodyDoesNotMatchSchema",
                   "message": "The json body provided does not match the expected schema",
                   "validationErrors": [{
                        "errorName": "stringIsTooShort",
                        "message": "String is too short",
                        "jsonPath": "\$.cvv"
                    }]}"""

        stubFor(
            postRequest(request).willReturn(
                aResponse()
                    .withStatus(400)
                    .withBody(response)
            )
        )

        var assertionsRan = false
        val responseListener = object : SessionResponseListener {
            override fun onSuccess(sessionResponseMap: Map<SessionType, String>) {}

            override fun onError(error: AccessCheckoutException) {
                assertEquals("bodyDoesNotMatchSchema : The json body provided does not match the expected schema", error.message)
                assertionsRan = true
            }
        }

        getInstrumentation().runOnMainSync {
            val accessCheckoutClient = AccessCheckoutClientBuilder()
                .baseUrl(getBaseUrl().toString())
                .merchantId(merchantId)
                .sessionResponseListener(responseListener)
                .context(applicationContext)
                .lifecycleOwner(lifecycleOwner)
                .build()

            accessCheckoutClient.generateSessions(cardDetails, listOf(CARD))
        }

        await().atMost(5, TimeUnit.SECONDS).until { assertionsRan }
    }

    @Test
    fun shouldReturnError_whenHttpResponseHasNoValidationErrors() {
        val cardDetails = getCardDetails()
        val request = getExpectedRequest(cardDetails)

        val response = """{
                "errorName": "bodyIsNotJson",
                "message": "The body within the request is not valid json"
            }"""

        stubFor(
            postRequest(request).willReturn(
                aResponse()
                    .withStatus(400)
                    .withBody(response)
            )
        )

        var assertionsRan = false
        val errorListener = object : SessionResponseListener {
            override fun onSuccess(sessionResponseMap: Map<SessionType, String>) {}

            override fun onError(error: AccessCheckoutException) {
                assertEquals("bodyIsNotJson : The body within the request is not valid json", error.message)
                assertionsRan = true
            }
        }

        getInstrumentation().runOnMainSync {
            val accessCheckoutClient = AccessCheckoutClientBuilder()
                .baseUrl(getBaseUrl().toString())
                .merchantId(merchantId)
                .sessionResponseListener(errorListener)
                .context(applicationContext)
                .lifecycleOwner(lifecycleOwner)
                .build()

            accessCheckoutClient.generateSessions(cardDetails, listOf(CARD))
        }

        await().atMost(5, TimeUnit.SECONDS).until { assertionsRan }
    }

    @Test
    fun shouldReturnException_evenWhenResponseValidationRuleIsUnknown() {
        val cardDetails = getCardDetails()
        val request = getExpectedRequest(cardDetails)

        val response = """{
                   "errorName": "bodyDoesNotMatchSchema",
                   "message": "The json body provided does not match the expected schema",
                   "validationErrors": [{
                        "errorName": "some-unknown-error",
                        "message": "String is too short",
                        "jsonPath": "\$.cvv"
                    }]}"""

        stubFor(
            postRequest(request).willReturn(
                aResponse()
                    .withStatus(400)
                    .withBody(response)
            )
        )

        var assertionsRan = false
        val errorListener = object : SessionResponseListener {
            override fun onSuccess(sessionResponseMap: Map<SessionType, String>) {}

            override fun onError(error: AccessCheckoutException) {
                val expectedException = AccessCheckoutException(
                    message = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema",
                    validationRules = listOf(ValidationRule("some-unknown-error", "String is too short", "$.cvv"))
                )
                assertEquals(expectedException, error)
                assertionsRan = true
            }
        }

        getInstrumentation().runOnMainSync {
            val accessCheckoutClient = AccessCheckoutClientBuilder()
                .baseUrl(getBaseUrl().toString())
                .merchantId(merchantId)
                .sessionResponseListener(errorListener)
                .context(applicationContext)
                .lifecycleOwner(lifecycleOwner)
                .build()

            accessCheckoutClient.generateSessions(cardDetails, listOf(CARD))
        }

        await().atMost(5, TimeUnit.SECONDS).until { assertionsRan }
    }

    @Test
    fun shouldReturnError_whenHttpResponseIsServerError() {
        val cardDetails = getCardDetails()
        val request = getExpectedRequest(cardDetails)

        stubFor(
            postRequest(request)
                .willReturn(
                    aResponse()
                        .withStatus(500)
                        .withStatusMessage("Internal Server Error")
                )
        )

        var assertionsRan = false
        val errorListener = object : SessionResponseListener {
            override fun onSuccess(sessionResponseMap: Map<SessionType, String>) {}

            override fun onError(error: AccessCheckoutException) {
                assertEquals("Error message was: Internal Server Error", error.message)
                assertionsRan = true
            }
        }

        getInstrumentation().runOnMainSync {
            val accessCheckoutClient = AccessCheckoutClientBuilder()
                .baseUrl(getBaseUrl().toString())
                .merchantId(merchantId)
                .sessionResponseListener(errorListener)
                .context(applicationContext)
                .lifecycleOwner(lifecycleOwner)
                .build()

            accessCheckoutClient.generateSessions(cardDetails, listOf(CARD))
        }

        await().atMost(5, TimeUnit.SECONDS).until { assertionsRan }
    }

    private fun postRequest(request: String): MappingBuilder {
        return post(urlEqualTo("/$verifiedTokensEndpoint"))
            .withHeader("Accept", equalTo(VERIFIED_TOKENS_MEDIA_TYPE))
            .withHeader("Content-Type", equalTo(VERIFIED_TOKENS_MEDIA_TYPE))
            .withRequestBody(EqualToJsonPattern(request, true, true))
    }

    private fun getCardDetails(): CardDetails {
        return CardDetails.Builder()
            .pan(cardNumber)
            .expiryDate(expiryDate)
            .cvc(cvc)
            .build()
    }

    private fun getExpectedRequest(cardDetails: CardDetails): String {
        return """{
                "cardNumber": "${cardDetails.pan}",
                "cardExpiryDate": {
                    "month": ${cardDetails.expiryDate?.month},
                    "year": ${cardDetails.expiryDate?.year}
                },
                "cvc": "${cardDetails.cvc}",
                "identity": "$merchantId"
            }"""
    }
}

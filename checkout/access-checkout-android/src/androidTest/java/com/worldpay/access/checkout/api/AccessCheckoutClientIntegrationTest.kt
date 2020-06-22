package com.worldpay.access.checkout.api

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutClientError
import com.worldpay.access.checkout.api.AccessCheckoutException.Error
import com.worldpay.access.checkout.api.ApiDiscoveryStubs.stubServiceDiscoveryResponses
import com.worldpay.access.checkout.client.session.AccessCheckoutClientBuilder
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.client.session.model.SessionType.VERIFIED_TOKEN_SESSION
import org.awaitility.Awaitility.await
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class AccessCheckoutClientIntegrationTest {

    private val verifiedTokensEndpoint = "verifiedTokens/sessions"
    private val cvc = "123"
    private val cardNumber = "1111222233334444"
    private val expiryDate = "1220"
    private val merchantId = "identity"

    private val verifiedTokenMediaType = "application/vnd.worldpay.verified-tokens-v1.hal+json"

    private val applicationContext: Context = getInstrumentation().context.applicationContext
    private val lifecycleOwner: LifecycleOwner = mock(LifecycleOwner::class.java)

    private var lifecycleRegistry: LifecycleRegistry

    @get:Rule
    val wireMockRule = WireMockRule(
        WireMockConfiguration
            .options()
            .port(8090)
            .notifier(ConsoleNotifier(true))
            .extensions(ResponseTemplateTransformer(false))
    )

    init {
        lifecycleRegistry = LifecycleRegistry(lifecycleOwner)
        given(lifecycleOwner.lifecycle).willReturn(lifecycleRegistry)
    }

    @Before
    fun setup() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        stubServiceDiscoveryResponses()
    }

    @After
    fun tearDown() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        wireMockRule.resetAll()
    }

    @Test
    fun givenValidRequest_shouldReturnSuccessfulResponse() {
        val cardDetails = getCardDetails()
        val request = getExpectedRequest(cardDetails)

        val expectedSessionReference =
            """http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"""

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
                            "http://access.worldpay.com/$verifiedTokensEndpoint/sessions/<encrypted-data>"
                        )
                        .withBody(response)
                )
        )

        var assertResponse = false
        val responseListener = object : SessionResponseListener {
            override fun onSuccess(sessionResponseMap: Map<SessionType, String>) {
                assertEquals(mapOf(VERIFIED_TOKEN_SESSION to expectedSessionReference), sessionResponseMap)
                assertResponse = true
            }

            override fun onError(error: AccessCheckoutException) {}
        }

        val accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(merchantId)
            .sessionResponseListener(responseListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

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


        stubFor(postRequest(request).willReturn(
            aResponse()
                .withStatus(400)
                .withBody(response)
        ))

        var assertionsRan = false
        val responseListener = object : SessionResponseListener {
            override fun onSuccess(sessionResponseMap: Map<SessionType, String>) {}

            override fun onError(error: AccessCheckoutException) {
                assertEquals("The json body provided does not match the expected schema", error.message)

                error as AccessCheckoutClientError
                assertEquals(Error.BODY_DOES_NOT_MATCH_SCHEMA, error.error)
                assertionsRan = true
            }
        }

        val accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(merchantId)
            .sessionResponseListener(responseListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

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

        stubFor(postRequest(request).willReturn(
            aResponse()
                .withStatus(400)
                .withBody(response)
        ))

        var assertionsRan = false
        val errorListener = object : SessionResponseListener {
            override fun onSuccess(sessionResponseMap: Map<SessionType, String>) {}

            override fun onError(error: AccessCheckoutException) {
                assertEquals("The body within the request is not valid json", error.message)

                error as AccessCheckoutClientError
                assertEquals(Error.BODY_IS_NOT_JSON, error.error)
                assertionsRan = true
            }
        }

        val accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(merchantId)
            .sessionResponseListener(errorListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

        await().atMost(5, TimeUnit.SECONDS).until { assertionsRan }
    }

    @Test
    fun shouldReturnUnknownError_whenResponseValidationRuleIsUnknown() {
        val cardDetails = getCardDetails()
        val request = getExpectedRequest(cardDetails)

        val response = """{
                   "errorName": "bodyDoesNotMatchSchema",
                   "message": "The json body provided does not match the expected schema",
                   "validationErrors": [{
                        "errorName": "some-unkown-error",
                        "message": "String is too short",
                        "jsonPath": "\$.cvv"
                    }]}"""

        stubFor(postRequest(request).willReturn(
            aResponse()
                .withStatus(400)
                .withBody(response)
        ))

        var assertionsRan = false
        val errorListener = object : SessionResponseListener {
            override fun onSuccess(sessionResponseMap: Map<SessionType, String>) {}

            override fun onError(error: AccessCheckoutException) {
                assertEquals("unknown error", error.message)

                error as AccessCheckoutClientError
                assertEquals(Error.UNKNOWN_ERROR, error.error)
                assertionsRan = true
            }
        }

        val accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(merchantId)
            .sessionResponseListener(errorListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

        await().atMost(5, TimeUnit.SECONDS).until { assertionsRan }
    }

    @Test
    fun shouldReturnError_whenHttpResponseIsServerError() {
        val cardDetails = getCardDetails()
        val request = getExpectedRequest(cardDetails)

        stubFor(postRequest(request)
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
                assertFalse(error is AccessCheckoutClientError)
                assertionsRan = true
            }
        }

        val accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(merchantId)
            .sessionResponseListener(errorListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

        await().atMost(5, TimeUnit.SECONDS).until { assertionsRan }
    }

    private fun postRequest(request: String): MappingBuilder {
        return post(urlEqualTo("/$verifiedTokensEndpoint"))
            .withHeader("Accept", equalTo(verifiedTokenMediaType))
            .withHeader("Content-Type", equalTo(verifiedTokenMediaType))
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

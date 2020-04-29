package com.worldpay.access.checkout.api

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern
import com.worldpay.access.checkout.api.AccessCheckoutException.*
import com.worldpay.access.checkout.api.AccessCheckoutException.Error.BODY_DOES_NOT_MATCH_SCHEMA
import com.worldpay.access.checkout.api.ApiDiscoveryStubs.stubServiceDiscoveryResponses
import com.worldpay.access.checkout.api.session.CardSessionRequest
import com.worldpay.access.checkout.client.AccessCheckoutClient
import com.worldpay.access.checkout.client.AccessCheckoutClientBuilder
import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.SessionType
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
import com.worldpay.access.checkout.views.SessionResponseListener
import org.awaitility.Awaitility.await
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue
import kotlin.test.fail

@RunWith(AndroidJUnit4::class)
class CardSessionRequestIntegrationTest {

    private val verifiedTokensEndpoint = "verifiedTokens/sessions"
    private val cvv = "123"
    private val cardNumber = "1111222233334444"
    private val month = 12
    private val year = 2020
    private val merchantId = "identity"
    private val applicationContext = getInstrumentation().context.applicationContext
    private val lifecycleOwner = mock(LifecycleOwner::class.java)
    private lateinit var cardDetails: CardDetails

    private val sessionRequest =
        CardSessionRequest(
            cardNumber,
            CardSessionRequest.CardExpiryDate(
                month,
                year
            ),
            cvv,
            merchantId
        )

    @get:Rule
    var wireMockRule = WireMockRule(
        WireMockConfiguration
            .options()
            .port(8090)
            .notifier(ConsoleNotifier(true))
            .extensions(ResponseTemplateTransformer(false))
    )

    private lateinit var accessCheckoutClient: AccessCheckoutClient

    private lateinit var lifecycleRegistry: LifecycleRegistry

    @Before
    fun setup() {
        lifecycleRegistry = LifecycleRegistry(lifecycleOwner)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        given(lifecycleOwner.lifecycle).willReturn(lifecycleRegistry)
        stubServiceDiscoveryResponses()
        
        cardDetails = CardDetails.Builder()
            .pan(cardNumber)
            .expiryDate(month, year)
            .cvv(cvv)
            .build()
    }

    @After
    fun tearDown() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    @Test
    fun givenValidRequest_shouldReturnSuccessfulResponse() {
        val request = """
                {
                    "cardNumber": "$cardNumber",
                    "cardExpiryDate": {
                        "month": $month,
                        "year": $year
                    },
                    "cvc": "$cvv",
                    "identity": "${sessionRequest.identity}"
                }"""

        val expectedSessionReference = """http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"""

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
            WireMock.post(WireMock.urlEqualTo("/$verifiedTokensEndpoint"))
                .withHeader("Accept", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(EqualToJsonPattern(request, true, true))
                .willReturn(
                    WireMock.aResponse()
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
            override fun onRequestFinished(sessionResponseMap: Map<SessionType, String>?, error: AccessCheckoutException?) {
                val expectedResponse = mapOf(VERIFIED_TOKEN_SESSION to expectedSessionReference)
                assertResponse = sessionResponseMap!! == expectedResponse
                assertTrue("Actual response is $sessionResponseMap") { assertResponse }
            }

            override fun onRequestStarted() {
            }
        }

        accessCheckoutClient = AccessCheckoutClientBuilder()
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
    fun givenServerError_shouldReturnUnsuccessfulResponse() {
        val request = """
                {
                    "cardNumber": "$cardNumber",
                    "cardExpiryDate": {
                        "month": $month,
                        "year": $year
                    },
                    "cvc": "$cvv",
                    "identity": "${sessionRequest.identity}"
                }"""

        val response = """{
                   "errorName": "bodyDoesNotMatchSchema",
                   "message": "The json body provided does not match the expected schema",
                   "validationErrors": [{
                        "errorName": "InvalidIdentity",
                        "message": "Identity is invalid",
                        "jsonPath": "\$.identity"
                    }]}"""


        stubFor(
            WireMock.post(WireMock.urlEqualTo("/$verifiedTokensEndpoint"))
                .withHeader("Accept", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(EqualToJsonPattern(request, true, true))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(400)
                        .withBody(response)
                )
        )

        var assertResponse = false

        val responseListener = object : SessionResponseListener {
            override fun onRequestStarted() {
            }

            override fun onRequestFinished(
                sessionResponseMap: Map<SessionType, String>?,
                error: AccessCheckoutException?
            ) {
                assertResponse = sessionResponseMap == null && error != null
                assertTrue("Actual response is $sessionResponseMap") { assertResponse }
            }
        }

        accessCheckoutClient = AccessCheckoutClientBuilder()
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
    fun givenLuhnInvalidNumber_thenMockServerWillReturnLuhnError() {

        val luhnInvalidCard = "4444444444444444"

        val request = """
                {
                    "cardNumber": "$luhnInvalidCard",
                    "cardExpiryDate": {
                        "month": $month,
                        "year": $year
                    },
                    "cvc": "$cvv",
                    "identity": "$merchantId"
                }"""

        val jsonResponseWithLuhnFail = """{
                                "errorName": "bodyDoesNotMatchSchema",
                                "message": "The json body provided does not match the expected schema",
                                "validationErrors": [
                                    {
                                        "errorName": "panFailedLuhnCheck",
                                        "message": "The identified field contains a PAN that has failed the Luhn check.",
                                        "jsonPath": "${'$'}.cardNumber"
                                    }
                                ]
                            }"""

        stubFor(
            WireMock.post(WireMock.urlEqualTo("/$verifiedTokensEndpoint"))
                .withHeader("Accept", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(EqualToJsonPattern(request, true, true))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(400)
                        .withBody(jsonResponseWithLuhnFail)
                )
        )


        var assertExpectedErrorRaised = false

        val errorListener = object : SessionResponseListener {
            override fun onRequestStarted() {
            }

            override fun onRequestFinished(
                sessionResponseMap: Map<SessionType, String>?,
                error: AccessCheckoutException?
            ) {
                if (error == null)
                    fail("Expected error not detected")

                error as AccessCheckoutClientError
                assertExpectedErrorRaised = sessionResponseMap == null &&
                        matchesExpectedType(error, BODY_DOES_NOT_MATCH_SCHEMA) &&
                        hasDetectedBrokenRule(error, ValidationRuleName.PAN_FAILED_LUHN_CHECK)


                assertTrue("Detected error is ${error.error}") { assertExpectedErrorRaised }
            }
        }

        accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(merchantId)
            .sessionResponseListener(errorListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        val cardDetails = CardDetails.Builder()
            .pan(luhnInvalidCard)
            .expiryDate(month, year)
            .cvv(cvv)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

        await().atMost(5, TimeUnit.SECONDS).until { assertExpectedErrorRaised }
    }

    @Test
    fun givenInvalidField_thenMockServerWillReturnInvalidFieldError() {

        val invalidIdentity = "aaaaaaa"
        val requestWithInvalidField = """
                {
                    "cardNumber": "$cardNumber",
                    "cardExpiryDate": {
                        "month": $month,
                        "year": $year
                    },
                    "cvc": "$cvv",
                    "identity": "$invalidIdentity"
                }"""

        val responseWithInvalidField =
            """{
                    "errorName": "bodyDoesNotMatchSchema",
                    "message": "The json body provided does not match the expected schema",
                    "validationErrors": [
                        {
                            "errorName": "fieldHasInvalidValue",
                            "message": "Identity is invalid",
                            "jsonPath": "$.identity"
                        }
                    ]
                }"""


        stubFor(
            WireMock.post(WireMock.urlEqualTo("/$verifiedTokensEndpoint"))
                .withHeader("Accept", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(EqualToJsonPattern(requestWithInvalidField, true, true))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(BODY_DOES_NOT_MATCH_SCHEMA.errorCode)
                        .withHeader("Content-Type", "application/json")
                        .withHeader(
                            "Location",
                            "http://access.worldpay.com/$verifiedTokensEndpoint/sessions/<encrypted-data>"
                        )
                        .withBody(responseWithInvalidField)
                )
        )

        var assertResponse = false

        val responseListener = object : SessionResponseListener {
            override fun onRequestFinished(sessionResponseMap: Map<SessionType, String>?, error: AccessCheckoutException?) {

                if (error == null)
                    fail("Expected error not detected")

                val exceptionValidated = assertActualExceptionIsExpected(
                    error,
                    AccessCheckoutClientError(
                        BODY_DOES_NOT_MATCH_SCHEMA,
                        "The json body provided does not match the expected schema",
                        listOf(ValidationRule(ValidationRuleName.FIELD_HAS_INVALID_VALUE, "", ""))
                    )
                )
                assertResponse = sessionResponseMap == null && exceptionValidated

                assertTrue("Actual response is $sessionResponseMap") { assertResponse }
            }

            override fun onRequestStarted() {
            }
        }

        accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(invalidIdentity)
            .sessionResponseListener(responseListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

        await().atMost(5, TimeUnit.SECONDS).until { assertResponse }
    }

    @Test
    fun givenMissingField_thenMockServerWillReturnMissingFieldError() {

        val request = """
                {
                    "cardExpiryDate": {
                        "month": $month,
                        "year": $year
                    },
                    "cvc": "$cvv",
                    "identity": "$merchantId"
                }"""

        val jsonResponseWithMissingField = """{
                    "errorName": "bodyDoesNotMatchSchema",
                    "message": "The json body provided does not match the expected schema",
                    "validationErrors": [
                        {
                            "errorName": "fieldIsMissing",
                            "message": "Card number is missing. This field is mandatory",
                            "jsonPath": "${'$'}.cardNumber"
                        }
                    ]
                }"""

        stubFor(
            WireMock.post(WireMock.urlEqualTo("/$verifiedTokensEndpoint"))
                .withHeader("Accept", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(EqualToJsonPattern(request, true, true))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(400)
                        .withBody(jsonResponseWithMissingField)
                )
        )


        var assertExpectedErrorRaised = false

        val errorListener = object : SessionResponseListener {
            override fun onRequestStarted() {
            }

            override fun onRequestFinished(
                sessionResponseMap: Map<SessionType, String>?,
                error: AccessCheckoutException?
            ) {
                if (error == null)
                    fail("Expected error not detected")

                error as AccessCheckoutClientError
                assertExpectedErrorRaised = sessionResponseMap == null &&
                        matchesExpectedType(error, BODY_DOES_NOT_MATCH_SCHEMA) &&
                        hasDetectedBrokenRule(error, ValidationRuleName.FIELD_IS_MISSING)


                assertTrue("Detected error is ${error.error}") { assertExpectedErrorRaised }
            }
        }

        accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(merchantId)
            .sessionResponseListener(errorListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        val cardDetails = CardDetails.Builder()
            .pan("")
            .expiryDate(month, year)
            .cvv(cvv)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

        await().atMost(5, TimeUnit.SECONDS).until { assertExpectedErrorRaised }


    }

    @Test
    fun givenStringFieldTooShort_thenMockServerWillReturnStringTooShortError() {

        val cardTooShort = "1111"

        val request = """
                {
                    "cardNumber": "$cardTooShort",
                    "cardExpiryDate": {
                        "month": $month,
                        "year": $year
                    },
                    "cvc": "$cvv",
                    "identity": "$merchantId"
                }"""

        val jsonResponseWithStringShortError = """{
                    "errorName": "bodyDoesNotMatchSchema",
                    "message": "The json body provided does not match the expected schema",
                    "validationErrors": [
                        {
                            "errorName": "stringIsTooShort",
                            "message": "Card number is too short - must be between 10 & 19 digits",
                            "jsonPath": "${'$'}.cardNumber"
                        }
                    ]
                }"""

        stubFor(
            WireMock.post(WireMock.urlEqualTo("/$verifiedTokensEndpoint"))
                .withHeader("Accept", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(EqualToJsonPattern(request, true, true))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(400)
                        .withBody(jsonResponseWithStringShortError)
                )
        )


        var assertExpectedErrorRaised = false

        val errorListener = object : SessionResponseListener {
            override fun onRequestStarted() {
            }

            override fun onRequestFinished(
                sessionResponseMap: Map<SessionType, String>?,
                error: AccessCheckoutException?
            ) {
                if (error == null)
                    fail("Expected error not detected")

                error as AccessCheckoutClientError
                assertExpectedErrorRaised = sessionResponseMap == null &&
                        matchesExpectedType(error, BODY_DOES_NOT_MATCH_SCHEMA) &&
                        hasDetectedBrokenRule(error, ValidationRuleName.STRING_IS_TOO_SHORT)


                assertTrue("Detected error is ${error.error} with broken rules: ${error.validationRules.orEmpty().first().errorName}}") { assertExpectedErrorRaised }
            }
        }

        accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(merchantId)
            .sessionResponseListener(errorListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        val cardDetails = CardDetails.Builder()
            .pan(cardTooShort)
            .expiryDate(month, year)
            .cvv(cvv)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

        await().atMost(5, TimeUnit.SECONDS).until { assertExpectedErrorRaised }
    }

    @Test
    fun givenStringFieldTooLong_thenMockServerWillReturnStringTooLongError() {

        val cardTooLong = "33333333333333333391111111111111"

        val request = """
                {
                    "cardNumber": "$cardTooLong",
                    "cardExpiryDate": {
                        "month": $month,
                        "year": $year
                    },
                    "cvc": "$cvv",
                    "identity": "$merchantId"
                }"""

        val jsonResponseWithStringLongError = """{
                                                    "errorName": "bodyDoesNotMatchSchema",
                                                    "message": "The json body provided does not match the expected schema",
                                                    "validationErrors": [
                                                        {
                                                            "errorName": "stringIsTooLong",
                                                            "message": "Card number is too long - must be between 10 & 19 digits",
                                                            "jsonPath": "${'$'}.cardNumber"
                                                        }
                                                    ]
                                                }"""

        stubFor(
            WireMock.post(WireMock.urlEqualTo("/$verifiedTokensEndpoint"))
                .withHeader("Accept", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(EqualToJsonPattern(request, true, true))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(400)
                        .withBody(jsonResponseWithStringLongError)
                )
        )


        var assertExpectedErrorRaised = false

        val errorListener = object : SessionResponseListener {
            override fun onRequestStarted() {
            }

            override fun onRequestFinished(
                sessionResponseMap: Map<SessionType, String>?,
                error: AccessCheckoutException?
            ) {
                if (error == null)
                    fail("Expected error not detected")

                error as AccessCheckoutClientError
                assertExpectedErrorRaised = sessionResponseMap == null &&
                        matchesExpectedType(error, BODY_DOES_NOT_MATCH_SCHEMA) &&
                        hasDetectedBrokenRule(error, ValidationRuleName.STRING_IS_TOO_LONG)


                assertTrue("Detected error is ${error.error} with broken rules: ${error.validationRules.orEmpty().first().errorName}}") { assertExpectedErrorRaised }
            }
        }

        accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(merchantId)
            .sessionResponseListener(errorListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        val cardDetails = CardDetails.Builder()
            .pan(cardTooLong)
            .expiryDate(month, year)
            .cvv(cvv)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

        await().atMost(5, TimeUnit.SECONDS).until { assertExpectedErrorRaised }
    }

    @Test
    fun givenNonIntegerInputForIntegerField_thenMockServerWillReturnFieldMustBeIntegerError() {

        val request = """
                {
                    "cardNumber": "$cardNumber",
                    "cardExpiryDate": {
                        "month": $month,
                        "year": $year
                    },
                    "cvc": "$cvv",
                    "identity": "$merchantId"
                }"""

        val jsonResponseWithBrokenMonth = """{
                    "errorName": "bodyDoesNotMatchSchema",
                    "message": "The json body provided does not match the expected schema",
                    "validationErrors": [
                        {
                            "errorName": "fieldMustBeInteger",
                            "message": "Card expiry month must be an integer",
                            "jsonPath": "${'$'}.cardExpiryDate.month"
                        }
                    ]
                }"""

        stubFor(
            WireMock.post(WireMock.urlEqualTo("/$verifiedTokensEndpoint"))
                .withHeader("Accept", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(EqualToJsonPattern(request, true, true))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(400)
                        .withBody(jsonResponseWithBrokenMonth)
                )
        )


        var assertExpectedErrorRaised = false

        val errorListener = object : SessionResponseListener {
            override fun onRequestStarted() {
            }

            override fun onRequestFinished(
                sessionResponseMap: Map<SessionType, String>?,
                error: AccessCheckoutException?
            ) {
                if (error == null)
                    fail("Expected error not detected")

                error as AccessCheckoutClientError
                assertExpectedErrorRaised = sessionResponseMap == null &&
                        matchesExpectedType(error, BODY_DOES_NOT_MATCH_SCHEMA) &&
                        hasDetectedBrokenRule(error, ValidationRuleName.FIELD_MUST_BE_INTEGER)


                assertTrue("Detected error is ${error.error} with broken rules: ${error.validationRules.orEmpty().first().errorName}}") { assertExpectedErrorRaised }
            }
        }

        accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(merchantId)
            .sessionResponseListener(errorListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

        await().atMost(5, TimeUnit.SECONDS).until { assertExpectedErrorRaised }
    }

    @Test
    fun givenTooShortIntegerInput_thenMockServerWillReturnIntegerTooSmallError() {

        val request = """
                {
                    "cardNumber": "$cardNumber",
                    "cardExpiryDate": {
                        "month": $month,
                        "year": $year
                    },
                    "cvc": "$cvv",
                    "identity": "$merchantId"
                }"""

        val jsonResponseWithBrokenMonth = """{
                    "errorName": "bodyDoesNotMatchSchema",
                    "message": "The json body provided does not match the expected schema",
                    "validationErrors": [
                        {
                            "errorName": "integerIsTooSmall",
                            "message": "Card expiry month is too small - must be between 1 & 12",
                            "jsonPath": "${'$'}.cardExpiryDate.month"
                        }
                    ]
                }"""

        stubFor(
            WireMock.post(WireMock.urlEqualTo("/$verifiedTokensEndpoint"))
                .withHeader("Accept", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(EqualToJsonPattern(request, true, true))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(400)
                        .withBody(jsonResponseWithBrokenMonth)
                )
        )


        var assertExpectedErrorRaised = false

        val errorListener = object : SessionResponseListener {
            override fun onRequestStarted() {
            }

            override fun onRequestFinished(
                sessionResponseMap: Map<SessionType, String>?,
                error: AccessCheckoutException?
            ) {
                if (error == null)
                    fail("Expected error not detected")

                error as AccessCheckoutClientError
                assertExpectedErrorRaised = sessionResponseMap == null &&
                        matchesExpectedType(error, BODY_DOES_NOT_MATCH_SCHEMA) &&
                        hasDetectedBrokenRule(error, ValidationRuleName.INTEGER_IS_TOO_SMALL)


                assertTrue("Detected error is ${error.error} with broken rules: ${error.validationRules.orEmpty().first().errorName}}") { assertExpectedErrorRaised }
            }
        }

        accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(merchantId)
            .sessionResponseListener(errorListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

        await().atMost(5, TimeUnit.SECONDS).until { assertExpectedErrorRaised }
    }

    @Test
    fun givenTooLargeIntegerInput_thenMockServerWillReturnIntegerTooLargeError() {

        val request = """
                {
                    "cardNumber": "$cardNumber",
                    "cardExpiryDate": {
                        "month": $month,
                        "year": $year
                    },
                    "cvc": "$cvv",
                    "identity": "$merchantId"
                }"""

        val jsonResponseWithBrokenMonth = """{
                    "errorName": "bodyDoesNotMatchSchema",
                    "message": "The json body provided does not match the expected schema",
                    "validationErrors": [
                        {
                            "errorName": "integerIsTooLarge",
                            "message": "Card expiry month is too large - must be between 1 & 12",
                            "jsonPath": "${'$'}.cardExpiryDate.month"
                        }
                    ]
                }"""

        stubFor(
            WireMock.post(WireMock.urlEqualTo("/$verifiedTokensEndpoint"))
                .withHeader("Accept", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(EqualToJsonPattern(request, true, true))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(400)
                        .withBody(jsonResponseWithBrokenMonth)
                )
        )


        var assertExpectedErrorRaised = false

        val errorListener = object : SessionResponseListener {
            override fun onRequestStarted() {
            }

            override fun onRequestFinished(
                sessionResponseMap: Map<SessionType, String>?,
                error: AccessCheckoutException?
            ) {
                if (error == null)
                    fail("Expected error not detected")

                error as AccessCheckoutClientError
                assertExpectedErrorRaised = sessionResponseMap == null &&
                        matchesExpectedType(error, BODY_DOES_NOT_MATCH_SCHEMA) &&
                        hasDetectedBrokenRule(error, ValidationRuleName.INTEGER_IS_TOO_LARGE)


                assertTrue("Detected error is ${error.error} with broken rules: ${error.validationRules.orEmpty().first().errorName}}") { assertExpectedErrorRaised }
            }
        }

        accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(merchantId)
            .sessionResponseListener(errorListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

        await().atMost(5, TimeUnit.SECONDS).until { assertExpectedErrorRaised }
    }

    @Test
    fun givenNonNumericInputForNumericField_thenMockServerWillReturnFieldMustBeNumericError() {

        val request = """
                {
                    "cardNumber": "$cardNumber",
                    "cardExpiryDate": {
                        "month": $month,
                        "year": $year
                    },
                    "cvc": "$cvv",
                    "identity": "$merchantId"
                }"""

        val jsonResponseWithBrokenCvc = """{
                    "errorName": "bodyDoesNotMatchSchema",
                    "message": "The json body provided does not match the expected schema",
                    "validationErrors": [
                        {
                            "errorName": "fieldMustBeNumber",
                            "message": "CVC must be numeric",
                            "jsonPath": "${'$'}.cvc"
                        }
                    ]
                }"""

        stubFor(
            WireMock.post(WireMock.urlEqualTo("/$verifiedTokensEndpoint"))
                .withHeader("Accept", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(EqualToJsonPattern(request, true, true))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(400)
                        .withBody(jsonResponseWithBrokenCvc)
                )
        )


        var assertExpectedErrorRaised = false

        val errorListener = object : SessionResponseListener {
            override fun onRequestStarted() {
            }

            override fun onRequestFinished(
                sessionResponseMap: Map<SessionType, String>?,
                error: AccessCheckoutException?
            ) {
                if (error == null)
                    fail("Expected error not detected")

                error as AccessCheckoutClientError
                assertExpectedErrorRaised = sessionResponseMap == null &&
                        matchesExpectedType(error, BODY_DOES_NOT_MATCH_SCHEMA) &&
                        hasDetectedBrokenRule(error, ValidationRuleName.FIELD_MUST_BE_NUMBER)


                assertTrue("Detected error is ${error.error} with broken rules: ${error.validationRules.orEmpty().first().errorName}}") { assertExpectedErrorRaised }
            }
        }

        accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(merchantId)
            .sessionResponseListener(errorListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

        await().atMost(5, TimeUnit.SECONDS).until { assertExpectedErrorRaised }
    }

    @Test
    fun givenNonJsonInput_thenMockServerWillReturnBodyNotJsonError() {

        val request = """
                {
                    "cardNumber": "$cardNumber",
                    "cardExpiryDate": {
                        "month": $month,
                        "year": $year
                    },
                    "cvc": "$cvv",
                    "identity": "$merchantId"
                }"""

        val jsonResponseWithNotJsonError = """{
                                            "errorName": "bodyIsNotJson",
                                            "message": "The body within the request is not valid json"
                                        }"""

        stubFor(
            WireMock.post(WireMock.urlEqualTo("/$verifiedTokensEndpoint"))
                .withHeader("Accept", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(EqualToJsonPattern(request, true, true))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(400)
                        .withBody(jsonResponseWithNotJsonError)
                )
        )


        var assertExpectedErrorRaised = false

        val errorListener = object : SessionResponseListener {
            override fun onRequestStarted() {
            }

            override fun onRequestFinished(
                sessionResponseMap: Map<SessionType, String>?,
                error: AccessCheckoutException?
            ) {
                if (error == null)
                    fail("Expected error not detected")

                error as AccessCheckoutClientError
                assertExpectedErrorRaised = sessionResponseMap == null &&
                        matchesExpectedType(error, Error.BODY_IS_NOT_JSON)


                assertTrue("Detected error is ${error.error}") { assertExpectedErrorRaised }
            }
        }

        accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(merchantId)
            .sessionResponseListener(errorListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

        await().atMost(5, TimeUnit.SECONDS).until { assertExpectedErrorRaised }
    }

    @Test
    fun givenGetMethodRequest_thenMockServerWillReturnMethodNotAllowedError() {

        val request = """
                {
                    "cardNumber": "$cardNumber",
                    "cardExpiryDate": {
                        "month": $month,
                        "year": $year
                    },
                    "cvc": "$cvv",
                    "identity": "$merchantId"
                }"""

        val jsonResponseWithMethodNotAllowed = """{
                                                "errorName": "methodNotAllowed",
                                                "message": "Requested method is not allowed"
                                            }"""

        stubFor(
            WireMock.post(WireMock.urlEqualTo("/$verifiedTokensEndpoint"))
                .withHeader("Accept", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(EqualToJsonPattern(request, true, true))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(400)
                        .withBody(jsonResponseWithMethodNotAllowed)
                )
        )


        var assertExpectedErrorRaised = false

        val errorListener = object : SessionResponseListener {
            override fun onRequestStarted() {
            }

            override fun onRequestFinished(
                sessionResponseMap: Map<SessionType, String>?,
                error: AccessCheckoutException?
            ) {
                if (error == null)
                    fail("Expected error not detected")

                error as AccessCheckoutClientError
                assertExpectedErrorRaised = sessionResponseMap == null &&
                        matchesExpectedType(error, Error.METHOD_NOT_ALLOWED)


                assertTrue("Detected error is ${error.error}") { assertExpectedErrorRaised }
            }
        }

        accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(merchantId)
            .sessionResponseListener(errorListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

        await().atMost(5, TimeUnit.SECONDS).until { assertExpectedErrorRaised }
    }

    @Test
    fun givenInternalServerError_thenAccessCheckoutExceptionIsPropagatedToClient() {

        val request = """
                {
                    "cardNumber": "$cardNumber",
                    "cardExpiryDate": {
                        "month": $month,
                        "year": $year
                    },
                    "cvc": "$cvv",
                    "identity": "$merchantId"
                }"""


        stubFor(
            WireMock.post(WireMock.urlEqualTo("/$verifiedTokensEndpoint"))
                .withHeader("Accept", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(EqualToJsonPattern(request, true, true))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(500)
                        .withStatusMessage("Internal Server Error")
                )
        )


        var assertExpectedErrorRaised = false

        val errorListener = object : SessionResponseListener {
            override fun onRequestStarted() {}
            override fun onRequestFinished(
                sessionResponseMap: Map<SessionType, String>?,
                error: AccessCheckoutException?
            ) {
                if (error == null)
                    fail("Expected error not detected")

                assertExpectedErrorRaised = sessionResponseMap == null
                        && error is AccessCheckoutError
                        && error.message == "Error message was: Internal Server Error"
                assertTrue("Detected error is $error") { assertExpectedErrorRaised }
            }
        }

        accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(wireMockRule.baseUrl())
            .merchantId(merchantId)
            .sessionResponseListener(errorListener)
            .context(applicationContext)
            .lifecycleOwner(lifecycleOwner)
            .build()

        accessCheckoutClient.generateSession(cardDetails, listOf(VERIFIED_TOKEN_SESSION))

        await().atMost(5, TimeUnit.SECONDS).until { assertExpectedErrorRaised }
    }

    private fun <T> assertActualExceptionIsExpected(ex: Exception, expected: T): Boolean where T : Exception =
        expected.message.orEmpty().let {
            ex.message.orEmpty().contains(it, true)
        }

    private fun matchesExpectedType(exception: AccessCheckoutClientError, expectedErrorType: Error) =
        exception.error == expectedErrorType

    private fun hasDetectedBrokenRule(exception: AccessCheckoutClientError, brokenRule: ValidationRuleName): Boolean =
        exception.validationRules.orEmpty().any { it.errorName == brokenRule }

}
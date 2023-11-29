package com.worldpay.access.checkout.api.pact

import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit.PactHttpsProviderRule
import au.com.dius.pact.consumer.junit.PactVerification
import au.com.dius.pact.core.model.PactSpecVersion
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryClient
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.discovery.DiscoveryCache
import com.worldpay.access.checkout.api.pact.PactUtils.Companion.escapeColonsInMatchingRules
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.api.exception.ValidationRule
import com.worldpay.access.checkout.client.testutil.TrustAllSSLSocketFactory
import com.worldpay.access.checkout.session.api.client.ACCEPT_HEADER
import com.worldpay.access.checkout.session.api.client.CONTENT_TYPE_HEADER
import com.worldpay.access.checkout.session.api.client.CardSessionClient
import com.worldpay.access.checkout.session.api.client.CvcSessionClient
import com.worldpay.access.checkout.session.api.client.SESSIONS_MEDIA_TYPE
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.request.CvcSessionRequest
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.session.api.serialization.CardSessionRequestSerializer
import com.worldpay.access.checkout.session.api.serialization.CardSessionResponseDeserializer
import com.worldpay.access.checkout.session.api.serialization.CvcSessionRequestSerializer
import com.worldpay.access.checkout.session.api.serialization.CvcSessionResponseDeserializer
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.test.assertEquals
import kotlin.test.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest as runAsBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.mockito.kotlin.given

@ExperimentalCoroutinesApi
class SessionsPactTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    companion object {
        private const val provider = "sessions"
        private const val consumer = "access-checkout-android-sdk"

        private const val POST = "POST"
        private const val GET = "GET"

        private val SESSIONS_CONTENT_TYPE_HEADER = mapOf(Pair(CONTENT_TYPE_HEADER, SESSIONS_MEDIA_TYPE))
        private val SESSIONS_ACCEPT_HEADER = mapOf(Pair(ACCEPT_HEADER, SESSIONS_MEDIA_TYPE))
    }

    private lateinit var cardSessionClient: CardSessionClient
    private lateinit var cvcSessionClient: CvcSessionClient
    private lateinit var discoveryClient: ApiDiscoveryClient

    @get:Rule
    var mockProvider = PactHttpsProviderRule(provider, "localhost", 8443, true, PactSpecVersion.V3, this)

    private val cvcSessionsPath = "/sessions/payments/cvc"
    private val cardSessionsPath = "/sessions/card"
    private val sessionsRootPath = "/sessions"
    private val discoveryEndpointRegex = "https?://[^/]+/sessions/.+"

    private val cardNumber = "1111222233334444"
    private val invalidLuhn = "444444444444444"
    private val cardStringTooShort = "1111"
    private val cardStringTooLong = "33333333333333333391111111111111"
    private val expiryMonth = 12

    private val integerMonthTooSmall = 0
    private val integerMonthTooLarge = 13
    private val expiryYear = 2030

    private val cvc = "123"
    private val cvcNonNumerical = "aaa"

    private val identity = "identity"
    private val invalidIdentity = "ABC"

    private val sessionReferenceRegex = "https?://[^/]+/sessions/[^/]+"
    private val sessionReferenceExample = "https://access.worldpay.com/sessions/<encrypted-data>"

    private val sessionEndpointRegex = "https?://[^/]+/sessions/.+"
    private val curiesRegex = "https?://[^/]+/rels/sessions/\\{rel\\}.json"

    private val curiesExample = "https://access.worldpay.com/rels/sessions/{rel}.json"

    private val cardSessionsEndpoint = URL("https://localhost:8443$cardSessionsPath")
    private val cardSessionsProdEndpoint = "https://access.worldpay.com/sessions/card"

    private val cvcSessionsEndpoint = URL("https://localhost:8443$cvcSessionsPath")
    private val cvcSessionsProdEndpoint = "https://access.worldpay.com/sessions/payments/cvc"

    private val discoveryEndpoint = URL("https://localhost:8443$sessionsRootPath")

    private val responseBody = escapeColonsInMatchingRules(
        PactDslJsonBody()
            .`object`("_links")
            .`object`("sessions:session")
            .stringMatcher("href", sessionReferenceRegex, sessionReferenceExample)
            .closeObject()
            .array("curies")
            .`object`()
            .stringMatcher("href", curiesRegex, curiesExample)
            .stringValue("name", "sessions")
            .booleanValue("templated", true)
            .closeObject()
            .closeArray()
            .closeObject()
    )

    private val getSessionsRootResponseBody = escapeColonsInMatchingRules(
        PactDslJsonBody()
            .`object`("_links")
            .`object`("sessions:card")
            .stringMatcher("href", discoveryEndpointRegex, cardSessionsProdEndpoint)
            .closeObject()
            .`object`("sessions:paymentsCvc")
            .stringMatcher("href", sessionEndpointRegex, cvcSessionsProdEndpoint)
            .closeObject()
            .closeObject()
    )

    @Before
    fun setup() {
        HttpsURLConnection.setDefaultSSLSocketFactory(TrustAllSSLSocketFactory())

        cardSessionClient =
            CardSessionClient(
                CardSessionResponseDeserializer(),
                CardSessionRequestSerializer(),
                HttpsClient(dispatcher = coroutinesTestRule.testDispatcher)
            )

        cvcSessionClient =
            CvcSessionClient(
                CvcSessionResponseDeserializer(),
                CvcSessionRequestSerializer(),
                HttpsClient(dispatcher = coroutinesTestRule.testDispatcher)
            )

        discoveryClient = ApiDiscoveryClient(
            httpsClient = HttpsClient(dispatcher = coroutinesTestRule.testDispatcher),
            discoveryCache = DiscoveryCache
        )
    }

    /**
     * Tests for /sessions/ endpoint used for discovering card session and cvc sessions endpoints
     */
    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun createSuccessfulGetRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("GET request to /sessions to discover card session and cvc session endpoints")
            .path(sessionsRootPath)
            .method(GET)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .headers(SESSIONS_ACCEPT_HEADER)
            .willRespondWith()
            .status(200)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .body(getSessionsRootResponseBody)
            .toPact()
    }

    @Test
    @PactVerification("sessions", fragment = "createSuccessfulGetRequestInteraction")
    fun `should receive a valid response when a valid GET request is sent`() = runAsBlockingTest {
        val httpClient = HttpsClient(dispatcher = coroutinesTestRule.testDispatcher)
        val headers = mapOf(ACCEPT_HEADER to SESSIONS_MEDIA_TYPE, CONTENT_TYPE_HEADER to SESSIONS_MEDIA_TYPE)

        val deserializer = DiscoverLinks.cvcSessions.endpoints[1].getDeserializer()

        val response = httpClient.doGet(discoveryEndpoint, deserializer, headers)
        assertEquals(cvcSessionsProdEndpoint, response)
    }

    /**
     * Tests for /sessions/payments/cvc/ endpoint used to create a cvc session
     */
    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun cvcSessionCreateSuccessfulRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("POST request to /sessions/payments/cvc with valid body")
            .path(cvcSessionsPath)
            .method(POST)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .headers(SESSIONS_ACCEPT_HEADER)
            .body(generateCvcSessionRequest(identity))
            .willRespondWith()
            .status(201)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .matchHeader("Location", sessionReferenceRegex, sessionReferenceExample)
            .body(responseBody)
            .toPact()
    }

    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun cvcSessionCreateInvalidIdentityRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("POST request to /sessions/payments/cvc with invalid identity in body")
            .path(cvcSessionsPath)
            .method(POST)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .headers(SESSIONS_ACCEPT_HEADER)
            .body(generateCvcSessionRequest(invalidIdentity))
            .willRespondWith()
            .status(400)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .body(generateResponse("fieldHasInvalidValue", "Identity is invalid", "\$.identity"))
            .toPact()
    }

    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun cvcSessionCreateStringNonNumericalCvcRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("POST request to /sessions/payments/cvc with non-numerical cvc in body")
            .path(cvcSessionsPath)
            .method(POST)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .headers(SESSIONS_ACCEPT_HEADER)
            .body(generateCvcSessionRequest(cvc = cvcNonNumerical))
            .willRespondWith()
            .status(400)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .body(
                generateResponse(
                    "fieldMustBeNumber",
                    "CVC must be numeric",
                    "\$.cvc"
                )
            )
            .toPact()
    }

    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun cvcSessionCreateEmptyBodyErrorInteractionRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("POST request to /sessions/payments/cvc with empty body")
            .path(cvcSessionsPath)
            .method(POST)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .headers(SESSIONS_ACCEPT_HEADER)
            .body("")
            .willRespondWith()
            .status(400)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .body(
                PactDslJsonBody()
                    .stringValue("errorName", "bodyIsEmpty")
                    .stringValue("message", "The body within the request is empty")
            )
            .toPact()
    }

    @Test
    @PactVerification("sessions", fragment = "cvcSessionCreateSuccessfulRequestInteraction")
    fun `should receive a valid response when a valid request is sent`() = runAsBlockingTest {
        val sessionRequest =
            CvcSessionRequest(
                cvc,
                identity
            )
        val expectedCuries = arrayOf(
            SessionResponse.Links.Curies(
                curiesExample,
                "sessions",
                true
            )
        )
        val expectedLinks = SessionResponse.Links(
            SessionResponse.Links.Endpoints(sessionReferenceExample),
            expectedCuries
        )
        val expectedSessionResponse = SessionResponse(expectedLinks)

        assertEquals(
            expectedSessionResponse,
            cvcSessionClient.getSessionResponse(cvcSessionsEndpoint, sessionRequest)
        )
    }

    @Test
    @PactVerification("sessions", fragment = "cvcSessionCreateInvalidIdentityRequestInteraction")
    fun `should receive a 400 response when a Cvc session request is sent with an invalid identity`() =
        runAsBlockingTest {
            val sessionRequest =
                CvcSessionRequest(
                    cvc,
                    invalidIdentity
                )

            try {
                cvcSessionClient.getSessionResponse(cvcSessionsEndpoint, sessionRequest)
                fail("Should not have reached here!")
            } catch (ex: AccessCheckoutException) {
                val validationRule = ValidationRule(
                    "fieldHasInvalidValue",
                    "Identity is invalid",
                    "\$.identity"
                )
                val accessCheckoutClientError = AccessCheckoutException(
                    message = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema",
                    validationRules = listOf(validationRule)
                )
                assertEquals(accessCheckoutClientError, ex)
            } catch (ex: Exception) {
                fail("Should not have reached here!")
            }
        }

    @Test
    @PactVerification("sessions", fragment = "cvcSessionCreateStringNonNumericalCvcRequestInteraction")
    fun `should receive an error when Cvc session request has a non-numeric cvc is provided`() = runAsBlockingTest {
        val sessionRequest =
            CvcSessionRequest(
                cvcNonNumerical,
                identity
            )

        try {
            cvcSessionClient.getSessionResponse(cvcSessionsEndpoint, sessionRequest)
            fail("Should not have reached here!")
        } catch (ex: AccessCheckoutException) {
            val validationRule = ValidationRule(
                "fieldMustBeNumber",
                "CVC must be numeric",
                "\$.cvc"
            )
            val accessCheckoutClientError = AccessCheckoutException(
                message = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema",
                validationRules = listOf(validationRule)
            )
            assertEquals(accessCheckoutClientError, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("sessions", fragment = "cvcSessionCreateEmptyBodyErrorInteractionRequestInteraction")
    fun `should receive a 400 response with error when body of Cvc session request is empty`() =
        runAsBlockingTest {
            val mockEmptySerializer = Mockito.mock(CvcSessionRequestSerializer::class.java)

            val emptyString = ""
            val sessionRequest =
                CvcSessionRequest(
                    emptyString,
                    emptyString
                )

            given(mockEmptySerializer.serialize(sessionRequest)).willReturn(emptyString)

            cvcSessionClient =
                CvcSessionClient(
                    CvcSessionResponseDeserializer(),
                    mockEmptySerializer,
                    HttpsClient(dispatcher = coroutinesTestRule.testDispatcher)
                )

            try {
                cvcSessionClient.getSessionResponse(cvcSessionsEndpoint, sessionRequest)
                fail("Should not have reached here!")
            } catch (ex: AccessCheckoutException) {
                val accessCheckoutClientError = AccessCheckoutException("bodyIsEmpty : The body within the request is empty")
                assertEquals(accessCheckoutClientError, ex)
            } catch (ex: Exception) {
                fail("Should not have reached here!")
            }
        }

    private fun generateCvcSessionRequest(
        identity: String = this.identity,
        cvc: String = this.cvc
    ): PactDslJsonBody {
        return PactDslJsonBody()
            .asBody()
            .stringValue("cvc", cvc)
            .stringValue("identity", identity)
    }

    /**
     * Tests for /sessions/card/ endpoint used to create a card session
     */
    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun cardSessionCreateSuccessfulRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("POST request to /sessions/card with valid body")
            .path(cardSessionsPath)
            .method(POST)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .headers(SESSIONS_ACCEPT_HEADER)
            .body(generateRequest(identity))
            .willRespondWith()
            .status(201)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .matchHeader("Location", sessionReferenceRegex, sessionReferenceExample)
            .body(responseBody)
            .toPact()
    }

    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun cardSessionCreateInvalidIdentityRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("POST request to /sessions/card with invalid identity in body")
            .path(cardSessionsPath)
            .method(POST)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .headers(SESSIONS_ACCEPT_HEADER)
            .body(generateRequest(invalidIdentity))
            .willRespondWith()
            .status(400)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .body(generateResponse("fieldHasInvalidValue", "Identity is invalid", "\$.identity"))
            .toPact()
    }

    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun cardSessionCreateInvalidLuhnRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("POST request to /sessions/card with card number that fails Luhn check")
            .path(cardSessionsPath)
            .method(POST)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .headers(SESSIONS_ACCEPT_HEADER)
            .body(generateRequest(pan = invalidLuhn))
            .willRespondWith()
            .status(400)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .body(
                generateResponse(
                    "panFailedLuhnCheck",
                    "The identified field contains a PAN that has failed the Luhn check.",
                    "\$.cardNumber"
                )
            )
            .toPact()
    }

    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun cardSessionCreateStringTooShortRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("POST request to /sessions/card with card number too short")
            .path(cardSessionsPath)
            .method(POST)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .headers(SESSIONS_ACCEPT_HEADER)
            .body(generateRequest(pan = cardStringTooShort))
            .willRespondWith()
            .status(400)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .body(
                generateResponse(
                    "stringIsTooShort",
                    "Card number is too short - must be between 10 & 19 digits",
                    "\$.cardNumber"
                )
            )
            .toPact()
    }

    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun cardSessionCreateStringTooLongRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("POST request to /sessions/card with card number too long")
            .path(cardSessionsPath)
            .method(POST)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .headers(SESSIONS_ACCEPT_HEADER)
            .body(generateRequest(pan = cardStringTooLong))
            .willRespondWith()
            .status(400)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .body(
                generateResponse(
                    "stringIsTooLong",
                    "Card number is too long - must be between 10 & 19 digits",
                    "\$.cardNumber"
                )
            )
            .toPact()
    }

    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun cardSessionCreateIntegerMonthTooSmallRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("POST request to /sessions/card with expiry date month lower than 1")
            .path(cardSessionsPath)
            .method(POST)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .headers(SESSIONS_ACCEPT_HEADER)
            .body(generateRequest(month = integerMonthTooSmall))
            .willRespondWith()
            .status(400)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .body(
                generateResponse(
                    "integerIsTooSmall",
                    "Card expiry month is too small - must be between 1 & 12",
                    "\$.cardExpiryDate.month"
                )
            )
            .toPact()
    }

    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun cardSessionCreateIntegerMonthTooLargeRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("POST request to /sessions/card with expiry date month greater than 12")
            .path(cardSessionsPath)
            .method(POST)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .headers(SESSIONS_ACCEPT_HEADER)
            .body(generateRequest(month = integerMonthTooLarge))
            .willRespondWith()
            .status(400)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .body(
                generateResponse(
                    "integerIsTooLarge",
                    "Card expiry month is too large - must be between 1 & 12",
                    "\$.cardExpiryDate.month"
                )
            )
            .toPact()
    }

    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun cardSessionCreateStringNonNumericalCvcRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("POST request to /sessions/card with non-numerical cvc")
            .path(cardSessionsPath)
            .method(POST)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .headers(SESSIONS_ACCEPT_HEADER)
            .body(generateRequest(cvc = cvcNonNumerical))
            .willRespondWith()
            .status(400)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .body(
                generateResponse(
                    "fieldMustBeNumber",
                    "CVC must be numeric",
                    "\$.cvc"
                )
            )
            .toPact()
    }

    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun cardSessionCreateEmptyBodyErrorInteractionRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("POST request to /sessions/card with empty body")
            .path(cardSessionsPath)
            .method(POST)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .headers(SESSIONS_ACCEPT_HEADER)
            .body("")
            .willRespondWith()
            .status(400)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .body(
                PactDslJsonBody()
                    .stringValue("errorName", "bodyIsEmpty")
                    .stringValue("message", "The body within the request is empty")
            )
            .toPact()
    }

    @Test
    @PactVerification("sessions", fragment = "cardSessionCreateSuccessfulRequestInteraction")
    fun givenValidCardSessionRequestThenShouldReceiveValidResponse() = runAsBlockingTest {
        val sessionRequest =
            CardSessionRequest(
                cardNumber,
                CardSessionRequest.CardExpiryDate(
                    expiryMonth,
                    expiryYear
                ),
                cvc,
                identity
            )

        val expectedCuries = arrayOf(
            SessionResponse.Links.Curies(
                curiesExample,
                "sessions",
                true
            )
        )
        val expectedLinks = SessionResponse.Links(
            SessionResponse.Links.Endpoints(sessionReferenceExample),
            expectedCuries
        )
        val expectedSessionResponse =
            SessionResponse(
                expectedLinks
            )

        Assert.assertEquals(
            expectedSessionResponse,
            cardSessionClient.getSessionResponse(cardSessionsEndpoint, sessionRequest)
        )
    }

    @Test
    @PactVerification("sessions", fragment = "cardSessionCreateInvalidIdentityRequestInteraction")
    fun givenInvalidIdentityInCardSessionRequestThenShouldReceiveA400ResponseWithIdentityAsReason() =
        runAsBlockingTest {
            val sessionRequest =
                CardSessionRequest(
                    cardNumber,
                    CardSessionRequest.CardExpiryDate(
                        expiryMonth,
                        expiryYear
                    ),
                    cvc,
                    invalidIdentity
                )

            try {
                cardSessionClient.getSessionResponse(cardSessionsEndpoint, sessionRequest)
                fail("Should not have reached here!")
            } catch (ex: AccessCheckoutException) {
                val validationRule = ValidationRule("fieldHasInvalidValue", "Identity is invalid", "\$.identity")
                val accessCheckoutException = AccessCheckoutException(
                    message = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema",
                    validationRules = listOf(validationRule)
                )
                Assert.assertEquals(accessCheckoutException, ex)
            } catch (ex: Exception) {
                fail("Should not have reached here!")
            }
        }

    @Test
    @PactVerification("sessions", fragment = "cardSessionCreateInvalidLuhnRequestInteraction")
    fun givenLuhnInvalidCardInCardSessionRequestThenShouldReceiveA400ResponseWithPANFailedLuhnError() =
        runAsBlockingTest {
            val sessionRequest =
                CardSessionRequest(
                    invalidLuhn,
                    CardSessionRequest.CardExpiryDate(
                        expiryMonth,
                        expiryYear
                    ),
                    cvc,
                    identity
                )

            try {
                cardSessionClient.getSessionResponse(cardSessionsEndpoint, sessionRequest)
                fail("Should not have reached here!")
            } catch (ex: AccessCheckoutException) {
                val validationRule = ValidationRule(
                    "panFailedLuhnCheck",
                    "The identified field contains a PAN that has failed the Luhn check.",
                    "\$.cardNumber"
                )
                val accessCheckoutException = AccessCheckoutException(
                    message = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema",
                    validationRules = listOf(validationRule)
                )
                Assert.assertEquals(accessCheckoutException, ex)
            } catch (ex: Exception) {
                fail("Should not have reached here!")
            }
        }

    @Test
    @PactVerification("sessions", fragment = "cardSessionCreateStringTooShortRequestInteraction")
    fun givenStringTooShortCardInCardSessionRequestThenShouldReceiveA400ResponseWithCorrectError() =
        runAsBlockingTest {
            val sessionRequest =
                CardSessionRequest(
                    cardStringTooShort,
                    CardSessionRequest.CardExpiryDate(
                        expiryMonth,
                        expiryYear
                    ),
                    cvc,
                    identity
                )

            try {
                cardSessionClient.getSessionResponse(cardSessionsEndpoint, sessionRequest)
                fail("Should not have reached here!")
            } catch (ex: AccessCheckoutException) {
                val validationRule = ValidationRule(
                    "stringIsTooShort",
                    "Card number is too short - must be between 10 & 19 digits",
                    "\$.cardNumber"
                )
                val accessCheckoutException = AccessCheckoutException(
                    message = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema",
                    validationRules = listOf(validationRule)
                )
                Assert.assertEquals(accessCheckoutException, ex)
            } catch (ex: Exception) {
                fail("Should not have reached here!")
            }
        }

    @Test
    @PactVerification("sessions", fragment = "cardSessionCreateStringTooLongRequestInteraction")
    fun givenStringTooLongCardInCardSessionRequestThenShouldReceiveA400ResponseWithCorrectError() =
        runAsBlockingTest {
            val sessionRequest =
                CardSessionRequest(
                    cardStringTooLong,
                    CardSessionRequest.CardExpiryDate(
                        expiryMonth,
                        expiryYear
                    ),
                    cvc,
                    identity
                )

            try {
                cardSessionClient.getSessionResponse(cardSessionsEndpoint, sessionRequest)
                fail("Should not have reached here!")
            } catch (ex: AccessCheckoutException) {
                val validationRule = ValidationRule(
                    "stringIsTooLong",
                    "Card number is too long - must be between 10 & 19 digits",
                    "\$.cardNumber"
                )
                val accessCheckoutException = AccessCheckoutException(
                    message = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema",
                    validationRules = listOf(validationRule)
                )
                Assert.assertEquals(accessCheckoutException, ex)
            } catch (ex: Exception) {
                fail("Should not have reached here!")
            }
        }

    @Test
    @PactVerification("sessions", fragment = "cardSessionCreateIntegerMonthTooSmallRequestInteraction")
    fun givenIntegerMonthTooSmallInCardSessionRequestThenShouldReceiveA400ResponseWithCorrectError() =
        runAsBlockingTest {
            val sessionRequest =
                CardSessionRequest(
                    cardNumber,
                    CardSessionRequest.CardExpiryDate(
                        integerMonthTooSmall,
                        expiryYear
                    ),
                    cvc,
                    identity
                )

            try {
                cardSessionClient.getSessionResponse(cardSessionsEndpoint, sessionRequest)
                fail("Should not have reached here!")
            } catch (ex: AccessCheckoutException) {
                val validationRule = ValidationRule(
                    "integerIsTooSmall",
                    "Card expiry month is too small - must be between 1 & 12",
                    "\$.cardExpiryDate.month"
                )
                val accessCheckoutException = AccessCheckoutException(
                    message = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema",
                    validationRules = listOf(validationRule)
                )
                Assert.assertEquals(accessCheckoutException, ex)
            } catch (ex: Exception) {
                fail("Should not have reached here!")
            }
        }

    @Test
    @PactVerification("sessions", fragment = "cardSessionCreateIntegerMonthTooLargeRequestInteraction")
    fun givenIntegerMonthTooLargeInCardSessionRequestThenShouldReceiveA400ResponseWithCorrectError() =
        runAsBlockingTest {
            val sessionRequest =
                CardSessionRequest(
                    cardNumber,
                    CardSessionRequest.CardExpiryDate(
                        integerMonthTooLarge,
                        expiryYear
                    ),
                    cvc,
                    identity
                )

            try {
                cardSessionClient.getSessionResponse(cardSessionsEndpoint, sessionRequest)
                fail("Should not have reached here!")
            } catch (ex: AccessCheckoutException) {
                val validationRule = ValidationRule(
                    "integerIsTooLarge",
                    "Card expiry month is too large - must be between 1 & 12",
                    "\$.cardExpiryDate.month"
                )
                val accessCheckoutException = AccessCheckoutException(
                    message = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema",
                    validationRules = listOf(validationRule)
                )
                Assert.assertEquals(accessCheckoutException, ex)
            } catch (ex: Exception) {
                fail("Should not have reached here!")
            }
        }

    @Test
    @PactVerification("sessions", fragment = "cardSessionCreateStringNonNumericalCvcRequestInteraction")
    fun givenStringNonNumericalInCardSessionRequestThenShouldReceiveA400ResponseWithCorrectError() =
        runAsBlockingTest {
            val sessionRequest =
                CardSessionRequest(
                    cardNumber,
                    CardSessionRequest.CardExpiryDate(
                        expiryMonth,
                        expiryYear
                    ),
                    cvcNonNumerical,
                    identity
                )

            try {
                cardSessionClient.getSessionResponse(cardSessionsEndpoint, sessionRequest)
                fail("Should not have reached here!")
            } catch (ex: AccessCheckoutException) {
                val validationRule = ValidationRule(
                    "fieldMustBeNumber",
                    "CVC must be numeric",
                    "\$.cvc"
                )
                val accessCheckoutException = AccessCheckoutException(
                    message = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema",
                    validationRules = listOf(validationRule)
                )
                Assert.assertEquals(accessCheckoutException, ex)
            } catch (ex: Exception) {
                fail("Should not have reached here!")
            }
        }

    @Test
    @PactVerification("sessions", fragment = "cardSessionCreateEmptyBodyErrorInteractionRequestInteraction")
    fun givenEmptyBodyInTheRequestThenShouldReceiveA400ResponseWithCorrectError() = runAsBlockingTest {
        val mockEmptySerializer = Mockito.mock(CardSessionRequestSerializer::class.java)

        val emptyString = ""
        val sessionRequest =
            CardSessionRequest(
                emptyString,
                CardSessionRequest.CardExpiryDate(
                    1,
                    99
                ),
                emptyString,
                emptyString
            )

        BDDMockito.given(mockEmptySerializer.serialize(sessionRequest)).willReturn(emptyString)

        cardSessionClient =
            CardSessionClient(
                CardSessionResponseDeserializer(),
                mockEmptySerializer,
                HttpsClient(dispatcher = coroutinesTestRule.testDispatcher)
            )

        try {
            cardSessionClient.getSessionResponse(cardSessionsEndpoint, sessionRequest)
            fail("Expected exception but got none")
        } catch (ace: AccessCheckoutException) {
            Assert.assertEquals("bodyIsEmpty : The body within the request is empty", ace.message)
        } catch (ex: Exception) {
            fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
        }
    }

    private fun generateRequest(
        identity: String = this.identity,
        pan: String = cardNumber,
        cvc: String = this.cvc,
        month: Int = expiryMonth,
        year: Int = expiryYear
    ): PactDslJsonBody {
        return PactDslJsonBody()
            .stringValue("cardNumber", pan)
            .`object`("cardExpiryDate")
            .integerType("month", month)
            .integerType("year", year)
            .closeObject()
            .asBody()
            .stringValue("cvc", cvc)
            .stringValue("identity", identity)
    }

    private fun generateResponse(
        brokenRuleErrorName: String,
        brokenRuleMessage: String,
        jsonPath: String
    ) =
        PactDslJsonBody()
            .stringValue("errorName", "bodyDoesNotMatchSchema")
            .stringValue("message", "The json body provided does not match the expected schema")
            .array("validationErrors")
            .`object`()
            .stringValue("errorName", brokenRuleErrorName)
            .stringValue("message", brokenRuleMessage)
            .stringValue("jsonPath", jsonPath)
            .closeObject()
            .closeArray()
}

package com.worldpay.access.checkout.api

import au.com.dius.pact.consumer.Pact
import au.com.dius.pact.consumer.PactProviderRuleMk2
import au.com.dius.pact.consumer.PactVerification
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.model.RequestResponsePact
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryAsyncTaskFactory
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryClient
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.discovery.DiscoveryCache
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.api.exception.ValidationRule
import com.worldpay.access.checkout.session.api.client.ACCEPT_HEADER
import com.worldpay.access.checkout.session.api.client.CONTENT_TYPE_HEADER
import com.worldpay.access.checkout.session.api.client.PaymentsCvcSessionClient
import com.worldpay.access.checkout.session.api.client.SESSIONS_MEDIA_TYPE
import com.worldpay.access.checkout.session.api.request.CvcSessionRequest
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.session.api.serialization.CvcSessionRequestSerializer
import com.worldpay.access.checkout.session.api.serialization.CvcSessionResponseDeserializer
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.net.URL
import kotlin.test.fail

class SessionsPactTest {

    companion object {
        private const val provider = "sessions"
    }

    private lateinit var paymentsCvcSessionClient: PaymentsCvcSessionClient
    private lateinit var discoveryClient: ApiDiscoveryClient

    @Before
    fun setup() {
        paymentsCvcSessionClient =
            PaymentsCvcSessionClient(
                CvcSessionResponseDeserializer(),
                CvcSessionRequestSerializer(),
                HttpClient()
            )

        discoveryClient = ApiDiscoveryClient(
            discoveryCache = DiscoveryCache,
            apiDiscoveryAsyncTaskFactory = ApiDiscoveryAsyncTaskFactory()
        )
    }

    @get:Rule
    var mockProvider = PactProviderRuleMk2(provider, "localhost", 8080, this)


    private val sessionPath = "/sessions/payments/cvc"
    private val discoveryPath = "/sessions"
    private val cvc = "123"
    private val cvcNonNumerical = "aaa"

    private val identity = "identity"
    private val invalidIdentity = "ABC"

    private val sessionReferenceRegex = "https?://[^/]+/sessions/[^/]+"
    private val sessionReferenceExample = "http://access.worldpay.com/sessions/<encrypted-data>"

    private val sessionEndpointRegex = "https?://[^/]+/sessions/.+"
    private val paymentsCvcSessionEndpoint = "http://access.worldpay.com/sessions/payments/cvc"

    private val curiesRegex = "https?://[^/]+/rels/sessions/\\{rel\\}.json"
    private val curiesExample = "http://access.worldpay.com/rels/sessions/{rel}.json"

    private val responseBody = PactDslJsonBody()
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

    private val getResponseBody = PactDslJsonBody()
        .`object`("_links")
        .`object`("sessions:paymentsCvc")
        .stringMatcher("href", sessionEndpointRegex, paymentsCvcSessionEndpoint )
        .closeObject()
        .closeObject()

    @Pact(provider = "sessions", consumer = "access-checkout-sdk")
    fun createSuccessfulGetRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A service discovery request")
            .path(discoveryPath)
            .method("GET")
            .headers("Content-Type", "application/vnd.worldpay.sessions-v1.hal+json")
            .headers("Accept", "application/vnd.worldpay.sessions-v1.hal+json")
            .willRespondWith()
            .status(200)
            .headers(
                mutableMapOf(
                    Pair(
                        "Content-Type",
                        "application/vnd.worldpay.sessions-v1.hal+json"
                    )
                )
            )
            .body(getResponseBody)
            .toPact()
    }

    @Test
    @PactVerification("sessions", fragment = "createSuccessfulGetRequestInteraction")
    fun `should receive a valid response when a valid GET request is sent`() {
        val httpClient  = HttpClient()
        val url = URL(mockProvider.url + discoveryPath)
        val headers = mapOf(ACCEPT_HEADER to SESSIONS_MEDIA_TYPE, CONTENT_TYPE_HEADER to SESSIONS_MEDIA_TYPE )

        val deserializer = DiscoverLinks.sessions.endpoints[1].getDeserializer()

        val response = httpClient.doGet(url, deserializer, headers)
        Assert.assertEquals(paymentsCvcSessionEndpoint, response)
    }

    @Pact(provider = "sessions", consumer = "access-checkout-sdk")
    fun createSuccessfulRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference")
            .path(sessionPath)
            .method("POST")
            .headers("Content-Type", "application/vnd.worldpay.sessions-v1.hal+json")
            .headers("Accept", "application/vnd.worldpay.sessions-v1.hal+json")
            .body(generateRequest(identity))
            .willRespondWith()
            .status(201)
            .headers(
                mutableMapOf(
                    Pair(
                        "Content-Type",
                        "application/vnd.worldpay.sessions-v1.hal+json"
                    )
                )
            )
            .matchHeader("Location", sessionReferenceRegex, sessionReferenceExample)
            .body(responseBody)
            .toPact()
    }

    @Pact(provider = "sessions", consumer = "access-checkout-sdk")
    fun createInvalidIdentityRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with invalid identity")
            .path(sessionPath)
            .method("POST")
            .headers("Content-Type", "application/vnd.worldpay.sessions-v1.hal+json")
            .headers("Accept", "application/vnd.worldpay.sessions-v1.hal+json")
            .body(generateRequest(invalidIdentity))
            .willRespondWith()
            .status(400)
            .headers(
                mutableMapOf(
                    Pair(
                        "Content-Type",
                        "application/vnd.worldpay.sessions-v1.hal+json"
                    )
                )
            )
            .body(generateResponse("fieldHasInvalidValue", "Identity is invalid", "\$.identity"))
            .toPact()
    }

    @Pact(provider = "sessions", consumer = "access-checkout-sdk")
    fun createStringNonNumericalCvcRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with non-numerical CVV")
            .path(sessionPath)
            .method("POST")
            .headers("Content-Type", "application/vnd.worldpay.sessions-v1.hal+json")
            .headers("Accept", "application/vnd.worldpay.sessions-v1.hal+json")
            .body(generateRequest(cvc = cvcNonNumerical))
            .willRespondWith()
            .status(400)
            .headers(
                mutableMapOf(
                    Pair(
                        "Content-Type",
                        "application/vnd.worldpay.sessions-v1.hal+json"
                    )
                )
            )
            .body(
                generateResponse(
                    "fieldMustBeNumber",
                    "CVC must be numeric",
                    "\$.cvc"
                )
            )
            .toPact()
    }

    @Pact(provider = "sessions", consumer = "access-checkout-sdk")
    fun createEmptyBodyErrorInteractionRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with empty body")
            .path(sessionPath)
            .method("POST")
            .headers("Content-Type", "application/vnd.worldpay.sessions-v1.hal+json")
            .headers("Accept", "application/vnd.worldpay.sessions-v1.hal+json")
            .body("")
            .willRespondWith()
            .status(400)
            .headers(
                mapOf(
                    Pair(
                        "Content-Type",
                        "application/vnd.worldpay.sessions-v1.hal+json"
                    )
                )
            )
            .body(
                generateResponseVariation1(
                    "bodyIsEmpty",
                    "The body within the request is empty"
                )
            )
            .toPact()
    }

    @Test
    @PactVerification("sessions", fragment = "createSuccessfulRequestInteraction")
    fun `should receive a valid response when a valid request is sent`() {
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
        val expectedSessionResponse =
            SessionResponse(
                expectedLinks
            )

        Assert.assertEquals(
            expectedSessionResponse,
            paymentsCvcSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
        )
    }

    @Test
    @PactVerification("sessions", fragment = "createInvalidIdentityRequestInteraction")
    fun `should receive a 400 response when a request is sent with an invalid identity`() {
        val sessionRequest =
            CvcSessionRequest(
                cvc,
                invalidIdentity
            )

        try {
            paymentsCvcSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
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
            Assert.assertEquals(accessCheckoutClientError, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("sessions", fragment = "createStringNonNumericalCvcRequestInteraction")
    fun `should receive an error when no numeric cvc is provided`() {
        val sessionRequest =
            CvcSessionRequest(
                cvcNonNumerical,
                identity
            )

        try {
            paymentsCvcSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
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
            Assert.assertEquals(accessCheckoutClientError, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("sessions", fragment = "createEmptyBodyErrorInteractionRequestInteraction")
    fun `should receive a 400 response with error when body of request is empty`() {

        val mockEmptySerializer = Mockito.mock(CvcSessionRequestSerializer::class.java)

        val emptyString = ""
        val sessionRequest =
            CvcSessionRequest(
                emptyString,
                emptyString
            )


        BDDMockito.given(mockEmptySerializer.serialize(sessionRequest))
            .willReturn(emptyString)

        paymentsCvcSessionClient =
            PaymentsCvcSessionClient(
                CvcSessionResponseDeserializer(),
                mockEmptySerializer,
                HttpClient()
            )

        try {
            paymentsCvcSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
            fail("Should not have reached here!")
        } catch (ex: AccessCheckoutException) {

            val accessCheckoutClientError = AccessCheckoutException("bodyIsEmpty : The body within the request is empty")
            Assert.assertEquals(accessCheckoutClientError, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    private fun generateRequest(
        identity: String = this.identity,
        cvc: String = this.cvc
    ): PactDslJsonBody {
        return PactDslJsonBody()
            .asBody()
            .stringValue("cvc", cvc)
            .stringValue("identity", identity)
    }

    private fun generateResponse(brokenRuleErrorName: String, brokenRuleMessage: String, jsonPath: String) =
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

    private fun generateResponseVariation1(errorName: String, message: String) =
        PactDslJsonBody()
            .stringValue("errorName", errorName)
            .stringValue("message", message)
}

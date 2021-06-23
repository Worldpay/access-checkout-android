package com.worldpay.access.checkout.api.pact

import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit.PactHttpsProviderRule
import au.com.dius.pact.consumer.junit.PactVerification
import au.com.dius.pact.core.model.PactSpecVersion
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryAsyncTaskFactory
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryClient
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.discovery.DiscoveryCache
import com.worldpay.access.checkout.api.pact.PactUtils.Companion.escapeColonsInMatchingRules
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.api.exception.ValidationRule
import com.worldpay.access.checkout.client.testutil.TrustAllSSLSocketFactory
import com.worldpay.access.checkout.session.api.client.ACCEPT_HEADER
import com.worldpay.access.checkout.session.api.client.CONTENT_TYPE_HEADER
import com.worldpay.access.checkout.session.api.client.CvcSessionClient
import com.worldpay.access.checkout.session.api.client.SESSIONS_MEDIA_TYPE
import com.worldpay.access.checkout.session.api.request.CvcSessionRequest
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.session.api.serialization.CvcSessionRequestSerializer
import com.worldpay.access.checkout.session.api.serialization.CvcSessionResponseDeserializer
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.test.assertEquals
import kotlin.test.fail
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito
import org.mockito.Mockito

class SessionsPactTest {

    companion object {
        private const val provider = "sessions"
        private const val consumer = "access-checkout-android-sdk"

        private const val POST = "POST"
        private const val GET = "GET"

        private val SESSIONS_CONTENT_TYPE_HEADER = mapOf(Pair(CONTENT_TYPE_HEADER, SESSIONS_MEDIA_TYPE))
        private val SESSIONS_ACCEPT_HEADER = mapOf(Pair(ACCEPT_HEADER, SESSIONS_MEDIA_TYPE))
    }

    private lateinit var cvcSessionClient: CvcSessionClient
    private lateinit var discoveryClient: ApiDiscoveryClient

    @Before
    fun setup() {
        HttpsURLConnection.setDefaultSSLSocketFactory(TrustAllSSLSocketFactory())
        cvcSessionClient =
            CvcSessionClient(
                CvcSessionResponseDeserializer(),
                CvcSessionRequestSerializer(),
                HttpsClient()
            )

        discoveryClient = ApiDiscoveryClient(
            discoveryCache = DiscoveryCache,
            apiDiscoveryAsyncTaskFactory = ApiDiscoveryAsyncTaskFactory()
        )
    }

    @get:Rule
    var mockProvider = PactHttpsProviderRule(provider, "localhost", 8443, true, PactSpecVersion.V3, this)

    private val sessionPath = "/sessions/payments/cvc"
    private val discoveryPath = "/sessions"
    private val cvc = "123"
    private val cvcNonNumerical = "aaa"

    private val identity = "identity"
    private val invalidIdentity = "ABC"

    private val sessionReferenceRegex = "https?://[^/]+/sessions/[^/]+"
    private val sessionReferenceExample = "https://access.worldpay.com/sessions/<encrypted-data>"

    private val sessionEndpointRegex = "https?://[^/]+/sessions/.+"
    private val paymentsCvcSessionEndpoint = "https://access.worldpay.com/sessions/payments/cvc"

    private val curiesRegex = "https?://[^/]+/rels/sessions/\\{rel\\}.json"
    private val curiesExample = "https://access.worldpay.com/rels/sessions/{rel}.json"

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

    private val getResponseBody = escapeColonsInMatchingRules(
        PactDslJsonBody()
            .`object`("_links")
            .`object`("sessions:paymentsCvc")
            .stringMatcher("href", sessionEndpointRegex, paymentsCvcSessionEndpoint)
            .closeObject()
            .closeObject()
    )

    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun createSuccessfulGetRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A service discovery request")
            .path(discoveryPath)
            .method(GET)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .headers(SESSIONS_ACCEPT_HEADER)
            .willRespondWith()
            .status(200)
            .headers(SESSIONS_CONTENT_TYPE_HEADER)
            .body(getResponseBody)
            .toPact()
    }

    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun createSuccessfulRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference")
            .path(sessionPath)
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
    fun createInvalidIdentityRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with invalid identity")
            .path(sessionPath)
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
    fun createStringNonNumericalCvcRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with non-numerical CVV")
            .path(sessionPath)
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
    fun createEmptyBodyErrorInteractionRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with empty body")
            .path(sessionPath)
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
    @PactVerification("sessions", fragment = "createSuccessfulGetRequestInteraction")
    fun `should receive a valid response when a valid GET request is sent`() {
        val httpClient = HttpsClient()
        val url = URL(mockProvider.url + discoveryPath)
        val headers = mapOf(ACCEPT_HEADER to SESSIONS_MEDIA_TYPE, CONTENT_TYPE_HEADER to SESSIONS_MEDIA_TYPE)

        val deserializer = DiscoverLinks.sessions.endpoints[1].getDeserializer()

        val response = httpClient.doGet(url, deserializer, headers)
        Assert.assertEquals(paymentsCvcSessionEndpoint, response)
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

        assertEquals(
            expectedSessionResponse,
            cvcSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
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
            cvcSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
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
            cvcSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
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

        cvcSessionClient =
            CvcSessionClient(
                CvcSessionResponseDeserializer(),
                mockEmptySerializer,
                HttpsClient()
            )

        try {
            cvcSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
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

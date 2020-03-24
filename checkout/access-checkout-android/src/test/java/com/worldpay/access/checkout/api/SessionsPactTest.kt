package com.worldpay.access.checkout.api

import au.com.dius.pact.consumer.Pact
import au.com.dius.pact.consumer.PactProviderRuleMk2
import au.com.dius.pact.consumer.PactVerification
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.model.RequestResponsePact
import com.worldpay.access.checkout.api.session.CVVSessionRequest
import com.worldpay.access.checkout.api.session.SessionResponse
import com.worldpay.access.checkout.api.session.client.CVVSessionClient
import com.worldpay.access.checkout.api.session.serialization.CVVSessionRequestSerializer
import com.worldpay.access.checkout.api.session.serialization.CVVSessionResponseDeserializer
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

    private lateinit var cvvSessionClient: CVVSessionClient

    @Before
    fun setup() {
        cvvSessionClient =
            CVVSessionClient(
                CVVSessionResponseDeserializer(),
                CVVSessionRequestSerializer(),
                HttpClient()
            )
    }

    @get:Rule
    var mockProvider = PactProviderRuleMk2(provider, "localhost", 8080, this)


    private val path = "/sessions/payments/cvc"
    private val cvv = "123"
    private val cvvNonNumerical = "aaa"

    private val identity = "identity"
    private val invalidIdentity = "ABC"

    private val sessionReferenceRegex = "https?://[^/]+/sessions/[^/]+"
    private val sessionReferenceExample = "http://access.worldpay.com/sessions/<encrypted-data>"

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


    @Pact(provider = "sessions", consumer = "access-checkout-android-sdk")
    fun createSuccessfulRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference")
            .path(path)
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

    @Pact(provider = "sessions", consumer = "access-checkout-android-sdk")
    fun createInvalidIdentityRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with invalid identity")
            .path(path)
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

    @Pact(provider = "sessions", consumer = "access-checkout-android-sdk")
    fun createStringNonNumericalCvvRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with non-numerical CVV")
            .path(path)
            .method("POST")
            .headers("Content-Type", "application/vnd.worldpay.sessions-v1.hal+json")
            .headers("Accept", "application/vnd.worldpay.sessions-v1.hal+json")
            .body(generateRequest(cvv = cvvNonNumerical))
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

    @Pact(provider = "sessions", consumer = "access-checkout-android-sdk")
    fun createEmptyBodyErrorInteractionRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with empty body")
            .path(path)
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
            CVVSessionRequest(
                cvv,
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
            cvvSessionClient.getSessionResponse(URL(mockProvider.url + path), sessionRequest)
        )
    }

    @Test
    @PactVerification("sessions", fragment = "createInvalidIdentityRequestInteraction")
    fun `should receive a 400 response when a request is sent with an invalid identity`() {
        val sessionRequest =
            CVVSessionRequest(
                cvv,
                invalidIdentity
            )

        try {
            cvvSessionClient.getSessionResponse(URL(mockProvider.url + path), sessionRequest)
            fail("Should not have reached here!")
        } catch (ex: AccessCheckoutException.AccessCheckoutClientError) {
            val validationRule =
                AccessCheckoutException.ValidationRule(
                    AccessCheckoutException.ValidationRuleName.FIELD_HAS_INVALID_VALUE,
                    "Identity is invalid",
                    "\$.identity"
                )
            val accessCheckoutClientError = AccessCheckoutException.AccessCheckoutClientError(
                AccessCheckoutException.Error.BODY_DOES_NOT_MATCH_SCHEMA,
                "The json body provided does not match the expected schema",
                listOf(validationRule)
            )
            Assert.assertEquals(accessCheckoutClientError, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("sessions", fragment = "createStringNonNumericalCvvRequestInteraction")
    fun `should receive an error when no numeric CVV is provided`() {
        val sessionRequest =
            CVVSessionRequest(
                cvvNonNumerical,
                identity
            )

        try {
            cvvSessionClient.getSessionResponse(URL(mockProvider.url + path), sessionRequest)
            fail("Should not have reached here!")
        } catch (ex: AccessCheckoutException.AccessCheckoutClientError) {
            val validationRule = AccessCheckoutException.ValidationRule(
                AccessCheckoutException.ValidationRuleName.FIELD_MUST_BE_NUMBER,
                "CVC must be numeric",
                "\$.cvc"
            )
            val accessCheckoutClientError = AccessCheckoutException.AccessCheckoutClientError(
                AccessCheckoutException.Error.BODY_DOES_NOT_MATCH_SCHEMA,
                "The json body provided does not match the expected schema",
                listOf(validationRule)
            )
            Assert.assertEquals(accessCheckoutClientError, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("sessions", fragment = "createEmptyBodyErrorInteractionRequestInteraction")
    fun `should receive a 400 response with error when body of request is empty`() {

        val mockEmptySerializer = Mockito.mock(CVVSessionRequestSerializer::class.java)

        val emptyString = ""
        val sessionRequest =
            CVVSessionRequest(
                emptyString,
                emptyString
            )


        BDDMockito.given(mockEmptySerializer.serialize(sessionRequest))
            .willReturn(emptyString)

        cvvSessionClient =
            CVVSessionClient(
                CVVSessionResponseDeserializer(),
                mockEmptySerializer,
                HttpClient()
            )

        try {
            cvvSessionClient.getSessionResponse(URL(mockProvider.url + path), sessionRequest)
            fail("Should not have reached here!")
        } catch (ex: AccessCheckoutException.AccessCheckoutClientError) {

            val accessCheckoutClientError = AccessCheckoutException.AccessCheckoutClientError(
                AccessCheckoutException.Error.BODY_IS_EMPTY,
                "The body within the request is empty"
            )
            Assert.assertEquals(accessCheckoutClientError, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    private fun generateRequest(
        identity: String = this.identity,
        cvv: String = this.cvv
    ): PactDslJsonBody {
        return PactDslJsonBody()
            .asBody()
            .stringValue("cvc", cvv)
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
package com.worldpay.access.checkout.api.pact

import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit.PactHttpsProviderRule
import au.com.dius.pact.consumer.junit.PactVerification
import au.com.dius.pact.core.model.PactSpecVersion
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.pact.PactUtils.Companion.escapeColonsInMatchingRules
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.api.exception.ValidationRule
import com.worldpay.access.checkout.client.testutil.TrustAllSSLSocketFactory
import com.worldpay.access.checkout.session.api.client.ACCEPT_HEADER
import com.worldpay.access.checkout.session.api.client.CONTENT_TYPE_HEADER
import com.worldpay.access.checkout.session.api.client.CardSessionClient
import com.worldpay.access.checkout.session.api.client.VERIFIED_TOKENS_MEDIA_TYPE
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.session.api.serialization.CardSessionRequestSerializer
import com.worldpay.access.checkout.session.api.serialization.CardSessionResponseDeserializer
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.test.assertFailsWith
import kotlin.test.fail

class VerifiedTokensPactTest {

    companion object {
        private const val provider = "verified-tokens"
        private const val consumer = "access-checkout-android-sdk"

        private const val POST = "POST"
        private const val GET = "GET"

        private val VERIFIED_TOKENS_CONTENT_TYPE_HEADER = mapOf(Pair(CONTENT_TYPE_HEADER, VERIFIED_TOKENS_MEDIA_TYPE))
        private val VERIFIED_TOKENS_ACCEPT_HEADER = mapOf(Pair(ACCEPT_HEADER, VERIFIED_TOKENS_MEDIA_TYPE))
    }

    private lateinit var cardSessionClient: CardSessionClient

    @Before
    fun setup() {
        HttpsURLConnection.setDefaultSSLSocketFactory(TrustAllSSLSocketFactory())
        cardSessionClient =
            CardSessionClient(
                CardSessionResponseDeserializer(),
                CardSessionRequestSerializer(),
                HttpsClient()
            )
    }

    @get:Rule
    var mockProvider = PactHttpsProviderRule(provider, "localhost", 8443, true, PactSpecVersion.V3, this)

    private val sessionPath = "/verifiedTokens/sessions"
    private val discoveryPath = "/verifiedTokens"

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

    private val discoveryEndpointRegex = "https?://[^/]+/verifiedTokens/.+"
    private val verifiedTokensSessionEndpoint = "https://access.worldpay.com/verifiedTokens/sessions"

    private val sessionReferenceRegex = "https?://[^/]+/verifiedTokens/sessions/[^/]+"
    private val sessionReferenceExample = "https://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"
    private val curiesRegex = "https?://[^/]+/rels/verifiedTokens/\\{rel\\}.json"
    private val curiesExample = "https://access.worldpay.com/rels/verifiedTokens/{rel}.json"
    
    private val responseBody = escapeColonsInMatchingRules(PactDslJsonBody()
        .`object`("_links")
        .`object`("verifiedTokens:session")
        .stringMatcher("href", sessionReferenceRegex, sessionReferenceExample)
        .closeObject()
        .array("curies")
        .`object`()
        .stringMatcher("href", curiesRegex, curiesExample)
        .stringValue("name", "verifiedTokens")
        .booleanValue("templated", true)
        .closeObject()
        .closeArray()
        .closeObject())

    private val getResponseBody = escapeColonsInMatchingRules(PactDslJsonBody()
        .`object`("_links")
        .`object`("verifiedTokens:sessions")
        .stringMatcher("href", discoveryEndpointRegex, verifiedTokensSessionEndpoint)
        .closeObject()
        .closeObject())

    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun createSuccessfulGetRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A service discovery request")
            .path(discoveryPath)
            .method(GET)
            .headers("Content-Type", VERIFIED_TOKENS_MEDIA_TYPE)
            .headers("Accept", VERIFIED_TOKENS_MEDIA_TYPE)
            .willRespondWith()
            .status(200)
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
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
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
            .headers(VERIFIED_TOKENS_ACCEPT_HEADER)
            .body(generateRequest(identity))
            .willRespondWith()
            .status(201)
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
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
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
            .headers(VERIFIED_TOKENS_ACCEPT_HEADER)
            .body(generateRequest(invalidIdentity))
            .willRespondWith()
            .status(400)
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
            .body(generateResponse("fieldHasInvalidValue", "Identity is invalid", "\$.identity"))
            .toPact()
    }

    @Pact(provider = provider, consumer = consumer)
    @SuppressWarnings("unused")
    fun createInvalidLuhnRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with invalid luhn")
            .path(sessionPath)
            .method(POST)
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
            .headers(VERIFIED_TOKENS_ACCEPT_HEADER)
            .body(generateRequest(pan = invalidLuhn))
            .willRespondWith()
            .status(400)
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
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
    fun createStringTooShortRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with string card property too short")
            .path(sessionPath)
            .method(POST)
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
            .headers(VERIFIED_TOKENS_ACCEPT_HEADER)
            .body(generateRequest(pan = cardStringTooShort))
            .willRespondWith()
            .status(400)
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
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
    fun createStringTooLongRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with string card property too long")
            .path(sessionPath)
            .method(POST)
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
            .headers(VERIFIED_TOKENS_ACCEPT_HEADER)
            .body(generateRequest(pan = cardStringTooLong))
            .willRespondWith()
            .status(400)
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
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
    fun createIntegerMonthTooSmallRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with month int too small")
            .path(sessionPath)
            .method(POST)
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
            .headers(VERIFIED_TOKENS_ACCEPT_HEADER)
            .body(generateRequest(month = integerMonthTooSmall))
            .willRespondWith()
            .status(400)
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
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
    fun createIntegerMonthTooLargeRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with month int too large")
            .path(sessionPath)
            .method(POST)
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
            .headers(VERIFIED_TOKENS_ACCEPT_HEADER)
            .body(generateRequest(month = integerMonthTooLarge))
            .willRespondWith()
            .status(400)
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
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
    fun createStringNonNumericalCvcRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with non-numerical CVV")
            .path(sessionPath)
            .method(POST)
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
            .headers(VERIFIED_TOKENS_ACCEPT_HEADER)
            .body(generateRequest(cvc = cvcNonNumerical))
            .willRespondWith()
            .status(400)
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
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
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
            .headers(VERIFIED_TOKENS_ACCEPT_HEADER)
            .body("")
            .willRespondWith()
            .status(400)
            .headers(VERIFIED_TOKENS_CONTENT_TYPE_HEADER)
            .body(
                PactDslJsonBody()
                    .stringValue("errorName", "bodyIsEmpty")
                    .stringValue("message", "The body within the request is empty")
            )
            .toPact()
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createSuccessfulGetRequestInteraction")
    fun `should receive a valid response when a valid GET request is sent`() {
        val httpClient  = HttpsClient()
        val url = URL(mockProvider.url + discoveryPath)
        val headers = mapOf(
            ACCEPT_HEADER to VERIFIED_TOKENS_MEDIA_TYPE,
            CONTENT_TYPE_HEADER to VERIFIED_TOKENS_MEDIA_TYPE
        )

        val deserializer = DiscoverLinks.verifiedTokens.endpoints[1].getDeserializer()

        val response = httpClient.doGet(url, deserializer, headers)
        assertEquals(verifiedTokensSessionEndpoint, response)
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createSuccessfulRequestInteraction")
    fun givenValidRequestThenShouldReceiveValidResponse() {
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
                "verifiedTokens",
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
            cardSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
        )
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createInvalidIdentityRequestInteraction")
    fun givenInvalidIdentityInTheRequestThenShouldReceiveA400ResponseWithIdentityAsReason() {
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
            cardSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
            fail("Should not have reached here!")
        } catch (ex: AccessCheckoutException) {
            val validationRule = ValidationRule("fieldHasInvalidValue", "Identity is invalid", "\$.identity")
            val accessCheckoutException = AccessCheckoutException(
                message = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema",
                validationRules = listOf(validationRule)
            )
            assertEquals(accessCheckoutException, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createInvalidLuhnRequestInteraction")
    fun givenLuhnInvalidCardInTheRequestThenShouldReceiveA400ResponseWithPANFailedLuhnError() {
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
            cardSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
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
            assertEquals(accessCheckoutException, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createStringTooShortRequestInteraction")
    fun givenStringTooShortCardInTheRequestThenShouldReceiveA400ResponseWithCorrectError() {
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
            cardSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
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
            assertEquals(accessCheckoutException, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createStringTooLongRequestInteraction")
    fun givenStringTooLongCardInTheRequestThenShouldReceiveA400ResponseWithCorrectError() {
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
            cardSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
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
            assertEquals(accessCheckoutException, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createIntegerMonthTooSmallRequestInteraction")
    fun givenIntegerMonthTooSmallInTheRequestThenShouldReceiveA400ResponseWithCorrectError() {
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
            cardSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
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
            assertEquals(accessCheckoutException, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createIntegerMonthTooLargeRequestInteraction")
    fun givenIntegerMonthTooLargeInTheRequestThenShouldReceiveA400ResponseWithCorrectError() {
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
            cardSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
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
            assertEquals(accessCheckoutException, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createStringNonNumericalCvcRequestInteraction")
    fun givenStringNonNumericalRequestThenShouldReceiveA400ResponseWithCorrectError() {
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
            cardSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
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
            assertEquals(accessCheckoutException, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createEmptyBodyErrorInteractionRequestInteraction")
    fun givenEmptyBodyInTheRequestThenShouldReceiveA400ResponseWithCorrectError() {

        val mockEmptySerializer = mock(CardSessionRequestSerializer::class.java)

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


        given(mockEmptySerializer.serialize(sessionRequest)).willReturn(emptyString)

        cardSessionClient =
            CardSessionClient(
                CardSessionResponseDeserializer(),
                mockEmptySerializer,
                HttpsClient()
            )

        val expectedException = AccessCheckoutException("bodyIsEmpty : The body within the request is empty")

        val exception = assertFailsWith<AccessCheckoutException> {
            cardSessionClient.getSessionResponse(URL(mockProvider.url + sessionPath), sessionRequest)
        }

        assertEquals(expectedException, exception)
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

}

package com.worldpay.access.checkout.api

import au.com.dius.pact.consumer.Pact
import au.com.dius.pact.consumer.PactProviderRuleMk2
import au.com.dius.pact.consumer.PactVerification
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.model.RequestResponsePact
import com.worldpay.access.checkout.api.AccessCheckoutException.*
import com.worldpay.access.checkout.api.serialization.SessionRequestSerializer
import com.worldpay.access.checkout.api.serialization.SessionResponseDeserializer
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import java.net.URL
import kotlin.test.fail


class PactTest {

    companion object {
        private const val provider = "verified-tokens"
    }

    private lateinit var sessionClient: SessionClientImpl

    @Before
    fun setup() {
        sessionClient = SessionClientImpl(SessionResponseDeserializer(), SessionRequestSerializer(), HttpClient())
    }

    @get:Rule
    var mockProvider = PactProviderRuleMk2(provider, "localhost", 8080, this)

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    private val path = "/verifiedTokens/sessions"
    private val cardNumber = "1111222233334444"
    private val invalidLuhn = "444444444444444"
    private val cardStringTooShort = "1111"
    private val cardStringTooLong = "33333333333333333391111111111111"

    private val expiryMonth = 12
    private val nonIntegerMonth = "aa"
    private val integerMonthTooSmall = 0
    private val integerMonthTooLarge = 13
    private val expiryYear = 2020

    private val cvv = "123"
    private val cvvNonNumerical = "aaa"

    private val identity = "identity"
    private val invalidIdentity = "ABC"

    private val sessionReferenceRegex = "https?://[^/]+/verifiedTokens/sessions/[^/]+"
    private val sessionReferenceExample = "http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"
    private val curiesRegex = "https?://[^/]+/rels/verifiedTokens/\\{rel\\}.json"
    private val curiesExample = "http://access.worldpay.com/rels/verifiedTokens/{rel}.json"
    private val responseBody = PactDslJsonBody()
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
        .closeObject()

    @Pact(provider = "verified-tokens", consumer = "access-checkout-android-sdk")
    fun createSuccessfulRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference")
            .path(path)
            .method("POST")
            .headers("Content-Type", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .headers("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .body(generateRequest(identity))
            .willRespondWith()
            .status(201)
            .headers(
                mutableMapOf(
                    Pair(
                        "Content-Type",
                        "application/vnd.worldpay.verified-tokens-v1.hal+json"
                    )
                )
            )
            .matchHeader("Location", sessionReferenceRegex, sessionReferenceExample)
            .body(responseBody)
            .toPact()
    }

    @Pact(provider = "verified-tokens", consumer = "access-checkout-android-sdk")
    fun createInvalidIdentityRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with invalid identity")
            .path(path)
            .method("POST")
            .headers("Content-Type", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .headers("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .body(generateRequest(invalidIdentity))
            .willRespondWith()
            .status(400)
            .headers(
                mutableMapOf(
                    Pair(
                        "Content-Type",
                        "application/vnd.worldpay.verified-tokens-v1.hal+json"
                    )
                )
            )
            .body(generateResponse("fieldHasInvalidValue", "Identity is invalid", "\$.identity"))
            .toPact()
    }

    @Pact(provider = "verified-tokens", consumer = "access-checkout-android-sdk")
    fun createInvalidLuhnRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with invalid luhn")
            .path(path)
            .method("POST")
            .headers("Content-Type", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .headers("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .body(generateRequest(pan = invalidLuhn))
            .willRespondWith()
            .status(400)
            .headers(
                mutableMapOf(
                    Pair(
                        "Content-Type",
                        "application/vnd.worldpay.verified-tokens-v1.hal+json"
                    )
                )
            )
            .body(
                generateResponse(
                    "panFailedLuhnCheck",
                    "The identified field contains a PAN that has failed the Luhn check.",
                    "\$.cardNumber"
                )
            )
            .toPact()
    }

    @Pact(provider = "verified-tokens", consumer = "access-checkout-android-sdk")
    fun createStringTooShortRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with string card property too short")
            .path(path)
            .method("POST")
            .headers("Content-Type", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .headers("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .body(generateRequest(pan = cardStringTooShort))
            .willRespondWith()
            .status(400)
            .headers(
                mutableMapOf(
                    Pair(
                        "Content-Type",
                        "application/vnd.worldpay.verified-tokens-v1.hal+json"
                    )
                )
            )
            .body(
                generateResponse(
                    "stringIsTooShort",
                    "Card number is too short - must be between 10 & 19 digits",
                    "\$.cardNumber"
                )
            )
            .toPact()
    }

    @Pact(provider = "verified-tokens", consumer = "access-checkout-android-sdk")
    fun createStringTooLongRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with string card property too long")
            .path(path)
            .method("POST")
            .headers("Content-Type", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .headers("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .body(generateRequest(pan = cardStringTooLong))
            .willRespondWith()
            .status(400)
            .headers(
                mutableMapOf(
                    Pair(
                        "Content-Type",
                        "application/vnd.worldpay.verified-tokens-v1.hal+json"
                    )
                )
            )
            .body(
                generateResponse(
                    "stringIsTooLong",
                    "Card number is too long - must be between 10 & 19 digits",
                    "\$.cardNumber"
                )
            )
            .toPact()
    }

    @Pact(provider = "verified-tokens", consumer = "access-checkout-android-sdk")
    fun createIntegerMonthTooSmallRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with month int too small")
            .path(path)
            .method("POST")
            .headers("Content-Type", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .headers("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .body(generateRequest(month = integerMonthTooSmall))
            .willRespondWith()
            .status(400)
            .headers(
                mutableMapOf(
                    Pair(
                        "Content-Type",
                        "application/vnd.worldpay.verified-tokens-v1.hal+json"
                    )
                )
            )
            .body(
                generateResponse(
                    "integerIsTooSmall",
                    "Card expiry month is too small - must be between 1 & 12",
                    "\$.cardExpiryDate.month"
                )
            )
            .toPact()
    }

    @Pact(provider = "verified-tokens", consumer = "access-checkout-android-sdk")
    fun createIntegerMonthTooLargeRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with month int too large")
            .path(path)
            .method("POST")
            .headers("Content-Type", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .headers("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .body(generateRequest(month = integerMonthTooLarge))
            .willRespondWith()
            .status(400)
            .headers(
                mutableMapOf(
                    Pair(
                        "Content-Type",
                        "application/vnd.worldpay.verified-tokens-v1.hal+json"
                    )
                )
            )
            .body(
                generateResponse(
                    "integerIsTooLarge",
                    "Card expiry month is too large - must be between 1 & 12",
                    "\$.cardExpiryDate.month"
                )
            )
            .toPact()
    }

    @Pact(provider = "verified-tokens", consumer = "access-checkout-android-sdk")
    fun createStringNonNumericalCvvRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with non-numerical CVV")
            .path(path)
            .method("POST")
            .headers("Content-Type", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .headers("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .body(generateRequest(cvv = cvvNonNumerical))
            .willRespondWith()
            .status(400)
            .headers(
                mutableMapOf(
                    Pair(
                        "Content-Type",
                        "application/vnd.worldpay.verified-tokens-v1.hal+json"
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

    @Pact(provider = "verified-tokens", consumer = "access-checkout-android-sdk")
    fun createEmptyBodyErrorInteractionRequestInteraction(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .uponReceiving("A request for a session reference with empty body")
            .path(path)
            .method("POST")
            .headers("Content-Type", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .headers("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .body("")
            .willRespondWith()
            .status(400)
            .headers(
                mapOf(
                    Pair(
                        "Content-Type",
                        "application/vnd.worldpay.verified-tokens-v1.hal+json"
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
    @PactVerification("verified-tokens", fragment = "createSuccessfulRequestInteraction")
    fun givenValidRequestThenShouldReceiveValidResponse() {
        val sessionRequest =
            CardSessionRequest(cardNumber, CardSessionRequest.CardExpiryDate(expiryMonth, expiryYear), cvv, identity)

        val expectedCuries = arrayOf(
            SessionResponse.Links.Curies(
                curiesExample,
                "verifiedTokens",
                true
            )
        )
        val expectedLinks = SessionResponse.Links(
            SessionResponse.Links.VerifiedTokensSession(sessionReferenceExample),
            expectedCuries
        )
        val expectedSessionResponse = SessionResponse(expectedLinks)

        assertEquals(
            expectedSessionResponse,
            sessionClient.getSessionResponse(URL(mockProvider.url + path), sessionRequest)
        )
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createInvalidIdentityRequestInteraction")
    fun givenInvalidIdentityInTheRequestThenShouldReceiveA400ResponseWithIdentityAsReason() {
        val sessionRequest =
            CardSessionRequest(cardNumber, CardSessionRequest.CardExpiryDate(expiryMonth, expiryYear), cvv, invalidIdentity)

        try {
            sessionClient.getSessionResponse(URL(mockProvider.url + path), sessionRequest)
            fail("Should not have reached here!")
        } catch (ex: AccessCheckoutClientError) {
            val validationRule =
                ValidationRule(ValidationRuleName.FIELD_HAS_INVALID_VALUE, "Identity is invalid", "\$.identity")
            val accessCheckoutClientError = AccessCheckoutClientError(
                Error.BODY_DOES_NOT_MATCH_SCHEMA,
                "The json body provided does not match the expected schema",
                listOf(validationRule)
            )
            assertEquals(accessCheckoutClientError, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createInvalidLuhnRequestInteraction")
    fun givenLuhnInvalidCardInTheRequestThenShouldReceiveA400ResponseWithPANFailedLuhnError() {
        val sessionRequest =
            CardSessionRequest(invalidLuhn, CardSessionRequest.CardExpiryDate(expiryMonth, expiryYear), cvv, identity)

        try {
            sessionClient.getSessionResponse(URL(mockProvider.url + path), sessionRequest)
            fail("Should not have reached here!")
        } catch (ex: AccessCheckoutClientError) {
            val validationRule = ValidationRule(
                ValidationRuleName.PAN_FAILED_LUHN_CHECK,
                "The identified field contains a PAN that has failed the Luhn check.",
                "\$.cardNumber"
            )
            val accessCheckoutClientError = AccessCheckoutClientError(
                Error.BODY_DOES_NOT_MATCH_SCHEMA,
                "The json body provided does not match the expected schema",
                listOf(validationRule)
            )
            assertEquals(accessCheckoutClientError, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createStringTooShortRequestInteraction")
    fun givenStringTooShortCardInTheRequestThenShouldReceiveA400ResponseWithCorrectError() {
        val sessionRequest =
            CardSessionRequest(cardStringTooShort, CardSessionRequest.CardExpiryDate(expiryMonth, expiryYear), cvv, identity)

        try {
            sessionClient.getSessionResponse(URL(mockProvider.url + path), sessionRequest)
            fail("Should not have reached here!")
        } catch (ex: AccessCheckoutClientError) {
            val validationRule = ValidationRule(
                ValidationRuleName.STRING_IS_TOO_SHORT,
                "Card number is too short - must be between 10 & 19 digits",
                "\$.cardNumber"
            )
            val accessCheckoutClientError = AccessCheckoutClientError(
                Error.BODY_DOES_NOT_MATCH_SCHEMA,
                "The json body provided does not match the expected schema",
                listOf(validationRule)
            )
            assertEquals(accessCheckoutClientError, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createStringTooLongRequestInteraction")
    fun givenStringTooLongCardInTheRequestThenShouldReceiveA400ResponseWithCorrectError() {
        val sessionRequest =
            CardSessionRequest(cardStringTooLong, CardSessionRequest.CardExpiryDate(expiryMonth, expiryYear), cvv, identity)

        try {
            sessionClient.getSessionResponse(URL(mockProvider.url + path), sessionRequest)
            fail("Should not have reached here!")
        } catch (ex: AccessCheckoutClientError) {
            val validationRule = ValidationRule(
                ValidationRuleName.STRING_IS_TOO_LONG,
                "Card number is too long - must be between 10 & 19 digits",
                "\$.cardNumber"
            )
            val accessCheckoutClientError = AccessCheckoutClientError(
                Error.BODY_DOES_NOT_MATCH_SCHEMA,
                "The json body provided does not match the expected schema",
                listOf(validationRule)
            )
            assertEquals(accessCheckoutClientError, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createIntegerMonthTooSmallRequestInteraction")
    fun givenIntegerMonthTooSmallInTheRequestThenShouldReceiveA400ResponseWithCorrectError() {
        val sessionRequest =
            CardSessionRequest(cardNumber, CardSessionRequest.CardExpiryDate(integerMonthTooSmall, expiryYear), cvv, identity)

        try {
            sessionClient.getSessionResponse(URL(mockProvider.url + path), sessionRequest)
            fail("Should not have reached here!")
        } catch (ex: AccessCheckoutClientError) {
            val validationRule = ValidationRule(
                ValidationRuleName.INTEGER_IS_TOO_SMALL,
                "Card expiry month is too small - must be between 1 & 12",
                "\$.cardExpiryDate.month"
            )
            val accessCheckoutClientError = AccessCheckoutClientError(
                Error.BODY_DOES_NOT_MATCH_SCHEMA,
                "The json body provided does not match the expected schema",
                listOf(validationRule)
            )
            assertEquals(accessCheckoutClientError, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createIntegerMonthTooLargeRequestInteraction")
    fun givenIntegerMonthTooLargeInTheRequestThenShouldReceiveA400ResponseWithCorrectError() {
        val sessionRequest =
            CardSessionRequest(cardNumber, CardSessionRequest.CardExpiryDate(integerMonthTooLarge, expiryYear), cvv, identity)

        try {
            sessionClient.getSessionResponse(URL(mockProvider.url + path), sessionRequest)
            fail("Should not have reached here!")
        } catch (ex: AccessCheckoutClientError) {
            val validationRule = ValidationRule(
                ValidationRuleName.INTEGER_IS_TOO_LARGE,
                "Card expiry month is too large - must be between 1 & 12",
                "\$.cardExpiryDate.month"
            )
            val accessCheckoutClientError = AccessCheckoutClientError(
                Error.BODY_DOES_NOT_MATCH_SCHEMA,
                "The json body provided does not match the expected schema",
                listOf(validationRule)
            )
            assertEquals(accessCheckoutClientError, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createStringNonNumericalCvvRequestInteraction")
    fun givenStringNonNumericalRequestThenShouldReceiveA400ResponseWithCorrectError() {
        val sessionRequest =
            CardSessionRequest(
                cardNumber,
                CardSessionRequest.CardExpiryDate(expiryMonth, expiryYear),
                cvvNonNumerical,
                identity
            )

        try {
            sessionClient.getSessionResponse(URL(mockProvider.url + path), sessionRequest)
            fail("Should not have reached here!")
        } catch (ex: AccessCheckoutClientError) {
            val validationRule = ValidationRule(
                ValidationRuleName.FIELD_MUST_BE_NUMBER,
                "CVC must be numeric",
                "\$.cvc"
            )
            val accessCheckoutClientError = AccessCheckoutClientError(
                Error.BODY_DOES_NOT_MATCH_SCHEMA,
                "The json body provided does not match the expected schema",
                listOf(validationRule)
            )
            assertEquals(accessCheckoutClientError, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }

    @Test
    @PactVerification("verified-tokens", fragment = "createEmptyBodyErrorInteractionRequestInteraction")
    fun givenEmptyBodyInTheRequestThenShouldReceiveA400ResponseWithCorrectError() {

        val mockEmptySerializer = mock(SessionRequestSerializer::class.java)

        val emptyString = ""
        val sessionRequest =
            CardSessionRequest(emptyString, CardSessionRequest.CardExpiryDate(1, 99), emptyString, emptyString)


        given(mockEmptySerializer.serialize(sessionRequest))
            .willReturn(emptyString)

        sessionClient = SessionClientImpl(SessionResponseDeserializer(), mockEmptySerializer, HttpClient())

        try {
            sessionClient.getSessionResponse(URL(mockProvider.url + path), sessionRequest)
            fail("Should not have reached here!")
        } catch (ex: AccessCheckoutClientError) {

            val accessCheckoutClientError = AccessCheckoutClientError(
                Error.BODY_IS_EMPTY,
                "The body within the request is empty"
            )
            assertEquals(accessCheckoutClientError, ex)
        } catch (ex: Exception) {
            fail("Should not have reached here!")
        }
    }


    private fun generateEmptyBodyRequest() = ""

    private fun generateRequest(
        identity: String = this.identity,
        pan: String = cardNumber,
        cvv: String = this.cvv,
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
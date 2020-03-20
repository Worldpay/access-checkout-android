package com.worldpay.access.checkout.api

import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutClientError
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutHttpException
import com.worldpay.access.checkout.api.session.CardSessionRequest
import com.worldpay.access.checkout.api.session.RequestDispatcher
import com.worldpay.access.checkout.api.session.SessionClient
import com.worldpay.access.checkout.api.session.SessionClientFactory
import org.awaitility.Awaitility.await
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class RequestDispatcherTest {

    private val verifiedTokensEndpoint = "verifiedTokens"

    private val sessionRequest =
        CardSessionRequest(
            "1111222233334444",
            CardSessionRequest.CardExpiryDate(
                12,
                2020
            ),
            "123",
            "MERCHANT-123"
        )


    private lateinit var sessionClient: SessionClient
    private lateinit var sessionClientFactory: SessionClientFactory

    @Before
    fun setup() {
        sessionClient = Mockito.mock(SessionClient::class.java)
        sessionClientFactory = Mockito.mock(SessionClientFactory::class.java)
        given(sessionClientFactory.createClient()).willReturn(sessionClient)
    }

    private val baseUrl = "http://localhost"

    @Test
    fun givenValidRequest_shouldReturnSuccessfulResponse() {

        val expectedCuries = arrayOf(
            SessionResponse.Links.Curies(
                "https://access.worldpay.com/rels/verifiedTokens{rel}.json",
                "verifiedTokens",
                true
            )
        )
        val expectedLinks = SessionResponse.Links(
            SessionResponse.Links.VerifiedTokensSession("http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"),
            expectedCuries
        )
        val expectedSessionResponse = SessionResponse(expectedLinks)

        given(sessionClient.getSessionResponse(URL("$baseUrl/$verifiedTokensEndpoint"), sessionRequest)).willReturn(
            expectedSessionResponse
        )

        var assertResponse = false

        val responseListener = object :
            Callback<SessionResponse> {
            override fun onResponse(error: Exception?, response: SessionResponse?) {
                assertResponse = expectedSessionResponse == response!!
                assertTrue(assertResponse)
                assertNull(error)
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute(sessionRequest)

        await().atMost(5, TimeUnit.SECONDS).until { assertResponse }
    }

    @Test
    fun givenAClientError_ThenShouldReturnErrorInCallback() {
        given(sessionClient.getSessionResponse(URL("$baseUrl/$verifiedTokensEndpoint"), sessionRequest)).willThrow(
            AccessCheckoutClientError(AccessCheckoutException.Error.INTERNAL_ERROR_OCCURRED, "Some message")
        )

        var assertResponse = false

        val responseListener = object :
            Callback<SessionResponse> {
            override fun onResponse(error: Exception?, response: SessionResponse?) {
                assertResponse = error is AccessCheckoutClientError && error.message == "Some message"
                assertTrue(assertResponse)
                assertNull(response)
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute(sessionRequest)

        await().atMost(5, TimeUnit.SECONDS).until { assertResponse }
    }

    @Test
    fun givenHttpException_ThenShouldReturnExceptionInCallback() {
        given(sessionClient.getSessionResponse(URL("$baseUrl/$verifiedTokensEndpoint"), sessionRequest)).willThrow(
            AccessCheckoutHttpException(
                "Some message",
                null
            )
        )

        var assertResponse = false

        val responseListener = object :
            Callback<SessionResponse> {
            override fun onResponse(error: Exception?, response: SessionResponse?) {
                assertResponse = error is AccessCheckoutHttpException && error.message == "Some message"
                assertTrue(assertResponse)
                assertNull(response)
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute(sessionRequest)

        await().atMost(5, TimeUnit.SECONDS).until { assertResponse }

    }

    @Test
    fun givenHttpExceptionWithEmptyMessage_ThenShouldReturnExceptionWithDefaultMessageInCallback() {
        given(sessionClient.getSessionResponse(URL("$baseUrl/$verifiedTokensEndpoint"), sessionRequest)).willThrow(
            AccessCheckoutHttpException("", null)
        )

        var assertResponse = false

        val responseListener = object :
            Callback<SessionResponse> {
            override fun onResponse(error: Exception?, response: SessionResponse?) {
                assertResponse = error is AccessCheckoutHttpException && error.message == "An exception was thrown when trying to establish a connection"
                assertTrue(assertResponse)
                assertNull(response)
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute(sessionRequest)

        await().atMost(5, TimeUnit.SECONDS).until { assertResponse }

    }

    @Test
    fun givenHttpExceptionWithNullMessage_ThenShouldReturnExceptionWithDefaultMessageInCallback() {
        given(sessionClient.getSessionResponse(URL("$baseUrl/$verifiedTokensEndpoint"), sessionRequest)).willThrow(
            AccessCheckoutHttpException(null, null)
        )

        var assertResponse = false

        val responseListener = object :
            Callback<SessionResponse> {
            override fun onResponse(error: Exception?, response: SessionResponse?) {
                assertResponse = error is AccessCheckoutHttpException && error.message == "An exception was thrown when trying to establish a connection"
                assertTrue(assertResponse)
                assertNull(response)
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute(sessionRequest)

        await().atMost(5, TimeUnit.SECONDS).until { assertResponse }

    }

    @Test
    fun givenSomeOtherException_ThenShouldReturnExceptionInCallback() {
        given(sessionClient.getSessionResponse(URL("$baseUrl/$verifiedTokensEndpoint"), sessionRequest)).willThrow(
            IllegalStateException("Something went badly wrong!")
        )

        var assertResponse = false

        val responseListener = object :
            Callback<SessionResponse> {
            override fun onResponse(error: Exception?, response: SessionResponse?) {
                assertResponse = error is IllegalStateException && error.message == "Something went badly wrong!"
                assertTrue(assertResponse)
                assertNull(response)
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute(sessionRequest)

        await().atMost(5, TimeUnit.SECONDS).until { assertResponse }

    }

    @Test
    fun givenNoRequestParametersPassed_ThenShouldReturnExceptionInCallback() {
        var assertResponse = false

        val responseListener = object :
            Callback<SessionResponse> {
            override fun onResponse(error: Exception?, response: SessionResponse?) {
                assertResponse =
                    error is AccessCheckoutHttpException && error.message == "No request was supplied for sending"
                assertTrue("Actual error is '$error', message is '${error?.message}'") { assertResponse }
                assertNull(response)
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute()

        await().atMost(5, TimeUnit.SECONDS).until { assertResponse }
    }

}
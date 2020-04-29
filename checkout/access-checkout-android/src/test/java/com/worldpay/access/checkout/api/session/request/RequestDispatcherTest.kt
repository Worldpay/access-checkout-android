package com.worldpay.access.checkout.api.session.request

import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutClientError
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutHttpException
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.session.CardSessionRequest
import com.worldpay.access.checkout.api.session.SessionRequestInfo
import com.worldpay.access.checkout.api.session.SessionResponse
import com.worldpay.access.checkout.api.session.SessionResponseInfo
import com.worldpay.access.checkout.api.session.client.SessionClient
import com.worldpay.access.checkout.api.session.client.SessionClientFactory
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
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
    private val baseUrl = "http://localhost"

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

    private val sessionRequestInfo = SessionRequestInfo.Builder()
        .baseUrl(baseUrl)
        .requestBody(sessionRequest)
        .sessionType(VERIFIED_TOKEN_SESSION)
        .discoverLinks(DiscoverLinks.verifiedTokens)
        .build()

    private lateinit var sessionClient: SessionClient
    private lateinit var sessionClientFactory: SessionClientFactory

    @Before
    fun setup() {
        sessionClient = Mockito.mock(SessionClient::class.java)
        sessionClientFactory = Mockito.mock(SessionClientFactory::class.java)
        given(sessionClientFactory.createClient(sessionRequest)).willReturn(sessionClient)
    }

    @Test
    fun givenValidRequest_shouldReturnSuccessfulResponse() {

        val expectedCuries = arrayOf(
            SessionResponse.Links.Curies(
                "https://access.worldpay.com/rels/verifiedTokens{rel}.json",
                "verifiedTokens",
                true
            )
        )
        val expectedLinks =
            SessionResponse.Links(
                SessionResponse.Links.Endpoints(
                    "http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"
                ),
                expectedCuries
            )
        val expectedSessionResponse =
            SessionResponse(
                expectedLinks
            )

        given(sessionClient.getSessionResponse(URL("$baseUrl/$verifiedTokensEndpoint"), sessionRequest)).willReturn(
            expectedSessionResponse
        )

        var assertResponse = false

        val responseListener = object :
            Callback<SessionResponseInfo> {
            override fun onResponse(error: Exception?, response: SessionResponseInfo?) {
                assertResponse = expectedSessionResponse == response?.responseBody
                assertTrue(assertResponse)
                assertNull(error)
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute(sessionRequestInfo)

        await().atMost(5, TimeUnit.SECONDS).until { assertResponse }
    }

    @Test
    fun givenAClientError_ThenShouldReturnErrorInCallback() {
        given(sessionClient.getSessionResponse(URL("$baseUrl/$verifiedTokensEndpoint"), sessionRequest)).willThrow(
            AccessCheckoutClientError(AccessCheckoutException.Error.INTERNAL_ERROR_OCCURRED, "Some message")
        )

        var assertResponse = false

        val responseListener = object :
            Callback<SessionResponseInfo> {
            override fun onResponse(error: Exception?, response: SessionResponseInfo?) {
                assertResponse = error is AccessCheckoutClientError && error.message == "Some message"
                assertTrue(assertResponse)
                assertNull(response)
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute(sessionRequestInfo)

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
            Callback<SessionResponseInfo> {
            override fun onResponse(error: Exception?, response: SessionResponseInfo?) {
                assertResponse = error is AccessCheckoutHttpException && error.message == "Some message"
                assertTrue(assertResponse)
                assertNull(response)
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute(sessionRequestInfo)

        await().atMost(5, TimeUnit.SECONDS).until { assertResponse }

    }

    @Test
    fun givenHttpExceptionWithEmptyMessage_ThenShouldReturnExceptionWithDefaultMessageInCallback() {
        given(sessionClient.getSessionResponse(URL("$baseUrl/$verifiedTokensEndpoint"), sessionRequest)).willThrow(
            AccessCheckoutHttpException("", null)
        )

        var assertResponse = false

        val responseListener = object :
            Callback<SessionResponseInfo> {
            override fun onResponse(error: Exception?, response: SessionResponseInfo?) {
                assertResponse = error is AccessCheckoutHttpException && error.message == "An exception was thrown when trying to establish a connection"
                assertTrue(assertResponse)
                assertNull(response)
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute(sessionRequestInfo)

        await().atMost(5, TimeUnit.SECONDS).until { assertResponse }

    }

    @Test
    fun givenHttpExceptionWithNullMessage_ThenShouldReturnExceptionWithDefaultMessageInCallback() {
        given(sessionClient.getSessionResponse(URL("$baseUrl/$verifiedTokensEndpoint"), sessionRequest)).willThrow(
            AccessCheckoutHttpException(null, null)
        )

        var assertResponse = false

        val responseListener = object :
            Callback<SessionResponseInfo> {
            override fun onResponse(error: Exception?, response: SessionResponseInfo?) {
                assertResponse = error is AccessCheckoutHttpException && error.message == "An exception was thrown when trying to establish a connection"
                assertTrue(assertResponse)
                assertNull(response)
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute(sessionRequestInfo)

        await().atMost(5, TimeUnit.SECONDS).until { assertResponse }

    }

    @Test
    fun givenSomeOtherException_ThenShouldReturnExceptionInCallback() {
        given(sessionClient.getSessionResponse(URL("$baseUrl/$verifiedTokensEndpoint"), sessionRequest)).willThrow(
            IllegalStateException("Something went badly wrong!")
        )

        var assertResponse = false

        val responseListener = object :
            Callback<SessionResponseInfo> {
            override fun onResponse(error: Exception?, response: SessionResponseInfo?) {
                assertResponse = error is IllegalStateException && error.message == "Something went badly wrong!"
                assertTrue(assertResponse)
                assertNull(response)
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute(sessionRequestInfo)

        await().atMost(5, TimeUnit.SECONDS).until { assertResponse }

    }

    @Test
    fun givenNoRequestParametersPassed_ThenShouldReturnExceptionInCallback() {
        var assertResponse = false

        val responseListener = object :
            Callback<SessionResponseInfo> {
            override fun onResponse(error: Exception?, response: SessionResponseInfo?) {
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
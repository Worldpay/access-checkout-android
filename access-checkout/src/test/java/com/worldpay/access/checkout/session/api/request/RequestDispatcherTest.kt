package com.worldpay.access.checkout.session.api.request

import android.os.Looper.getMainLooper
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.session.api.client.SessionClient
import com.worldpay.access.checkout.session.api.client.SessionClientFactory
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.session.api.response.SessionResponseInfo
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class RequestDispatcherTest {

    private val verifiedTokensEndpoint = "verifiedTokens"
    private val baseUrl = "https://localhost:8443"

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
        .sessionType(CARD)
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
                    "https://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"
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
        ).execute(sessionRequestInfo).get()

        shadowOf(getMainLooper()).idle()

        assertTrue { assertResponse }
    }

    @Test
    fun givenAClientError_ThenShouldReturnErrorInCallback() {
        val expectedException = AccessCheckoutException("internalErrorOccurred : Some message")
        given(sessionClient.getSessionResponse(URL("$baseUrl/$verifiedTokensEndpoint"), sessionRequest)).willThrow(expectedException)

        var assertResponse = false

        val responseListener = object :
            Callback<SessionResponseInfo> {
            override fun onResponse(error: Exception?, response: SessionResponseInfo?) {
                assertEquals(expectedException, error)
                assertNull(response)
                assertResponse = true
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute(sessionRequestInfo).get()

        shadowOf(getMainLooper()).idle()

        assertTrue { assertResponse }
    }

    @Test
    fun givenHttpException_ThenShouldReturnExceptionInCallback() {
        given(sessionClient.getSessionResponse(URL("$baseUrl/$verifiedTokensEndpoint"), sessionRequest)).willThrow(
            AccessCheckoutException(
                "Some message",
                null
            )
        )

        var assertResponse = false

        val responseListener = object :
            Callback<SessionResponseInfo> {
            override fun onResponse(error: Exception?, response: SessionResponseInfo?) {
                assertResponse = error is AccessCheckoutException && error.message == "Some message"
                assertTrue(assertResponse)
                assertNull(response)
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute(sessionRequestInfo).get()

        shadowOf(getMainLooper()).idle()

        assertTrue { assertResponse }
    }

    @Test
    fun givenHttpExceptionWithEmptyMessage_ThenShouldReturnExceptionWithDefaultMessageInCallback() {
        val expectedException = AccessCheckoutException("")
        given(sessionClient.getSessionResponse(URL("$baseUrl/$verifiedTokensEndpoint"), sessionRequest)).willThrow(expectedException)

        var assertResponse = false

        val responseListener = object : Callback<SessionResponseInfo> {
            override fun onResponse(error: Exception?, response: SessionResponseInfo?) {
                assertEquals(expectedException, error)
                assertNull(response)
                assertResponse = true
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute(sessionRequestInfo).get()

        shadowOf(getMainLooper()).idle()

        assertTrue { assertResponse }
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
        ).execute(sessionRequestInfo).get()

        shadowOf(getMainLooper()).idle()

        assertTrue { assertResponse }
    }

    @Test
    fun givenNoRequestParametersPassed_ThenShouldReturnExceptionInCallback() {
        var assertResponse = false

        val responseListener = object :
            Callback<SessionResponseInfo> {
            override fun onResponse(error: Exception?, response: SessionResponseInfo?) {
                assertResponse =
                    error is AccessCheckoutException && error.message == "No request was supplied for sending"
                assertTrue("Actual error is '$error', message is '${error?.message}'") { assertResponse }
                assertNull(response)
            }
        }

        RequestDispatcher(
            "$baseUrl/$verifiedTokensEndpoint",
            responseListener,
            sessionClient
        ).execute().get()

        shadowOf(getMainLooper()).idle()

        assertTrue { assertResponse }
    }
}

package com.worldpay.access.checkout.session.api

import com.worldpay.access.checkout.api.discovery.ApiDiscoveryClient
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.discovery.DiscoveryCache
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.session.api.client.SessionClient
import com.worldpay.access.checkout.session.api.client.SessionClientFactory
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.session.api.response.SessionResponseInfo
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.kotlin.mock
import java.net.URL
import kotlin.test.assertNotNull
import kotlin.test.fail

@ExperimentalCoroutinesApi
class SessionRequestSenderTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val sessionClientFactory = mock<SessionClientFactory>()
    private val sessionRequestSender = SessionRequestSender(sessionClientFactory)

    private val baseURLAsString = "https://base.url"
    private val endpoint = URL("https://endpoint.url")

    @Before
    fun setUp() {
        // This is used to set up the behaviour of the service discovery for testing purposes
        val cacheKey =
            "${DiscoverLinks.cardSessions.endpoints[0].key},${DiscoverLinks.cardSessions.endpoints[1].key}"
        DiscoveryCache.results[cacheKey] = endpoint

        ApiDiscoveryClient.initialise(baseURLAsString)
    }

    @After
    fun tearDown() {
        DiscoveryCache.results.clear()
    }

    @Test
    fun `should obtain an instance using only a sessionClientFactory`() {
        assertNotNull(SessionRequestSender(sessionClientFactory))
    }

    @Test
    fun `should execute request given that the discovery response is valid`() = runTest {
        val expectedSessionRequest =
            CardSessionRequest(
                cardNumber = "00001111222233334444",
                cardExpiryDate = CardSessionRequest.CardExpiryDate(
                    1,
                    2020
                ),
                cvc = "123",
                identity = ""
            )

        val sessionResponse =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        "https://access.worldpay.com/sessions/<encrypted-data>"
                    ),
                    arrayOf(
                        SessionResponse.Links.Curies(
                            "https://access.worldpay.com/rels/sessions{rel}.json",
                            "sessions",
                            true
                        )
                    )
                )
            )

        val expectedResponse = SessionResponseInfo.Builder()
            .responseBody(sessionResponse)
            .sessionType(CARD)
            .build()

        val sessionRequestInfo = SessionRequestInfo.Builder()
            .requestBody(expectedSessionRequest)
            .sessionType(CARD)
            .discoverLinks(DiscoverLinks.cardSessions)
            .build()

        val sessionClient = mock<SessionClient>()

        given(sessionClientFactory.createClient(expectedSessionRequest)).willReturn(sessionClient)
        given(
            sessionClient.getSessionResponse(
                endpoint,
                sessionRequestInfo.requestBody
            )
        ).willReturn(sessionResponse)

        val sessionResponseInfo = sessionRequestSender.sendSessionRequest(sessionRequestInfo)

        assertEquals(expectedResponse.responseBody, sessionResponseInfo.responseBody)
        assertEquals(expectedResponse.sessionType, sessionResponseInfo.sessionType)
    }

    @Test
    fun `should error with exception given that the discovery response is invalid`() =
        runTest {
            val expectedSessionRequest =
                CardSessionRequest(
                    cardNumber = "00001111222233334444",
                    cardExpiryDate = CardSessionRequest.CardExpiryDate(
                        1,
                        2020
                    ),
                    cvc = "123",
                    identity = ""
                )

            val sessionRequestInfo = SessionRequestInfo.Builder()
                .requestBody(expectedSessionRequest)
                .sessionType(CARD)
                .discoverLinks(DiscoverLinks.cardSessions)
                .build()

            val sessionClient = mock<SessionClient>()

            given(sessionClientFactory.createClient(expectedSessionRequest)).willReturn(
                sessionClient
            )
            given(
                sessionClient.getSessionResponse(
                    endpoint,
                    sessionRequestInfo.requestBody
                )
            ).willThrow(RuntimeException("some message"))

            try {
                sessionRequestSender.sendSessionRequest(sessionRequestInfo)
                fail("Expected exception to be thrown but was not")
            } catch (ex: Exception) {
                assertTrue(ex is RuntimeException)
                assertEquals("some message", ex.message)
            }
        }
}

package com.worldpay.access.checkout.session.api

import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryClient
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.session.api.client.SessionClient
import com.worldpay.access.checkout.session.api.client.SessionClientFactory
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import com.worldpay.access.checkout.session.api.response.SessionResponse
import com.worldpay.access.checkout.session.api.response.SessionResponseInfo
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import java.net.URL
import kotlin.test.assertNotNull
import kotlin.test.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest as runAsBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given

@ExperimentalCoroutinesApi
class SessionRequestSenderTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val apiDiscoveryClient = mock<ApiDiscoveryClient>()
    private val sessionClientFactory = mock<SessionClientFactory>()
    private val sessionRequestSender = SessionRequestSender(sessionClientFactory, apiDiscoveryClient)

    private val baseURL = URL("https://base.url")
    private val endpoint = URL("https://endpoint.url")

    @Test
    fun `should obtain an instance using only a sessionClientFactory`() {
        assertNotNull(SessionRequestSender(sessionClientFactory))
    }

    @Test
    fun `should execute request given that the discovery response is valid`() = runAsBlockingTest {
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
                        "https://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"
                    ),
                    arrayOf(
                        SessionResponse.Links.Curies(
                            "https://access.worldpay.com/rels/verifiedTokens{rel}.json",
                            "verifiedTokens",
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
            .baseUrl(baseURL)
            .requestBody(expectedSessionRequest)
            .sessionType(CARD)
            .discoverLinks(DiscoverLinks.verifiedTokens)
            .build()

        val sessionClient = mock<SessionClient>()

        given(apiDiscoveryClient.discoverEndpoint(sessionRequestInfo.baseUrl, sessionRequestInfo.discoverLinks)).willReturn(endpoint)
        given(sessionClientFactory.createClient(expectedSessionRequest)).willReturn(sessionClient)
        given(sessionClient.getSessionResponse(endpoint, sessionRequestInfo.requestBody)).willReturn(sessionResponse)

        val sessionResponseInfo = sessionRequestSender.sendSessionRequest(sessionRequestInfo)

        assertEquals(expectedResponse.responseBody, sessionResponseInfo.responseBody)
        assertEquals(expectedResponse.sessionType, sessionResponseInfo.sessionType)
    }

    @Test
    fun `should error with exception given that the discovery response is invalid`() =
        runAsBlockingTest {
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
                .baseUrl(baseURL)
                .requestBody(expectedSessionRequest)
                .sessionType(CARD)
                .discoverLinks(DiscoverLinks.verifiedTokens)
                .build()

            val sessionClient = mock<SessionClient>()

            given(apiDiscoveryClient.discoverEndpoint(sessionRequestInfo.baseUrl, sessionRequestInfo.discoverLinks)).willReturn(endpoint)
            given(sessionClientFactory.createClient(expectedSessionRequest)).willReturn(sessionClient)
            given(sessionClient.getSessionResponse(endpoint, sessionRequestInfo.requestBody)).willThrow(RuntimeException("some message"))

            try {
                sessionRequestSender.sendSessionRequest(sessionRequestInfo)
                fail("Expected exception to be thrown but was not")
            } catch (ex: Exception) {
                assertTrue(ex is RuntimeException)
                assertEquals("some message", ex.message)
            }
        }
}

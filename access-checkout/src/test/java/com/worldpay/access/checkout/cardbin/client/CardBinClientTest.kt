package com.worldpay.access.checkout.cardbin.client

import com.worldpay.access.checkout.api.HttpsClient
import com.worldpay.access.checkout.api.URLFactory
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient.Companion.WP_API_VERSION
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient.Companion.WP_API_VERSION_VALUE
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient.Companion.WP_CALLER_ID
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient.Companion.WP_CALLER_ID_VALUE
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient.Companion.WP_CONTENT_TYPE
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient.Companion.WP_CONTENT_TYPE_VALUE
import com.worldpay.access.checkout.cardbin.api.request.CardBinRequest
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinRequestSerializer
import com.worldpay.access.checkout.cardbin.api.serialization.CardBinResponseDeserializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import java.net.URL

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CardBinClientTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()
    private val baseUrl = URL("https://some-base-url")
    private val cardBinEndpoint = "public/card/bindetails"
    private val cardBinUrl = URL("$baseUrl/$cardBinEndpoint")
    private val headers = hashMapOf(
        WP_API_VERSION to WP_API_VERSION_VALUE,
        WP_CALLER_ID to WP_CALLER_ID_VALUE,
        WP_CONTENT_TYPE to WP_CONTENT_TYPE_VALUE
    )

    private lateinit var httpsClient: HttpsClient
    private lateinit var urlFactory: URLFactory
    private lateinit var serializer: CardBinRequestSerializer
    private lateinit var deserializer: CardBinResponseDeserializer
    private lateinit var cardBinRequest: CardBinRequest

    @Before
    fun setUp() {
        httpsClient = mock(HttpsClient::class.java)
        urlFactory = mock(URLFactory::class.java)
        serializer = mock(CardBinRequestSerializer::class.java)
        deserializer = mock(CardBinResponseDeserializer::class.java)
        cardBinRequest = CardBinRequest("1111222233334444", "some-id")

        given(urlFactory.getURL("$baseUrl/$cardBinEndpoint")).willReturn(cardBinUrl)
    }

    @Test
    fun `should use default urlFactory`() = runTest {
        //Added this tests just to cover the line to initialise the URLFactory when not provided
        val client = CardBinClient(
            baseUrl = baseUrl,
            httpsClient = httpsClient,
            deserializer = deserializer,
            serializer = serializer
        )

        val actualResponse = client.getCardBinResponse(cardBinRequest)

        assertEquals(null, actualResponse)
    }

    @Test
    fun `should make expected http request and return response`() = runTest {
        val client = createCardBinClient()

        val cardBinResponse = mock(CardBinResponse::class.java)
        given(httpsClient.doPost(cardBinUrl, cardBinRequest, headers, serializer, deserializer))
            .willReturn(cardBinResponse)

        val actualResponse = client.getCardBinResponse(cardBinRequest)
        assertEquals(cardBinResponse, actualResponse)
    }


    @Test
    fun `should wrap exception in AccessCheckoutException`() = runTest {
        val client = createCardBinClient()

        given(
            httpsClient.doPost(
                eq(cardBinUrl),
                eq(cardBinRequest),
                any<HashMap<String, String>>(),
                eq(serializer),
                eq(deserializer)
            )
        ).willThrow(RuntimeException("Some error"))

        val ex = runCatching { client.getCardBinResponse(cardBinRequest) }.exceptionOrNull()

        assertTrue(ex is AccessCheckoutException)
        assertEquals("Could not perform request to card-bin API.", ex?.message)
    }

    @Test
    fun `should cancel previous job if new request is made`() = runTest {
        val mockJob = mock(Job::class.java)
        val client = createCardBinClient(mockJob)

        val response1 = mock(CardBinResponse::class.java)
        val response2 = mock(CardBinResponse::class.java)
        given(httpsClient.doPost(cardBinUrl, cardBinRequest, headers, serializer, deserializer))
            .willReturn(response1, response2)


        // Launch first request (will be cancelled)
        client.getCardBinResponse(cardBinRequest)

        // Launch second request (should complete)
        val result = client.getCardBinResponse(cardBinRequest)

        verify(mockJob).cancel() // Verify the first job was canceled
        assertEquals(response2, result)
    }

    @Test
    fun `should call cancel on currentJob to ensure previous jobs are cancelled`() = runTest {
        val mockJob = mock(Job::class.java)
        val client = createCardBinClient(mockJob)

        client.getCardBinResponse(cardBinRequest)

        verify(mockJob).cancel()
    }

    @Test
    fun `should return result on first attempt`() =
        runTest {
            val client = createCardBinClient()

            val cardBinResponse = mock(CardBinResponse::class.java)
            given(httpsClient.doPost(cardBinUrl, cardBinRequest, headers, serializer, deserializer))
                .willReturn(cardBinResponse)

            val actualResponse = client.getCardBinResponse(cardBinRequest)

            assertEquals(cardBinResponse, actualResponse)
        }

    @Test
    fun `should retry and succeed on second attempt`() =
        runTest {
            val client = createCardBinClient()

            val cardBinResponse = mock(CardBinResponse::class.java)
            given(httpsClient.doPost(cardBinUrl, cardBinRequest, headers, serializer, deserializer))
                .willThrow(RuntimeException("First attempt failed"))
                .willReturn(cardBinResponse)

            val actualResponse = client.fetchCardBinResponseWithRetry(cardBinRequest)

            assertEquals(cardBinResponse, actualResponse)
        }

    @Test
    fun `should throw exception after max attempts reached`() =
        runTest {
            val client = createCardBinClient()

            given(httpsClient.doPost(cardBinUrl, cardBinRequest, headers, serializer, deserializer))
                .willThrow(RuntimeException("Request failed"))

            val ex = runCatching {
                client.fetchCardBinResponseWithRetry(cardBinRequest, maxAttempts = 3)
            }.exceptionOrNull()

            assertTrue(ex is AccessCheckoutException)
            assertEquals("Failed after 3 attempts", ex?.message)
        }

    private fun createCardBinClient(currentJob: Job? = null): CardBinClient {
        return CardBinClient(baseUrl, urlFactory, httpsClient, deserializer, serializer, currentJob)
    }
}

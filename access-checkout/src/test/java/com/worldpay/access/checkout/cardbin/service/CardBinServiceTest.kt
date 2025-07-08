package com.worldpay.access.checkout.cardbin.service

import com.worldpay.access.checkout.BaseCoroutineTest
import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import com.worldpay.access.checkout.cardbin.api.service.CardBinService
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.DISCOVER_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class CardBinServiceTest : BaseCoroutineTest() {

    @Mock
    private lateinit var cardBinClient: CardBinClient
    private lateinit var cardBinService: CardBinService

    private val checkoutId = "testCheckoutId"
    private val baseUrl = "https://localhost"

    private val visaTestPan = "444433332222"
    private val discoverDinersTestPan = "601100040000"


    @Before
    fun setup() {
        // use the primary constructor to inject mocked dependencies
        cardBinService = CardBinService(
            checkoutId = checkoutId,
            baseUrl = baseUrl,
            client = cardBinClient,
        )
    }

    @Test
    fun `should instantiate CardBinService with default client`() {
        val service = CardBinService(
            checkoutId = "testCheckoutId",
            baseUrl = "https://localhost"
        )
        assertNotNull(service)
    }

    @Test
    fun `should instantiate CardBinService with custom client`() {
        val mockClient = mock<CardBinClient>()
        val service = CardBinService(
            checkoutId = "testCheckoutId",
            baseUrl = "https://localhost",
            client = mockClient
        )
        assertNotNull(service)
    }

    @Test
    fun `should return a distinct list of brands when card-bin-service responds with brands`() =
        runTest {
            val brand = VISA_BRAND
            val callbackResult = CompletableDeferred<List<RemoteCardBrand>>()

            whenever(cardBinClient.fetchCardBinResponseWithRetry(any())).thenReturn(
                CardBinResponse(
                    brand = listOf("visa"),
                    fundingType = "debit",
                    luhnCompliant = true
                )
            )

            cardBinService.getCardBrands(brand, visaTestPan) { result ->
                callbackResult.complete(result)
            }

            val result = callbackResult.await()
            // Verify the callback result
            assertNotNull(result)
            assertEquals(1, result.size)
            assertEquals("visa", result[0].name)
        }

    @Test
    fun `should invoke callback when card-bin-service response contains multiple brands`() =
        runTest {
            val brand = DISCOVER_BRAND
            val callbackResult = CompletableDeferred<List<RemoteCardBrand>>()


            whenever(cardBinClient.fetchCardBinResponseWithRetry(any())).thenReturn(
                CardBinResponse(
                    brand = listOf("discover", "diners"),
                    fundingType = "debit",
                    luhnCompliant = true
                )
            )


            cardBinService.getCardBrands(brand, discoverDinersTestPan) { result ->
                callbackResult.complete(result)
            }

            val result = callbackResult.await()
            assertNotNull(result)
            assertEquals(2, result.size)
            assertEquals("discover", result[0].name)
            assertEquals("diners", result[1].name)
        }


    @Test
    fun `should invoke callback when response returns no brands for pan`() = runTest {
        val callbackResult = CompletableDeferred<List<RemoteCardBrand>>()
        val brand = DISCOVER_BRAND

        whenever(cardBinClient.fetchCardBinResponseWithRetry(any())).thenReturn(
            CardBinResponse(
                brand = emptyList(),
                fundingType = "debit",
                luhnCompliant = true
            )
        )

        cardBinService.getCardBrands(brand, discoverDinersTestPan) { result ->
            callbackResult.complete(result)
        }

        val result = callbackResult.await()

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("discover", result[0].name)
    }

    @Test
    fun `should invoke callback when response returns a single brand`() = runTest {
        val callbackResult = CompletableDeferred<List<RemoteCardBrand>>()
        val brand = DISCOVER_BRAND

        whenever(cardBinClient.fetchCardBinResponseWithRetry(any())).thenReturn(
            CardBinResponse(
                brand = listOf("discover"),
                fundingType = "debit",
                luhnCompliant = true
            )
        )

        cardBinService.getCardBrands(brand, discoverDinersTestPan) { result ->
            callbackResult.complete(result)
        }

        val result = callbackResult.await()

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("discover", result[0].name)
    }

    @Test
    fun `should invoke callback when response returns a single pan but does not match global brand`() =
        runTest {
            val callbackResult = CompletableDeferred<List<RemoteCardBrand>>()
            val brand = DISCOVER_BRAND

            whenever(cardBinClient.fetchCardBinResponseWithRetry(any())).thenReturn(
                CardBinResponse(
                    brand = listOf("mastercard"),
                    fundingType = "debit",
                    luhnCompliant = true
                )
            )

            cardBinService.getCardBrands(brand, discoverDinersTestPan) { result ->
                callbackResult.complete(result)
            }

            val result = callbackResult.await()

            assertNotNull(result)
            assertEquals(2, result.size)
            assertEquals("discover", result[0].name)
            assertEquals("mastercard", result[1].name)

        }

    @Test
    fun `should return brands with default validation rules when globalBrand was null`() =
        runTest {
            val callbackResult = CompletableDeferred<List<RemoteCardBrand>>()

            whenever(cardBinClient.fetchCardBinResponseWithRetry(any())).thenReturn(
                CardBinResponse(
                    brand = listOf("discovery"),
                    fundingType = "debit",
                    luhnCompliant = true
                )
            )

            cardBinService.getCardBrands(null, discoverDinersTestPan) { result ->
                callbackResult.complete(result)
            }

            val result = callbackResult.await()
            assertNotNull(result)
            assertEquals(1, result.size)
            assertEquals("discovery", result[0].name)
        }

    @Test
    fun `should return emptyList when globalBrand was null and card-bin-service response brands was empty`() =
        runTest {
            val callbackResult = CompletableDeferred<List<RemoteCardBrand>>()
            whenever(cardBinClient.fetchCardBinResponseWithRetry(any())).thenReturn(
                CardBinResponse(
                    brand = emptyList(),
                    fundingType = "debit",
                    luhnCompliant = true
                )
            )


            cardBinService.getCardBrands(null, discoverDinersTestPan) { result ->
                callbackResult.complete(result)
            }

            val result = callbackResult.await()
            assertNotNull(result)
            assertEquals(emptyList(), result)
        }

    @Test
    fun `should swallow exception and not call callback when exception thrown by API client`() =
        runTest {
            val globalBrand = DISCOVER_BRAND
            var callbackInvoked = false // Flag to track if the callback is called

            whenever(cardBinClient.fetchCardBinResponseWithRetry(any())).thenThrow(
                RuntimeException(
                    "hello"
                )
            )

            cardBinService.getCardBrands(globalBrand, discoverDinersTestPan) { _ ->
                callbackInvoked = true // Set the flag if the callback is invoked
            }

            // making sure that we never received any card brands
            assertFalse(
                callbackInvoked,
                "Callback should not be invoked when an exception is thrown"
            )
        }

    @Test
    fun `should cancel previous job is Active and new request is made, second response completes`() =
        runTest {
            val mockClient = mock<CardBinClient>()
            val mockJob = mock<Job>()
            whenever(mockJob.isActive).thenReturn(true)
            val cardBinService = CardBinService(
                checkoutId = checkoutId,
                baseUrl = baseUrl,
                client = mockClient
            )
            cardBinService.currentJob = mockJob // Set the current job to the mocked job

            val firstLatch = CountDownLatch(1)
            val firstResponse = mock<CardBinResponse>()
            val secondResponse = CardBinResponse(listOf("visa"), "debit", true)

            // Simulate the first response hanging
            whenever(mockClient.fetchCardBinResponseWithRetry(any())).thenAnswer {
                firstLatch.await() // First response hangs
                firstResponse // Return a mock response after hanging
            }

            // Simulate the second response completing
            whenever(mockClient.fetchCardBinResponseWithRetry(any())).thenReturn(secondResponse)

            // Launch first request (will hang)
            cardBinService.getCardBrands(null, visaTestPan) {}

            // Launch second request (should cancel the first job and complete)
            val secondResponseDeferred = CompletableDeferred<List<RemoteCardBrand>>()

            cardBinService.getCardBrands(null, visaTestPan) { brands ->
                secondResponseDeferred.complete(brands)
            }

            verify(mockJob).cancel() // Verify the first job was canceled

            // Allow the second response to complete
            val result = secondResponseDeferred.await()
            assertEquals(
                listOf(
                    RemoteCardBrand(
                        name = "visa",
                        images = emptyList(),
                        cvc = CardValidationRule(matcher = "^[0-9]*$", validLengths = listOf(3, 4)),
                        pan = CardValidationRule(
                            matcher = "^[0-9]*$",
                            validLengths = listOf(12, 13, 14, 15, 16, 17, 18, 19)
                        )
                    )
                ), result
            ) // Assert the second response completed
        }

    @Test
    fun `should call cancel on active currentJob to ensure previous jobs are cancelled`() =
        runTest {
            val mockClient = mock<CardBinClient>()
            val mockJob = mock<Job>()
            whenever(mockJob.isActive).thenReturn(true)
            val cardBinService = CardBinService(
                checkoutId = checkoutId,
                baseUrl = baseUrl,
                client = mockClient
            )
            cardBinService.currentJob = mockJob // Set the current job to the mocked job

            val cardBinResponse = mock<CardBinResponse>()
            whenever(mockClient.fetchCardBinResponseWithRetry(any())).thenReturn(cardBinResponse)

            // Make a request
            cardBinService.getCardBrands(null, visaTestPan) {}

            verify(mockJob).cancel() // Verify that cancel was called on the current job
        }

    @Test
    fun `should not attempt to cancel a currentJob that is not active`() = runTest {
        val mockClient = mock<CardBinClient>()
        val mockJob = mock<Job>()
        whenever(mockJob.isActive).thenReturn(false)
        val cardBinService = CardBinService(
            checkoutId = checkoutId,
            baseUrl = baseUrl,
            client = mockClient
        )
        cardBinService.currentJob = mockJob // Set the current job to the mocked job

        val cardBinResponse = mock<CardBinResponse>()
        whenever(mockClient.fetchCardBinResponseWithRetry(any())).thenReturn(cardBinResponse)

        // Make a request
        cardBinService.getCardBrands(null, visaTestPan) {}

        verify(mockJob, times(0)).cancel() // Verify that cancel was called on the current job
    }

    @Test
    fun `should handle CancellationException gracefully`() =
        runTest {
            val globalBrand = DISCOVER_BRAND
            var callbackInvoked = false // Flag to track if the callback is called

            whenever(cardBinClient.fetchCardBinResponseWithRetry(any())).thenThrow(
                CancellationException(
                    "hello"
                )
            )

            val result = runCatching {
                cardBinService.getCardBrands(globalBrand, discoverDinersTestPan) { _ ->
                    callbackInvoked = true // Set the flag if the callback is invoked
                }
            }

            // Assert that no exception was thrown
            assertFalse(result.isFailure, "No exception should be thrown")

            // Ensure the callback was not invoked
            assertFalse(
                callbackInvoked,
                "Callback should not be invoked when an exception is thrown"
            )
        }

    @Test
    fun `should be able to retrieve currentJob`() =
        runTest {
            val mockClient = mock<CardBinClient>()
            val mockJob = mock<Job>()
            val cardBinService = CardBinService(
                checkoutId = checkoutId,
                baseUrl = baseUrl,
                client = mockClient
            )
            cardBinService.currentJob = mockJob // Set the current job to the mocked job

            assertEquals(mockJob, cardBinService.currentJob)
        }
}

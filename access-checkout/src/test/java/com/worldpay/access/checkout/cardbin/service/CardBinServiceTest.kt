package com.worldpay.access.checkout.cardbin.service

import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import com.worldpay.access.checkout.cardbin.api.service.CardBinService
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.DISCOVER_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.fail

class CardBinServiceTest : BaseCoroutineTest() {

    @Mock
    private lateinit var cardBinClient: CardBinClient
    private lateinit var cardBinService: CardBinService

    private val checkoutId = "testCheckoutId"
    private val baseUrl = "https://example.com"

    private val visaTestPan = "444433332222"
    private val discoverDinersTestPan = "601100040000"


    @Before
    fun setup() {
        // use the primary constructor to inject mocked dependencies
        cardBinService = CardBinService(
            checkoutId = checkoutId,
            baseUrl = baseUrl,
            client = cardBinClient,
//            scope = testScope
        )
        //Ensure cache is cleared before each test
        CardBinService.clearCache()
    }

    @Test
    fun `should instantiate CardBinService with default client`() {
        val service = CardBinService(
            checkoutId = "testCheckoutId",
            baseUrl = "https://example.com"
        )
        assertNotNull(service)
    }

    @Test
    fun `should instantiate CardBinService with custom client`() {
        val mockClient = mock<CardBinClient>()
        val service = CardBinService(
            checkoutId = "testCheckoutId",
            baseUrl = "https://example.com",
            client = mockClient
        )
        assertNotNull(service)
    }

    @Test
    fun `should return a list of brands when able to find brand for pan`() = runTest {
        testScope.launch {
            val brand = VISA_BRAND
            var callbackResult: List<RemoteCardBrand>? = null
            val latch = CountDownLatch(1)

            whenever(cardBinClient.getCardBinResponse(any())).thenReturn(
                CardBinResponse(
                    brand = listOf("visa"),
                    fundingType = "debit",
                    luhnCompliant = true
                )
            )

            cardBinService.getCardBrands(brand, visaTestPan) { brands ->
                callbackResult = brands
                latch.countDown()
            }

            // Wait for the callback to be invoked
            assertTrue(latch.await(2, TimeUnit.SECONDS))

            // Verify the callback result
            assertNotNull(callbackResult)
            assertEquals(1, callbackResult?.size)
            assertEquals("visa", callbackResult?.get(0)?.name)
        }
    }

    @Test
    fun `should invoke callback when response returns multiple brands for pan`() = runTest {
        val brand = DISCOVER_BRAND
        var additionalCardBrands: List<RemoteCardBrand>? = null
        val latch = CountDownLatch(1)

        whenever(cardBinClient.getCardBinResponse(any())).thenReturn(
            CardBinResponse(
                brand = listOf("discover", "diners"),
                fundingType = "debit",
                luhnCompliant = true
            )
        )

        cardBinService.getCardBrands(
            brand,
            discoverDinersTestPan
        ) { brands ->
            additionalCardBrands = brands
            latch.countDown()
        }

        // Wait for the callback to be invoked
        assertTrue(latch.await(2, TimeUnit.SECONDS))

        assertNotNull(additionalCardBrands)
        assertEquals(2, additionalCardBrands?.size)
        assertEquals("discover", additionalCardBrands?.get(0)?.name)
        assertEquals("diners", additionalCardBrands?.get(1)?.name)
    }

    @Test
    fun `should have same response for two pan numbers with same first 12 digits`() = runTest {
        val firstBrandPan = discoverDinersTestPan + "1234"
        val secondBrandPan = discoverDinersTestPan + "5678"
        val brand = DISCOVER_BRAND

        val mockResponse = CardBinResponse(
            brand = listOf("discover", "diners"),
            fundingType = "debit",
            luhnCompliant = true
        )
        whenever(cardBinClient.getCardBinResponse(any())).thenReturn(mockResponse)

        var firstCallbackResult: List<RemoteCardBrand>? = null
        var secondCallbackResult: List<RemoteCardBrand>? = null
        val firstLatch = CountDownLatch(1)
        val secondLatch = CountDownLatch(1)

        // First call: triggers API and populates cache
        cardBinService.getCardBrands(brand, firstBrandPan) { brands ->
            firstCallbackResult = brands
            firstLatch.countDown()
        }
        assertTrue(firstLatch.await(2, TimeUnit.SECONDS))

        // Second call: should return cached result immediately
        cardBinService.getCardBrands(brand, secondBrandPan) { brands ->
            secondCallbackResult = brands
            secondLatch.countDown()
        }
        assertTrue(secondLatch.await(2, TimeUnit.SECONDS))

        // Assert both results are as expected
        assertNotNull(firstCallbackResult)
        assertEquals(2, firstCallbackResult?.size)
        assertEquals("discover", firstCallbackResult?.get(0)?.name)
        assertEquals("diners", firstCallbackResult?.get(1)?.name)

        assertNotNull(secondCallbackResult)
        assertEquals(2, secondCallbackResult?.size)
        assertEquals("discover", secondCallbackResult?.get(0)?.name)
        assertEquals("diners", secondCallbackResult?.get(1)?.name)

        // API should only be called once
        verify(cardBinClient, times(1)).getCardBinResponse(any())
    }

    @Test
    fun `should have same response for two pan numbers with same first 12 digits with callbacks`() =
        runTest {
            val firstBrandPan = discoverDinersTestPan + "1234"
            val secondBrandPan = discoverDinersTestPan + "5678"
            val brand = VISA_BRAND

            var firstCallbackResult: List<RemoteCardBrand>? = null
            var secondCallbackResult: List<RemoteCardBrand>? = null
            val firstLatch = CountDownLatch(1)
            val secondLatch = CountDownLatch(1)

            val mockResponse = CardBinResponse(
                brand = listOf("discover", "diners"),
                fundingType = "debit",
                luhnCompliant = true
            )
            whenever(cardBinClient.getCardBinResponse(any())).thenReturn(mockResponse)

            // First call with callback
            cardBinService.getCardBrands(brand, firstBrandPan) { brands ->
                firstCallbackResult = brands
                firstLatch.countDown()
            }
            assertTrue(firstLatch.await(2, TimeUnit.SECONDS))

            // Second call with callback (should return cached result)
            cardBinService.getCardBrands(brand, secondBrandPan) { brands ->
                secondCallbackResult = brands
                secondLatch.countDown()
            }
            assertTrue(secondLatch.await(2, TimeUnit.SECONDS))

            // Assert both callback results
            assertNotNull(firstCallbackResult)
            assertEquals(2, firstCallbackResult?.size)
            assertEquals("discover", firstCallbackResult?.get(0)?.name)
            assertEquals("diners", firstCallbackResult?.get(1)?.name)

            assertNotNull(secondCallbackResult)
            assertEquals(2, secondCallbackResult?.size)
            assertEquals("discover", secondCallbackResult?.get(0)?.name)
            assertEquals("diners", secondCallbackResult?.get(1)?.name)

            // API should only be called once
            verify(cardBinClient, times(1)).getCardBinResponse(any())
        }

    @Test
    fun `should invoke callback when response returns no brands for pan`() = runTest {
        val brand = DISCOVER_BRAND
        var additionalCardBrands: List<RemoteCardBrand>? = null
        val latch = CountDownLatch(1)

        whenever(cardBinClient.getCardBinResponse(any())).thenReturn(
            CardBinResponse(
                brand = emptyList(),
                fundingType = "debit",
                luhnCompliant = true
            )
        )

        cardBinService.getCardBrands(
            brand,
            discoverDinersTestPan
        ) { brands ->
            additionalCardBrands = brands
            latch.countDown()
        }

        // Wait for the callback to be invoked
        assertTrue(latch.await(2, TimeUnit.SECONDS))

        assertNotNull(additionalCardBrands)
        assertEquals(1, additionalCardBrands?.size)
        assertEquals("discover", additionalCardBrands?.get(0)?.name)
    }

    @Test
    fun `should invoke callback when response returns a single pan`() = runTest {
        val brand = DISCOVER_BRAND
        var additionalCardBrands: List<RemoteCardBrand>? = null
        val latch = CountDownLatch(1)

        whenever(cardBinClient.getCardBinResponse(any())).thenReturn(
            CardBinResponse(
                brand = listOf("discover"),
                fundingType = "debit",
                luhnCompliant = true
            )
        )

        cardBinService.getCardBrands(
            brand,
            discoverDinersTestPan
        ) { brands ->
            additionalCardBrands = brands
            latch.countDown()
        }

        // Wait for the callback to be invoked
        assertTrue(latch.await(2, TimeUnit.SECONDS))

        assertNotNull(additionalCardBrands)
        assertEquals(1, additionalCardBrands?.size)
        assertEquals("discover", additionalCardBrands?.get(0)?.name)
    }

    @Test
    fun `should invoke callback when response returns a single pan but does not match global brand`() =
        runTest {
            val brand = DISCOVER_BRAND
            var additionalCardBrands: List<RemoteCardBrand>? = null
            val latch = CountDownLatch(1)

            whenever(cardBinClient.getCardBinResponse(any())).thenReturn(
                CardBinResponse(
                    brand = listOf("mastercard"),
                    fundingType = "debit",
                    luhnCompliant = true
                )
            )

            cardBinService.getCardBrands(
                brand,
                discoverDinersTestPan
            ) { brands ->
                additionalCardBrands = brands
                latch.countDown()
            }

            // Wait for the callback to be invoked
            assertTrue(latch.await(2, TimeUnit.SECONDS))

            assertNotNull(additionalCardBrands)
            assertEquals(1, additionalCardBrands?.size)
            assertEquals("mastercard", additionalCardBrands?.get(0)?.name)
        }

    @Test
    fun `should cancel the current job when there is a request in flight`() = runTest {
        val firstBrandPan = discoverDinersTestPan + "1234"
        val secondBrandPan = discoverDinersTestPan + "5678"
        val brand = DISCOVER_BRAND

        // Simulate a long-running request
        whenever(cardBinClient.getCardBinResponse(any())).thenAnswer {
            Thread.sleep(5000) // Simulate delay
            CardBinResponse(
                brand = listOf("discover", "diners"),
                fundingType = "debit",
                luhnCompliant = true
            )
        }

        // Start the first request
        cardBinService.getCardBrands(brand, firstBrandPan) {}

        val firstJob = cardBinService.currentJob

        // Send a second request while the first is still in progress
        cardBinService.getCardBrands(brand, secondBrandPan) {}

        // Verify that the first job was cancelled
        assertTrue(firstJob?.isCancelled ?: false)
    }

    @Test
    fun `should raise AccessCheckoutException when client throws RuntimeException`() =
        runTest {

            testScope.launch {
                cardBinService = CardBinService(
                    checkoutId = checkoutId,
                    baseUrl = baseUrl,
                    client = cardBinClient,
                    scope = testScope
                )
                val callback: (List<RemoteCardBrand>) -> Unit = {}
                whenever(cardBinClient.getCardBinResponse(any())).thenThrow(RuntimeException("hello"))
                try {
                    cardBinService.getCardBrands(
                        DISCOVER_BRAND,
                        discoverDinersTestPan,
                        callback
                    )

                    assertNotNull(cardBinService.currentJob) //null
                    cardBinService.currentJob?.join() // wait for child thread to complete
                } catch (exception: Exception) {
                    assertNotNull(exception)
                    assertTrue(exception is AccessCheckoutException)
                    assertEquals("Could not perform request to card-bin API.", exception!!.message)
                    assertTrue(exception.cause is RuntimeException)
                }
                fail("Exception was not caught")
            }
        }
}

package com.worldpay.access.checkout.cardbin.service

import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import com.worldpay.access.checkout.cardbin.api.service.CardBinService
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.DISCOVER_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals


@ExperimentalCoroutinesApi
@RunWith(Enclosed::class)
class CardBinServiceTest {

    // runs tests with mock card configuration to detect brands
    // use before mocked dependencies
    class WithBrandDetection() {
        @get:Rule
        var coroutinesTestRule = CoroutineTestRule()

        @Mock
        private lateinit var cardBinClient: CardBinClient
        private lateinit var cardBinService: CardBinService
        private lateinit var testDispatcher: TestCoroutineDispatcher
        private lateinit var testScope: TestCoroutineScope

        private val checkoutId = "testCheckoutId"
        private val baseUrl = "https::/changeme.com"

        private val visaTestPan = "444433332222"
        private val discoverDinersTestPan = "601100040000"


        @Before
        fun setup() {
            // initialises the fields annotated with @Mock in the current test class above
            MockitoAnnotations.openMocks(this)
            testDispatcher = TestCoroutineDispatcher()
            testScope = TestCoroutineScope(testDispatcher + SupervisorJob())

            // use the primary constructor to inject mocked dependencies
            cardBinService = CardBinService(
                checkoutId = checkoutId,
                baseUrl = baseUrl,
                client = cardBinClient,
                coroutineScope = testScope
            )
        }

        @After
        fun tearDown() {
            cardBinService.destroy()
            testScope.cleanupTestCoroutines()
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
        fun `transform should return initialCardBrand when response brand is empty`() {
            val initialBrand = VISA_BRAND
            val emptyResponse = CardBinResponse(
                brand = emptyList(),
                fundingType = "debit",
                luhnCompliant = true
            )
            val result = cardBinService.transform(initialBrand, emptyResponse)
            assertEquals(listOf(initialBrand), result)
        }

        @Test
        fun `should swallow AccessCheckoutException and not invoke callback`() = testScope.runBlockingTest {
            whenever(cardBinClient.getCardBinResponse(any())).thenThrow(AccessCheckoutException("error"))
            var callbackInvoked = false

            cardBinService.getCardBrands(VISA_BRAND, "444433332222") {
                callbackInvoked = true
            }
            testDispatcher.advanceUntilIdle()
            assertFalse(callbackInvoked)
        }



        @Test
        fun `should return initialCardBrand when pan is less than CACHE_KEY_LENGTH`() {
            val result = cardBinService.getCardBrands(VISA_BRAND, "12345678901")
            assertEquals(listOf(VISA_BRAND), result)
        }

        @Test
        fun `transform should return initialCardBrand when response has single brand matching initialCardBrand name ignoring case`() {
            val initialBrand = VISA_BRAND.copy(name = "Visa")
            val response = CardBinResponse(
                brand = listOf("vIsA"),
                fundingType = "debit",
                luhnCompliant = true
            )
            val result = cardBinService.transform(initialBrand, response)
            assertEquals(listOf(initialBrand), result)
        }

        @Test
        fun `transform should return new brand when response has single brand not matching initialCardBrand name`() {
            val initialBrand = VISA_BRAND.copy(name = "Visa")
            val response = CardBinResponse(
                brand = listOf("mastercard"),
                fundingType = "debit",
                luhnCompliant = true
            )
            val result = cardBinService.transform(initialBrand, response)
            assertEquals(1, result.size)
            assertEquals("mastercard", result[0].name)
        }

        @Test
        fun `transform should return multiple brands when response has multiple brands`() {
            val initialBrand = VISA_BRAND
            val response = CardBinResponse(
                brand = listOf("visa", "mastercard"),
                fundingType = "debit",
                luhnCompliant = true
            )
            val result = cardBinService.transform(initialBrand, response)
            assertEquals(2, result.size)
            assertTrue(result.any { it.name == "visa" })
            assertTrue(result.any { it.name == "mastercard" })
        }

        @Test
        fun `getCardBrands should call client and cache response`() = testScope.runBlockingTest {
            val initialBrand = VISA_BRAND
            val response = CardBinResponse(
                brand = listOf("visa"),
                fundingType = "debit",
                luhnCompliant = true
            )
            whenever(cardBinClient.getCardBinResponse(any())).thenReturn(response)
            val result = cardBinService.getCardBrands(initialBrand, "444433332222")
            assertEquals(listOf(initialBrand), result)
            testDispatcher.advanceUntilIdle()

            // call again to check cache
            val cachedResult = cardBinService.getCardBrands(initialBrand, "444433332222")
            assertEquals(listOf(initialBrand), cachedResult)
            verify(cardBinClient, times(1)).getCardBinResponse(any())
        }

        @Test
        fun `should return an empty list when initialCardBrand is null`() = runBlockingTest {
            val result = cardBinService.getCardBrands(null, visaTestPan)
            assertEquals(emptyList<Any>(), result)
        }

        @Test
        fun `should return an empty list when pan is less than 12 digits`() = runBlockingTest {
            val result = cardBinService.getCardBrands(null, "44443333222")
            assertEquals(emptyList<Any>(), result, result.toString())
        }

        @Test
        fun `should return a list of brands when able to find brand for pan`() =
            testScope.runBlockingTest {
                val brand = VISA_BRAND
                whenever(cardBinClient.getCardBinResponse(any())).thenReturn(
                    CardBinResponse(
                        brand = listOf("visa"),
                        fundingType = "debit",
                        luhnCompliant = true
                    )
                )
                val result = cardBinService.getCardBrands(brand, visaTestPan)

                assertEquals(1, result.count())
                assertEquals("visa", result[0].name)

                // Advance time to ensure coroutine completes
                testDispatcher.advanceUntilIdle()
            }

//        @Test
//        fun `should handle null response from client gracefully`() = testScope.runBlockingTest {
//            // Mock the client to return null
//            whenever(cardBinClient.getCardBinResponse(any())).thenReturn(null)
//
//            var callbackInvoked = false
//            var callbackResult: List<RemoteCardBrand>? = null
//
//            // This should not crash even though the client returns null
//            val result = cardBinService.getCardBrands(VISA_BRAND, visaTestPan) { brands ->
//                callbackInvoked = true
//                callbackResult = brands
//            }
//
//            // Should return initial brand immediately
//            assertEquals(listOf(VISA_BRAND), result)
//
//            // Advance coroutine
//            testDispatcher.advanceUntilIdle()
//
//            // The callback should not be invoked due to the null response causing an exception
//            assertFalse(callbackInvoked)
//            assertNull(callbackResult)
//        }

        @Test
        fun `should invoke callback when response returns multiple brands for pan`() =
            testScope.runBlockingTest {
                val brand = DISCOVER_BRAND
                var additionalCardBrands: List<RemoteCardBrand>? = null
                val latch = CountDownLatch(1)

                // mock the response for the CardBinClient
                whenever(cardBinClient.getCardBinResponse(any())).thenReturn(
                    CardBinResponse(
                        brand = listOf("discover", "diners"),
                        fundingType = "debit",
                        luhnCompliant = true
                    )
                )

                // Direct callback in method call
                val initialResult = cardBinService.getCardBrands(
                    brand,
                    discoverDinersTestPan
                ) { brands ->
                    additionalCardBrands = brands
                    latch.countDown()
                }

                // Initial result should contain just the initial brand as early return
                assertEquals("discover", initialResult[0].name)
                assertEquals(1, initialResult.size)

                // advance the coroutine dispatcher to execute the launched coroutine
                testDispatcher.advanceUntilIdle()

                // wait for the callback to be invoked
                assertTrue(latch.await(2, TimeUnit.SECONDS))

                assertNotNull(additionalCardBrands)
                assertEquals(2, additionalCardBrands?.size)
                assertEquals("discover", additionalCardBrands?.get(0)?.name)
                assertEquals("diners", additionalCardBrands?.get(1)?.name)
            }

        @Test
        fun `should have same response for two pan numbers with same first 12 digits`() =
            testScope.runBlockingTest {
                val firstBrandPan = discoverDinersTestPan + "1234"
                val secondBrandPan = discoverDinersTestPan + "5678"
                val brand = DISCOVER_BRAND

                // Mock the response from CardBinClient
                val mockResponse = CardBinResponse(
                    brand = listOf("discover", "diners"),
                    fundingType = "debit",
                    luhnCompliant = true
                )
                whenever(cardBinClient.getCardBinResponse(any())).thenReturn(mockResponse)

                // first call to the get card brands
                val firstResult = cardBinService.getCardBrands(brand, firstBrandPan)

                // initial result should only contain the initial brand
                assertEquals(1, firstResult.size)
                assertEquals("discover", firstResult[0].name)

                testDispatcher.advanceUntilIdle()

                // second call with different last 4 digits but same first 12
                val secondResult = cardBinService.getCardBrands(brand, secondBrandPan)

                // should return cached result immediately which contains multiple brands
                assertEquals(2, secondResult.size)
                assertEquals("discover", secondResult[0].name)
                assertEquals("diners", secondResult[1].name)

                // verify the API was only called once (for the first PAN)
                verify(cardBinClient, times(1)).getCardBinResponse(any())
            }

        @Test
        fun `should have same response for two pan numbers with same first 12 digits with callbacks`() =
            testScope.runBlockingTest {
                val firstBrandPan = discoverDinersTestPan + "1234"
                val secondBrandPan = discoverDinersTestPan + "5678"
                val brand = VISA_BRAND

                var firstCallbackResult: List<RemoteCardBrand>? = null
                var secondCallbackResult: List<RemoteCardBrand>? = null
                val latch = CountDownLatch(1)

                val mockResponse = CardBinResponse(
                    brand = listOf("discover", "diners"),
                    fundingType = "debit",
                    luhnCompliant = true
                )
                whenever(cardBinClient.getCardBinResponse(any())).thenReturn(mockResponse)

                // first call with callback
                cardBinService.getCardBrands(brand, firstBrandPan) { brands ->
                    firstCallbackResult = brands
                    latch.countDown()
                }

                // same as before
                testDispatcher.advanceUntilIdle()

                assertTrue(latch.await(2, TimeUnit.SECONDS))

                // second call should return immediately from callback
                val secondResult = cardBinService.getCardBrands(brand, secondBrandPan) { brands ->
                    secondCallbackResult = brands
                }

                // since it's cached, the callback won't be invoked as the cache returns synchronously
                // The result is returned directly from cache
                assertEquals(2, secondResult.size)
                assertEquals("discover", secondResult[0].name)
                assertEquals("diners", secondResult[1].name)

                // verifying that the first callback was invoked with correct brands
                assertNotNull(firstCallbackResult)
                assertEquals(2, firstCallbackResult?.size)
                assertEquals("discover", firstCallbackResult?.get(0)?.name)
                assertEquals("diners", firstCallbackResult?.get(1)?.name)

                // second callback won't be invoked since result comes from cache
                assertNull(secondCallbackResult)
                verify(cardBinClient, times(1)).getCardBinResponse(any())
            }
    }

}

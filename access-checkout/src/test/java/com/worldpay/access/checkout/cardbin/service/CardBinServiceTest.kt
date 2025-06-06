package com.worldpay.access.checkout.cardbin.service

import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import com.worldpay.access.checkout.cardbin.api.service.CardBinService
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.DISCOVER_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
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

        private val checkoutId = "testCheckoutId"
        private val baseUrl = "https::/changeme.com"

        private val visaTestPan = "444433332222"
        private val discoverDinersTestPan = "601100040000"


        @Before
        fun setup() {
            // initialises the fields annotated with @Mock in the current test class above
            MockitoAnnotations.openMocks(this)

            // use the primary constructor to inject mocked dependencies
            cardBinService = CardBinService(
                checkoutId = checkoutId,
                baseUrl = baseUrl,
                client = cardBinClient,
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
    }
}

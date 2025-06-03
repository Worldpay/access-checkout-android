package com.worldpay.access.checkout.cardbin.service

import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import com.worldpay.access.checkout.cardbin.api.service.CardBinService
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.mockSuccessfulCardConfiguration
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import java.net.URL
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals


@ExperimentalCoroutinesApi
@RunWith(Enclosed::class)
class CardBinServiceTest {


    // Runs tests with mock card configuration to detect brands
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
        private val baseUrl = URL("https::/changeme.com")

        private val testPan = "444433332222"

        @Before
        fun setup(){
            testDispatcher = TestCoroutineDispatcher()
            testScope =  TestCoroutineScope(testDispatcher + SupervisorJob())
            cardBinClient = CardBinClient(baseUrl)
            cardBinService = CardBinService(checkoutId, baseUrl, cardBinClient, testScope)
        }

        @After
        fun tearDown() {
            cardBinService.destroy()
            testScope.cleanupTestCoroutines()
        }

        @Test
        fun `should return an empty list when initialCardBrand is null`() = runBlockingTest {
            val result = cardBinService.getCardBrands(null, testPan)
            assertEquals(emptyList<Any>(), result)
        }

        @Test
        fun `should return an empty list when pan is less than 12 digits`() = runBlockingTest {
            val result = cardBinService.getCardBrands(null, "44443333222")
            assertEquals(emptyList<Any>(), result, result.toString())
        }

        @Test
        fun `should return a list of brands when able to find brand for pan`() = runBlockingTest {
            val brand = VISA_BRAND
            val result = cardBinService.getCardBrands(brand, testPan)

            assertTrue(brand in result)
            assertEquals(1, result.count())
            assertEquals("visa", result[0].name)
        }

        @Test
        fun `should invoke callback when API returns multiple brands for pan`() = runBlockingTest {
            val multipleBrandPan = "415058099651"
            val brand = VISA_BRAND
            var additionalCardBrands: List<RemoteCardBrand>? = null
            val latch = CountDownLatch(1)

            whenever(cardBinClient.getCardBinResponse(Mockito.any())).thenReturn(
                CardBinResponse(brand = listOf("visa", "mastercard"), fundingType = "debit", luhnCompliant = true)
            )

            cardBinService.setOnAdditionalBrandsReceived { brands ->
                additionalCardBrands = brands
                latch.countDown()
            }

            val initialResult = cardBinService.getCardBrands(brand, multipleBrandPan)

            assertEquals("visa", initialResult[0].name)

            testDispatcher.advanceUntilIdle()

            println(additionalCardBrands)

            assertTrue(latch.await(2, TimeUnit.SECONDS))

            assertNotNull(additionalCardBrands)
        }

    }

    // Runs tests without mock card configuration
    class WithoutBrandDetection() {
        private val cardBinService = CardBinService(
            checkoutId = "testCheckoutId", baseUrl = URL("https://changeme.com")
        )

        //wrap the test inside a coroutine as getCardBrands is a suspend function

        @Test
        fun `should return an empty list when card brand is null`() = runBlockingTest {
            val brand = null
            val expected = emptyList<RemoteCardBrand>()
            val result = cardBinService.getCardBrands(brand, "1234123412341234")

            assertEquals(expected, result)
        }
    }
}

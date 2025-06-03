package com.worldpay.access.checkout.cardbin.service

import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.cardbin.api.client.CardBinClient
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.net.URL
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

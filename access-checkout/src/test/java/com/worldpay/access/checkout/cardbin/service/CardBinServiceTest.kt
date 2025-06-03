package com.worldpay.access.checkout.cardbin.service

import com.worldpay.access.checkout.cardbin.api.service.CardBinService
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.mockSuccessfulCardConfiguration
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import kotlin.test.assertEquals


@RunWith(Enclosed::class)
@ExperimentalCoroutinesApi
class CardBinServiceTest {

    // Runs tests without mock card configuration
    class WithoutBrandDetection() {
        private val cardBinService = CardBinService(
            checkoutId = "validCheckoutId"
        )

        //wrap the test inside a coroutine as getCardBrands is a suspend function

        @Test
        fun `should return an list with a single brand when unable to find brand for pan`() = runBlockingTest {
            val brand = VISA_BRAND
            val expected = listOf(brand)
            val result = cardBinService.getCardBrands(brand, "1234123412341234")

            assertEquals(expected, result)
        }
    }

    // Runs tests with mock card configuration to detect brands
    class WithBrandDetection() {
        @get:Rule
        var coroutinesTestRule = CoroutineTestRule()

        private val cardBinService = CardBinService(
            checkoutId = "validCheckoutId"
        )
        private val testPan = "4444333322221111"

        @Before
        fun setup() = runBlockingTest {
            mockSuccessfulCardConfiguration()
        }

        @Test
        fun `should return an empty list when brand is null`() = runBlockingTest {
            val result = cardBinService.getCardBrands(null, testPan)

            assertEquals(result, emptyList<Any>(), result.toString())
        }

        @Test
        fun `should return a list of brands when able to find brand for pan`() = runBlockingTest {
            val brand = VISA_BRAND
            val result = cardBinService.getCardBrands(brand, testPan)

            assertTrue(brand in result)
            assertEquals(2, result.count())
        }
    }
}

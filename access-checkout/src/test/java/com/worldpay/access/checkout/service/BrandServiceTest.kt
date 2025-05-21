package com.worldpay.access.checkout.service

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
class BrandServiceTest {

    // Runs tests without mock card configuration
    class WithoutBrandDetection() {
        private val brandService = BrandService()

        @Test
        fun `should return an array with a single brand when pan is required length but unable to find brand`() {
            val brand = VISA_BRAND
            val expected = listOf(brand)
            val result = brandService.getCardBrands(brand, "1234123412341234")

            assertEquals(result, expected)
        }
    }

    // Runs tests with mock card configuration to detect brands
    class WithBrandDetection() {
        @get:Rule
        var coroutinesTestRule = CoroutineTestRule()

        private val brandService = BrandService()
        private val testPan = "4444333322221111"

        @Before
        fun setup() = runBlockingTest {
            mockSuccessfulCardConfiguration()
        }

        @Test
        fun `should return an empty array when brand is null`() {
            val result = brandService.getCardBrands(null, testPan)

            assertEquals(result, emptyList())
        }

        @Test
        fun `should return an array with a single brand when pan is below required length`() {
            val panBelowRequiredLength = "44443333222"
            val brand = VISA_BRAND
            val expected = listOf(brand)
            val result = brandService.getCardBrands(brand, panBelowRequiredLength)

            assertEquals(result, expected)
        }

        @Test
        fun `should return a array of brands when pan is required length`() {
            val brand = VISA_BRAND
            val result = brandService.getCardBrands(brand, testPan)

            assertTrue(brand in result)
            assertEquals(2, result.count())
        }
    }
}
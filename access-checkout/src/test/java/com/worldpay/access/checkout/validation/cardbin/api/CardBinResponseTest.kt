package com.worldpay.access.checkout.validation.cardbin.api

import org.junit.Test
import kotlin.test.assertEquals

class CardBinResponseTest {

    @Test
    fun `should be able to set and get the brand, fundingType and luhnCompliant for a card bin response`() {
        val brand = listOf("brand1", "brand2")
        val fundingType = "some-funding-type"
        val luhnCompliant = false

        val cardBinResponse =
            CardBinResponse(
                brand,
                fundingType,
                luhnCompliant
            )

        assertEquals(brand, cardBinResponse.brand)
        assertEquals(fundingType, cardBinResponse.fundingType)
        assertEquals(luhnCompliant, cardBinResponse.luhnCompliant)
    }
}

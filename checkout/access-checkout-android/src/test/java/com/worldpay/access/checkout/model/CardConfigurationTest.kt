package com.worldpay.access.checkout.model

import org.junit.Assert.*
import org.junit.Test

class CardConfigurationTest {

    @Test
    fun shouldBuildEmptyCardConfiguration() {
        val emptyCardConfiguration = CardConfiguration.empty()

        assertTrue(emptyCardConfiguration.isEmpty())
    }

    @Test
    fun shouldBuildNonEmptyCardConfiguration() {
        val cardConfiguration = CardConfiguration(listOf(CardBrand("test", "test", null, emptyList())), CardDefaults(null, null, null, null))

        assertFalse(cardConfiguration.isEmpty())
    }
}
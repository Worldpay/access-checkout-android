package com.worldpay.access.checkout.validation.configuration

import com.worldpay.access.checkout.BaseCoroutineTest
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CardConfigurationProviderTest : BaseCoroutineTest() {

    private val cardConfigurationClient = mock<CardConfigurationClient>()

    @Before
    fun setup() = runTest {
        // Reset to default configuration before each test
        CardConfigurationProvider.savedCardConfiguration = CARD_CONFIG_NO_BRAND
    }

    @Test
    fun `should return default card configuration initially`() {
        assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())
    }

    @Test
    fun `should fetch and update card configuration successfully`() = runTest {
        // Mock successful card configuration fetch
        given(cardConfigurationClient.getCardConfiguration()).willReturn(CARD_CONFIG_BASIC)

        CardConfigurationProvider.initialise(cardConfigurationClient, emptyList())
        advanceUntilIdle()

        // Verify the configuration is updated to the expected value
        assertEquals(CARD_CONFIG_BASIC, CardConfigurationProvider.getCardConfiguration())
    }

    @Test
    fun `should reset to default card configuration when reinitialized`() = runTest {
        // Mock successful card configuration fetch
        given(cardConfigurationClient.getCardConfiguration()).willReturn(CARD_CONFIG_BASIC)

        CardConfigurationProvider.initialise(cardConfigurationClient, emptyList())
        advanceUntilIdle()

        assertEquals(CARD_CONFIG_BASIC, CardConfigurationProvider.getCardConfiguration())

        // Reinitialize and verify reset to default
        CardConfigurationProvider.initialise(cardConfigurationClient, emptyList())

        assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())
    }

    @Test
    fun `should notify observers with updated card configuration`() = runTest {
        val observer = mock<CardConfigurationObserver>()
        given(cardConfigurationClient.getCardConfiguration()).willReturn(CARD_CONFIG_BASIC)

        CardConfigurationProvider.initialise(cardConfigurationClient, listOf(observer))
        advanceUntilIdle()

        verify(observer, timeout(500)).update()
        assertEquals(CARD_CONFIG_BASIC, CardConfigurationProvider.getCardConfiguration())
    }

    @Test
    fun `should notify observers with default card configuration on failure`() = runTest {
        val observer = mock<CardConfigurationObserver>()
        given(cardConfigurationClient.getCardConfiguration()).willThrow(RuntimeException())

        CardConfigurationProvider.initialise(cardConfigurationClient, listOf(observer))
        advanceUntilIdle()

        verify(observer, timeout(500)).update()
        assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())
    }

    @Test
    fun `should handle multiple observers`() = runTest {
        val observer1 = mock<CardConfigurationObserver>()
        val observer2 = mock<CardConfigurationObserver>()
        given(cardConfigurationClient.getCardConfiguration()).willReturn(CARD_CONFIG_BASIC)

        CardConfigurationProvider.initialise(cardConfigurationClient, listOf(observer1, observer2))
        advanceUntilIdle()

        verify(observer1, timeout(500)).update()
        verify(observer2, timeout(500)).update()
        assertEquals(CARD_CONFIG_BASIC, CardConfigurationProvider.getCardConfiguration())
    }

    @Test
    fun `should handle empty observer list`() = runTest {
        given(cardConfigurationClient.getCardConfiguration()).willReturn(CARD_CONFIG_BASIC)

        CardConfigurationProvider.initialise(cardConfigurationClient, emptyList())
        advanceUntilIdle()

        assertEquals(CARD_CONFIG_BASIC, CardConfigurationProvider.getCardConfiguration())
    }

    @Test
    fun `should log error and fallback to default configuration on exception`() = runTest {
        given(cardConfigurationClient.getCardConfiguration()).willThrow(RuntimeException("Test exception"))

        CardConfigurationProvider.initialise(cardConfigurationClient, emptyList())
        advanceUntilIdle()

        assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())
    }

    @Test
    fun `should handle exceptions in observer update gracefully`() = runTest {
        val failingObserver = mock<CardConfigurationObserver> {
            on { update() }.thenThrow(RuntimeException("Observer update failed"))
        }
        val successfulObserver = mock<CardConfigurationObserver>()

        given(cardConfigurationClient.getCardConfiguration()).willReturn(CARD_CONFIG_BASIC)

        CardConfigurationProvider.initialise(
            cardConfigurationClient,
            listOf(failingObserver, successfulObserver)
        )
        advanceUntilIdle()

        verify(failingObserver).update()
        verify(successfulObserver).update()
        assertEquals(CARD_CONFIG_BASIC, CardConfigurationProvider.getCardConfiguration())
    }

}
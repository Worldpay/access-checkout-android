package com.worldpay.access.checkout.validation.configuration

import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.mockSuccessfulCardConfiguration
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import java.lang.RuntimeException
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class CardConfigurationProviderTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val cardConfigurationClient = mock<CardConfigurationClient>()

    private lateinit var cardConfigurationProvider: CardConfigurationProvider

    @Before
    fun setup() = runTest {
        given(cardConfigurationClient.getCardConfiguration()).willThrow(RuntimeException())
        cardConfigurationProvider = CardConfigurationProvider(
            cardConfigurationClient = cardConfigurationClient,
            observers = emptyList()
        )
    }

    @Test
    fun `should be getting default card configuration initially`() {
        assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())
    }

    @Test
    fun `should reset back to default card configuration each time a provider is created despite having remote card configuration before`() =
        runTest {
            assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())

            mockSuccessfulCardConfiguration()

            assertEquals(CARD_CONFIG_BASIC, CardConfigurationProvider.getCardConfiguration())

            CardConfigurationProvider(
                cardConfigurationClient = cardConfigurationClient,
                observers = emptyList()
            )

            assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())
        }

    @Test
    fun `should be getting remote card configuration once loaded`() = runTest {
        assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())

        val cardConfigurationClient = mock<CardConfigurationClient>()

        given(cardConfigurationClient.getCardConfiguration()).willReturn(CARD_CONFIG_BASIC)

        CardConfigurationProvider(cardConfigurationClient, emptyList())

        assertEquals(CARD_CONFIG_BASIC, CardConfigurationProvider.getCardConfiguration())
    }

    @Test
    fun `should be calling all observers once remote card configuration is retrieved`() = runTest {
        assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())

        val cardConfigObserver = mock<CardConfigurationObserver>()
        val cardConfigurationClient = mock<CardConfigurationClient>()

        given(cardConfigurationClient.getCardConfiguration()).willReturn(CARD_CONFIG_BASIC)

        CardConfigurationProvider(cardConfigurationClient, listOf(cardConfigObserver))

        assertEquals(CARD_CONFIG_BASIC, CardConfigurationProvider.getCardConfiguration())
        verify(cardConfigObserver).update()
    }

    @Test
    fun `should do nothing when remote card configuration call fails`() = runTest {
        assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())

        val cardConfigObserver = mock<CardConfigurationObserver>()
        val cardConfigurationClient = mock<CardConfigurationClient>()

        given(cardConfigurationClient.getCardConfiguration()).willThrow(RuntimeException())

        CardConfigurationProvider(cardConfigurationClient, listOf(cardConfigObserver))

        assertEquals(CARD_CONFIG_NO_BRAND, CardConfigurationProvider.getCardConfiguration())
        verify(cardConfigObserver, never()).update()
    }
}

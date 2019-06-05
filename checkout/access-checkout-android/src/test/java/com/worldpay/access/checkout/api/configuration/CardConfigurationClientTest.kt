package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.testutils.mock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import java.net.URL

class CardConfigurationClientTest {

    private lateinit var cardConfigurationClient: CardConfigurationClient
    private lateinit var httpClient: HttpClient
    private lateinit var deserializer: Deserializer<CardConfiguration>

    @Before
    fun setup() {
        httpClient = mock()
        deserializer = mock()
        cardConfigurationClient = CardConfigurationClient(httpClient, deserializer)
    }

    @Test
    fun givenARequestForCardConfiguration_ThenShouldBuildCardConfiguration() {
        val cardConfiguration = CardConfiguration()
        given(httpClient.doGet(URL("http://localhost/access-checkout/cardConfiguration.json"), deserializer)).willReturn(
            cardConfiguration
        )

        val actualCardConfiguration = cardConfigurationClient.getCardConfiguration("http://localhost")

        assertEquals(cardConfiguration, actualCardConfiguration)
    }
}
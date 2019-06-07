package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.testutils.mock
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.internal.util.reflection.FieldSetter
import org.mockito.internal.util.reflection.FieldSetter.setField

class CardConfigurationClientTest {

    private lateinit var httpClient: HttpClient
    private lateinit var deserializer: Deserializer<CardConfiguration>

    @Before
    fun setup() {
        httpClient = mock()
        deserializer = mock()
    }

    @Test
    fun givenARequestForCardConfiguration_ThenShouldBuildCardConfiguration() {
        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
            }
        }

        val cardConfigurationClient = CardConfigurationClient(callback)
        val cardConfigurationAsyncTask = mock<CardConfigurationAsyncTask>()
        setField(cardConfigurationClient, cardConfigurationClient::class.java.getDeclaredField("cardConfigurationAsyncTask"), cardConfigurationAsyncTask)

        val baseURL = "http://localhost"
        cardConfigurationClient.getCardConfiguration(baseURL)

        verify(cardConfigurationAsyncTask).execute(baseURL)
    }
}
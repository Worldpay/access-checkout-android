package com.worldpay.access.checkout.api.configuration

import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.Callback
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify

class CardConfigurationClientTest {

    private lateinit var cardConfigurationAsyncTaskFactory: CardConfigurationAsyncTaskFactory
    private lateinit var cardConfigurationClient: CardConfigurationClient

    @Before
    fun setup() {
        cardConfigurationAsyncTaskFactory = mock()
        cardConfigurationClient = CardConfigurationClient(cardConfigurationAsyncTaskFactory)
    }

    @Test
    fun givenARequestForCardConfiguration_ThenShouldBuildCardConfiguration() {
        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
            }
        }

        val asyncTask = mock<CardConfigurationAsyncTask>()
        given(cardConfigurationAsyncTaskFactory.getAsyncTask(callback)).willReturn(asyncTask)

        val baseURL = "http://localhost"
        cardConfigurationClient.getCardConfiguration(baseURL, callback)

        verify(asyncTask).execute(baseURL)
    }
}

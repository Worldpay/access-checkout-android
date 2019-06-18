package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.testutils.mock
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.mockito.internal.util.reflection.FieldSetter
import org.mockito.internal.util.reflection.FieldSetter.setField

class CardConfigurationClientImplTest {

    private lateinit var cardConfigurationAsyncTaskFactory: CardConfigurationAsyncTaskFactory
    private lateinit var cardConfigurationClientImpl: CardConfigurationClientImpl

    @Before
    fun setup() {
        cardConfigurationAsyncTaskFactory = mock()
        cardConfigurationClientImpl = CardConfigurationClientImpl(cardConfigurationAsyncTaskFactory)
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
        cardConfigurationClientImpl.getCardConfiguration(baseURL, callback)

        verify(asyncTask).execute(baseURL)
    }
}
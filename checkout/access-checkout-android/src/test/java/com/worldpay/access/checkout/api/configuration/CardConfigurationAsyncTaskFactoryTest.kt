package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.model.CardConfiguration
import org.junit.Assert.*
import org.junit.Test

class CardConfigurationAsyncTaskFactoryTest {

    @Test
    fun shouldReturnInstanceOfAsyncTask() {
        val asyncTask = CardConfigurationAsyncTaskFactory().getAsyncTask(object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
            }
        })

        assertNotNull(asyncTask)
    }
}
package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.api.AccessCheckoutException
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.discovery.AccessCheckoutDiscoveryAsyncTask
import com.worldpay.access.checkout.model.CardConfiguration
import org.awaitility.Awaitility
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CardConfigurationAsyncTaskTest {

    @Test
    fun givenBadBaseURL_ThenShouldThrowAnException() {
        var asserted = false

        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                assertNotNull(error)
                assertTrue(error is AccessCheckoutException.AccessCheckoutConfigurationException)
                asserted = true
            }
        }

        val cardConfigurationAsyncTask = CardConfigurationAsyncTask(callback)

        cardConfigurationAsyncTask.execute("abcd")

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until { asserted }
    }
}
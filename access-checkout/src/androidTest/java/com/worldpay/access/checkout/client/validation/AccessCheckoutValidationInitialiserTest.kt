package com.worldpay.access.checkout.client.validation

import android.content.Context
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.worldpay.access.checkout.api.ApiDiscoveryStubs.stubServiceDiscoveryResponses
import com.worldpay.access.checkout.api.MockServer.startWiremock
import com.worldpay.access.checkout.api.discovery.DiscoveryCache
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.awaitility.Awaitility.await
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit.SECONDS

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AccessCheckoutValidationInitialiserTest {
    companion object {
        val timeout = 5L

        private class TestValidationListener : AccessCheckoutCardValidationListener {
            override fun onValidationSuccess() {}

            override fun onCvcValidated(isValid: Boolean) {}

            override fun onPanValidated(isValid: Boolean) {}

            override fun onExpiryDateValidated(isValid: Boolean) {}

            override fun onBrandsChange(cardBrands: List<CardBrand>) {}
        }
    }

    private val applicationContext: Context = getInstrumentation().context.applicationContext
    private val wireMockPort = 8443
    private val baseUrl = "https://localhost:${wireMockPort}"
    private val checkoutId = "checkout id"

    @Before
    fun setUp() {
        DiscoveryCache.results.clear()
        startWiremock(applicationContext, wireMockPort)
        stubServiceDiscoveryResponses()
    }

    @Test
    fun shouldDiscoverCardSessionsEndPointWhenInitialisingCardValidation() {
        val expectedKeyInDiscoveryCache = "service:sessions,sessions:card"

        val config = CardValidationConfig.Builder().baseUrl(baseUrl)
            .pan(AccessCheckoutEditText(applicationContext))
            .expiryDate(AccessCheckoutEditText(applicationContext))
            .cvc(AccessCheckoutEditText(applicationContext))
            .validationListener(TestValidationListener()).lifecycleOwner(TestLifecycleOwner())
            .checkoutId(checkoutId).build()

        AccessCheckoutValidationInitialiser.initialise(config)

        await().atMost(timeout, SECONDS).until {
            DiscoveryCache.results.size == 1
                    && DiscoveryCache.results.containsKey(expectedKeyInDiscoveryCache)
        }
    }
}

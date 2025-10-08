package com.worldpay.access.checkout.client.validation

import android.content.Context
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.tomakehurst.wiremock.client.WireMock
import com.worldpay.access.checkout.api.MockServer.startWiremock
import com.worldpay.access.checkout.api.MockServer.stopWiremock
import com.worldpay.access.checkout.api.discovery.DiscoveryCache
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import com.worldpay.access.checkout.validation.filters.CvcLengthFilter
import com.worldpay.access.checkout.validation.filters.ExpiryDateLengthFilter
import com.worldpay.access.checkout.validation.filters.PanNumericFilter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.awaitility.Awaitility.await
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AccessCheckoutValidationInitialiserIntegrationTest {
    companion object {
        const val timeout = 5L

        private class TestValidationListener : AccessCheckoutCardValidationListener {
            override fun onValidationSuccess() {}

            override fun onCvcValidated(isValid: Boolean) {}

            override fun onPanValidated(isValid: Boolean) {}

            override fun onExpiryDateValidated(isValid: Boolean) {}

            override fun onBrandsChange(cardBrands: List<CardBrand>) {}
        }
    }

    private val applicationContext: Context = getInstrumentation().context.applicationContext
    private val pan = AccessCheckoutEditText(applicationContext)
    private val expiryDate = AccessCheckoutEditText(applicationContext)
    private val cvc = AccessCheckoutEditText(applicationContext)

    private val wireMockPort = 8443
    private val baseUrl = "https://localhost:${wireMockPort}"
    private val checkoutId = "checkout id"

    @Before
    fun setUp() {
        DiscoveryCache.results.clear()
        DiscoveryCache.responses.clear()
        startWiremock(applicationContext, wireMockPort)
    }

    @After
    fun tearDown() {
        stopWiremock()
    }

    @Test
    fun shouldDiscover_CardSessions_CvcSessions_CardBinDetails_endPointsWhenInitialisingCardValidation() {
        val expectedKey1InDiscoveryCache = "service:sessions,sessions:card"
        val expectedKey2InDiscoveryCache = "service:sessions,sessions:paymentsCvc"
        val expectedKey3InDiscoveryCache = "cardBinPublic:binDetails"

        AccessCheckoutValidationInitialiser.initialise(checkoutId, baseUrl, cardValidationConfig())

        await().atMost(timeout, SECONDS).until {
            DiscoveryCache.results.size == 3
                    && DiscoveryCache.results.containsKey(expectedKey1InDiscoveryCache)
                    && DiscoveryCache.results.containsKey(expectedKey2InDiscoveryCache)
                    && DiscoveryCache.results.containsKey(expectedKey3InDiscoveryCache)
        }
    }

    @Test
    fun shouldSuccessfullyInitialiseCardValidationWhenFailingToDiscoverServices() {
        // we do not stub the service discovery responses to simulate a failure to discover services
        WireMock.removeAllMappings()

        AccessCheckoutValidationInitialiser.initialise(checkoutId, baseUrl, cardValidationConfig())

        await()
            // waits for 1 second (at which point the service discovery has already
            // raised and swallowed an exception because it could not discover services)
            .pollDelay(1, SECONDS)
            .until {
                assertTrue(pan.filters.any { it is PanNumericFilter })
                assertTrue(expiryDate.filters.any { it is ExpiryDateLengthFilter })
                assertTrue(cvc.filters.any { it is CvcLengthFilter })
                true
            }
    }

    private fun cardValidationConfig(): CardValidationConfig {
        return CardValidationConfig.Builder()
            .pan(pan)
            .expiryDate(expiryDate)
            .cvc(cvc)
            .validationListener(TestValidationListener()).lifecycleOwner(TestLifecycleOwner())
            .build()
    }
}

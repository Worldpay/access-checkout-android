package com.worldpay.access.checkout.api

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.stubbing.Scenario
import com.worldpay.access.checkout.api.ApiDiscoveryStubs.rootResponseMapping
import com.worldpay.access.checkout.api.ApiDiscoveryStubs.stubServiceDiscoveryResponses
import com.worldpay.access.checkout.api.ApiDiscoveryStubs.verifiedTokensMapping
import com.worldpay.access.checkout.api.MockServer.getBaseUrl
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryClient
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.discovery.DiscoveryCache
import com.worldpay.access.checkout.api.discovery.EndpointDiscoveryClientFactory
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.awaitility.Awaitility.await
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ApiDiscoveryIntegrationTest {
// TODO: US707277 - fix this class
    private val applicationContext = InstrumentationRegistry.getInstrumentation().context.applicationContext

    @Before
    fun setUp() {
        DiscoveryCache.results.clear()
        MockServer.startWiremock(applicationContext, 8443)
    }

    @After
    fun tearDown() {
        MockServer.stopWiremock()
    }

    @Test
    fun shouldBeAbleToDiscoverVTSessionsFromRoot() {
        stubServiceDiscoveryResponses()

        var url: String? = null

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                url = response
            }
        }

        val client = ApiDiscoveryClient(EndpointDiscoveryClientFactory())

        client.discoverEndpoint(getBaseUrl(), callback, DiscoverLinks.verifiedTokens)

        await().atMost(5, TimeUnit.SECONDS).until {
            Log.d("AccessCheckoutDiscoveryIntegrationTest", "Discovered endpoint: $url")
            url != null && url.equals("${getBaseUrl()}/verifiedTokens/sessions")
        }
    }

    @Test
    fun shouldReturnException_whenDiscoveryFails() {
        stubFor(
            get("/")
                .willReturn(
                    aResponse()
                        .withStatus(500)
                )
        )

        var assertionDone = false
        val exceptedException = AccessCheckoutException("Error message was: Server Error")

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertEquals(exceptedException, error)
                assertNull(response)
                assertionDone = true
            }
        }

        val client = ApiDiscoveryClient(EndpointDiscoveryClientFactory())

        client.discoverEndpoint(getBaseUrl(), callback, DiscoverLinks.verifiedTokens)

        await().atMost(5, TimeUnit.SECONDS).until { assertionDone }
    }

    @Test
    fun shouldReturnUrl_whenFirstDiscoveryAttemptFailsThenRetrySucceeds() {
        val serviceAvailableState = "SERVICE_AVAILABLE_AGAIN"

        stubFor(
            get("/")
                .inScenario("service re-discovery")
                .whenScenarioStateIs(Scenario.STARTED)
                .willSetStateTo(serviceAvailableState)
                .willReturn(aResponse().withStatus(500))
        )

        stubFor(
            rootResponseMapping()
                .inScenario("service re-discovery")
                .whenScenarioStateIs(serviceAvailableState)
        )

        stubFor(verifiedTokensMapping())

        var exception: Exception? = null
        var url: String? = null
        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                exception = error
                url = response
            }
        }

        val client = ApiDiscoveryClient(EndpointDiscoveryClientFactory())
        client.discoverEndpoint(getBaseUrl(), callback, DiscoverLinks.verifiedTokens)

        await().atMost(5, TimeUnit.SECONDS).until {
            url != null && url.equals("${getBaseUrl()}/verifiedTokens/sessions") && exception == null
        }
    }
}

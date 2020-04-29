package com.worldpay.access.checkout.api

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.stubbing.Scenario
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDiscoveryException
import com.worldpay.access.checkout.api.ApiDiscoveryStubs.rootResponseMapping
import com.worldpay.access.checkout.api.ApiDiscoveryStubs.stubServiceDiscoveryResponses
import com.worldpay.access.checkout.api.ApiDiscoveryStubs.verifiedTokensMapping
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryAsyncTaskFactory
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryClient
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.discovery.DiscoveryCache
import org.awaitility.Awaitility.await
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ApiDiscoveryIntegrationTest {

    @get:Rule
    var wireMockRule = WireMockRule(
        WireMockConfiguration
            .options()
            .port(8090)
            .extensions(ResponseTemplateTransformer(false))
            .notifier(ConsoleNotifier(true))
    )

    @Before
    fun setUp() {
        DiscoveryCache.results.clear()
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

        val client = ApiDiscoveryClient(ApiDiscoveryAsyncTaskFactory())

        client.discover(wireMockRule.baseUrl(), callback, DiscoverLinks.verifiedTokens)

        await().atMost(5, TimeUnit.SECONDS).until {
            Log.d("AccessCheckoutDiscoveryIntegrationTest", "Discovered endpoint: $url")
            url != null && url.equals("${wireMockRule.baseUrl()}/verifiedTokens/sessions")
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

        var exception: Exception? = null

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                exception = error
            }
        }

        val client = ApiDiscoveryClient(ApiDiscoveryAsyncTaskFactory())

        client.discover(wireMockRule.baseUrl(), callback, DiscoverLinks.verifiedTokens)

        await().atMost(5, TimeUnit.SECONDS).until {
            Log.d("AccessCheckoutDiscoveryIntegrationTest", "Error received: $exception")
            exception is AccessCheckoutDiscoveryException &&
                    exception?.message == "An error was thrown when trying to make a connection to the service"
        }
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

        val client = ApiDiscoveryClient(ApiDiscoveryAsyncTaskFactory())
        client.discover(wireMockRule.baseUrl(), callback, DiscoverLinks.verifiedTokens)

        await().atMost(5, TimeUnit.SECONDS).until {
            url != null && url.equals("${wireMockRule.baseUrl()}/verifiedTokens/sessions") && exception == null
        }
    }
}
package com.worldpay.access.checkout.api

import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.stubbing.Scenario
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDiscoveryException
import com.worldpay.access.checkout.api.DiscoveryStubs.rootResponseMapping
import com.worldpay.access.checkout.api.DiscoveryStubs.stubServiceDiscoveryResponses
import com.worldpay.access.checkout.api.DiscoveryStubs.verifiedTokensMapping
import com.worldpay.access.checkout.api.discovery.AccessCheckoutDiscoveryAsyncTaskFactory
import com.worldpay.access.checkout.api.discovery.AccessCheckoutDiscoveryClient
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import org.awaitility.Awaitility.await
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class AccessCheckoutDiscoveryIntegrationTest {

    @get:Rule
    var wireMockRule = WireMockRule(
        WireMockConfiguration
            .options()
            .port(8090)
            .extensions(ResponseTemplateTransformer(false))
            .notifier(ConsoleNotifier(true))
    )

    @Test
    fun givenRootResourceURL_ThenServiceDiscoveryCanDiscoverSessionsResourceEndpoint() {
        stubServiceDiscoveryResponses()

        var url: String? = null

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                url = response
            }
        }

        val client = AccessCheckoutDiscoveryClient(AccessCheckoutDiscoveryAsyncTaskFactory())

        client.discover(wireMockRule.baseUrl(), callback, DiscoverLinks.verifiedTokens)

        await().atMost(5, TimeUnit.SECONDS).until {
            Log.d("AccessCheckoutDiscoveryIntegrationTest", "Discovered endpoint: $url")
            url != null && url.equals("${wireMockRule.baseUrl()}/verifiedTokens/sessions")
        }
    }

    @Test
    fun givenAnErrorFetchingUrl_ThenExceptionIsPassedBackToCallback() {
        stubFor(
            WireMock.get("/")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(500)
                )
        )

        var exception: Exception? = null

        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                exception = error
            }
        }

        val client = AccessCheckoutDiscoveryClient(AccessCheckoutDiscoveryAsyncTaskFactory())

        client.discover(wireMockRule.baseUrl(), callback, DiscoverLinks.verifiedTokens)

        await().atMost(5, TimeUnit.SECONDS).until {
            Log.d("AccessCheckoutDiscoveryIntegrationTest", "Error received: $exception")
            exception is AccessCheckoutDiscoveryException &&
                    exception?.message == "An error was thrown when trying to make a connection to the service"
        }
    }

    @Test
    fun givenAnInitialErrorFetchingUrl_ThenShouldReAttemptAndSendBackResultOnSuccess() {
        val serviceAvailableState = "SERVICE_AVAILABLE_AGAIN"

        stubFor(
            WireMock.get("/")
                .inScenario("service re-discovery")
                .whenScenarioStateIs(Scenario.STARTED)
                .willSetStateTo(serviceAvailableState)
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(500)
                )
        )

        stubFor(
            rootResponseMapping()
                .inScenario("service re-discovery")
                .whenScenarioStateIs(serviceAvailableState)
        )

        stubFor(verifiedTokensMapping())

        var exception: Exception? = null

        val firstCallback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                exception = error
            }
        }

        val client = AccessCheckoutDiscoveryClient(AccessCheckoutDiscoveryAsyncTaskFactory())

        client.discover(wireMockRule.baseUrl(), firstCallback, DiscoverLinks.verifiedTokens)

        await().atMost(5, TimeUnit.SECONDS).until {
            Log.d("AccessCheckoutDiscoveryIntegrationTest", "Error received: $exception")
            exception is AccessCheckoutDiscoveryException &&
                    exception?.message == "An error was thrown when trying to make a connection to the service"
        }

        var url: String? = null

        val secondCallback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                url = response
            }
        }

        client.discover(wireMockRule.baseUrl(), secondCallback, DiscoverLinks.verifiedTokens)


        await().atMost(5, TimeUnit.SECONDS).until {
            Log.d("AccessCheckoutDiscoveryIntegrationTest", "Discovered endpoint: $url")
            url != null && url.equals("${wireMockRule.baseUrl()}/verifiedTokens/sessions")
        }
    }
}
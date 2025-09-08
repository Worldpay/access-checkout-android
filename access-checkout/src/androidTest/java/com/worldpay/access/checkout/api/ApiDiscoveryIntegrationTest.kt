package com.worldpay.access.checkout.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.tomakehurst.wiremock.client.VerificationException
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.exactly
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import com.github.tomakehurst.wiremock.stubbing.Scenario
import com.worldpay.access.checkout.api.ApiDiscoveryStubs.cardSessionsMapping
import com.worldpay.access.checkout.api.ApiDiscoveryStubs.rootResponseMapping
import com.worldpay.access.checkout.api.ApiDiscoveryStubs.stubServiceDiscoveryResponses
import com.worldpay.access.checkout.api.MockServer.getBaseUrl
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryClient
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.discovery.DiscoveryCache
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.awaitility.Awaitility.await
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.fail

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ApiDiscoveryIntegrationTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val applicationContext =
        InstrumentationRegistry.getInstrumentation().context.applicationContext
    private lateinit var baseUrl: URL

    @Before
    fun setUp() {
        DiscoveryCache.results.clear()
        DiscoveryCache.responses.clear()

        MockServer.startWiremock(applicationContext, 8443)
        baseUrl = getBaseUrl()

        ApiDiscoveryClient.initialise(baseUrl.toString())
    }

    @After
    fun tearDown() {
        MockServer.stopWiremock()

        ApiDiscoveryClient.reset()
    }

    @Test
    fun shouldBeAbleToDiscoverCardSessionsFromRoot() = runBlocking {
        stubServiceDiscoveryResponses()

        val endpoint = ApiDiscoveryClient.discoverEndpoint(DiscoverLinks.cardSessions)

        await().atMost(5, TimeUnit.SECONDS).until {
            endpoint.toString() == "${baseUrl}/sessions/card"
        }
    }

    @Test
    fun shouldUseCachedResultsWhenDiscoveringSameEndpointsMultipleTimes() = runBlocking {
        stubServiceDiscoveryResponses()

        val cardSessionsEndpoint1 = ApiDiscoveryClient.discoverEndpoint(DiscoverLinks.cardSessions)
        val cardSessionsEndpoint2 = ApiDiscoveryClient.discoverEndpoint(DiscoverLinks.cardSessions)
        val cardSessionsEndpoint3 = ApiDiscoveryClient.discoverEndpoint(DiscoverLinks.cardSessions)

        await().atMost(5, TimeUnit.SECONDS).until {
            cardSessionsEndpoint1.toString() == "${baseUrl}/sessions/card"
                    && cardSessionsEndpoint2.toString() == "${baseUrl}/sessions/card"
                    && cardSessionsEndpoint3.toString() == "${baseUrl}/sessions/card"
                    && assertNumberRequestsToPath(1, "/")
                    && assertNumberRequestsToPath(1, "/sessions")
        }
    }

    @Test
    fun shouldUseCachedResponsesWhenDiscoveringDifferentEndpointsOnSameURL() = runBlocking {
        stubServiceDiscoveryResponses()

        val cardEndpoint = ApiDiscoveryClient.discoverEndpoint(DiscoverLinks.cardSessions)
        val cvcEndpoint = ApiDiscoveryClient.discoverEndpoint(DiscoverLinks.cvcSessions)

        await().atMost(5, TimeUnit.SECONDS).until {
            cardEndpoint.toString() == "${baseUrl}/sessions/card"
                    && cvcEndpoint.toString() == "${baseUrl}/sessions/payments/cvc"
                    && assertNumberRequestsToPath(1, "/")
                    && assertNumberRequestsToPath(1, "/sessions")
        }
    }

    @Test
    fun shouldReturnExceptionWhenDiscoveryFails() = runBlocking {
        stubFor(
            get("/")
                .willReturn(
                    aResponse()
                        .withStatus(500)
                )
        )

        try {
            ApiDiscoveryClient.discoverEndpoint(DiscoverLinks.cardSessions)
            fail("Expected exception but got none")
        } catch (ace: AccessCheckoutException) {
            assertEquals("Could not discover session endpoint", ace.message)
        } catch (ex: Exception) {
            fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
        }
    }

    @Test
    fun shouldReturnEndpointOnSecondRetry() = runBlocking {
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

        stubFor(cardSessionsMapping())

        val endpoint = ApiDiscoveryClient.discoverEndpoint(DiscoverLinks.cardSessions)

        await().atMost(5, TimeUnit.SECONDS).until {
            endpoint.toString() == "${baseUrl}/sessions/card"
        }
    }

    private fun assertNumberRequestsToPath(numberOfTimes: Int, path: String): Boolean {
        try {
            verify(exactly(numberOfTimes), getRequestedFor(urlEqualTo(path)))
            return true
        } catch (e: VerificationException) {
            return false
        }
    }
}

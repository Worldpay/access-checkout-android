package com.worldpay.access.checkout.api

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
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.awaitility.Awaitility.await
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ApiDiscoveryIntegrationTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

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
    fun shouldBeAbleToDiscoverVTSessionsFromRoot() = runBlocking {
        stubServiceDiscoveryResponses()

        val client = ApiDiscoveryClient()
        val endpoint = client.discoverEndpoint(getBaseUrl(), DiscoverLinks.verifiedTokens)

        await().atMost(5, TimeUnit.SECONDS).until {
            endpoint.equals("${getBaseUrl()}/verifiedTokens/sessions")
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
            val client = ApiDiscoveryClient()
            client.discoverEndpoint(getBaseUrl(), DiscoverLinks.verifiedTokens)
            fail("Expected exception but got none")
        } catch (ace: AccessCheckoutException) {
            assertEquals("Error message was: Server Error", ace.message)
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

        stubFor(verifiedTokensMapping())

        val client = ApiDiscoveryClient()
        val endpoint = client.discoverEndpoint(getBaseUrl(), DiscoverLinks.verifiedTokens)

        await().atMost(5, TimeUnit.SECONDS).until {
            endpoint.equals("${getBaseUrl()}/verifiedTokens/sessions")
        }
    }
}

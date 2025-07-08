package com.worldpay.access.checkout.client.testutil.mocks

import HealthChecker
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration

object CardBinServiceMock {
    private var server: WireMockServer? = null;

    private fun createSelfSignedServer(): WireMockServer {
        val selfSignedJks =
            CardBinServiceMock::class.java.getResource("/self-signed-keystore.jks")
        return WireMockServer(
            WireMockConfiguration.options()
                .port(0)
                .dynamicHttpsPort()
                .usingFilesUnderDirectory("src/test/resources/wiremock/card-bin")
                .keystorePath(selfSignedJks!!.toString())
                .keystorePassword("changeit")
                .keyManagerPassword("changeit")
                .keystoreType("jks")
        )
    }

    fun start(): WireMockServer {
        if (!isRunning()) {
            if (server == null) {
                server = createSelfSignedServer()
            }
            println("Starting Mock card-bin-service")
            server?.start()
            val isHealthy = isRunning()
            if (!isHealthy) {
                println("Mock card-bin-service health check failed after starting.")
                throw Exception("Could not start Mock card-bin-service")
            }
        } else {
            println("Mock card-bin-service is already running at: ${server?.url("/")}")
        }
        return server!!
    }

    fun shutdown() {
        if (isRunning()) {
            println("Stopping Mock card-bin-service")
            server?.shutdown() // Ensures all resources and ports are released
            server = null      // Allow fresh start and port reuse
        } else {
            println("Mock card-bin-service is not running")
        }
    }

    fun isRunning(): Boolean {
        if (server != null) {
            return HealthChecker.checkHealth(server!!.url("/health").toString(), "Card Bin Server")
        }
        return false
    }
}
package com.worldpay.access.checkout.sample.card.standard.testutil.mock

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
                .httpsPort(3003)
                .usingFilesUnderDirectory("src/androidTestMock/resources/wiremock/card-bin")
                .keystorePath(selfSignedJks!!.toString())
                .keystorePassword("changeit")
                .keystoreType("jks")
        )
    }

    fun start(): WireMockServer {
        if (server == null) {
            server = createSelfSignedServer()
        }

        if (server?.isRunning == false) {
            println("Starting Mock card-bin-service")
            try {
                server?.start()
            } catch (ex: Exception) {
                println("card-bin-service was already running")
            }

        }

        return server!!
    }

    fun stop() {
        if (server?.isRunning == true) {
            println("Stopping Mock card-bin-service")
            server?.stop()
            server?.shutdown() // Ensures all resources and ports are released
            server = null      // Allow fresh start and port reuse
        }
    }
}

package com.worldpay.access.checkout.client.testutil.mocks

import HealthChecker
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.client.testutil.TrustAllSSLSocketFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

object AccessWPServiceMock {

    private var server: MockWebServer? = null

    fun start(): MockWebServer {
        if (!this.isRunning()) {
            println("Starting Mock access-wp-server")
            server = MockWebServer()
            server!!.useHttps(getSslContext().socketFactory, false)

            setupMockEndpoints(server!!)

            server!!.start()

            val isHealthy = isRunning()
            if (!isHealthy) {
                println("Mock access-wp-server health check failed after starting.")
                throw Exception("Could not start Mock access-wp-server")
            }
        } else {
            println("access-wp-server was already running at: ${server!!.url("/")}")
        }
        return server!!
    }

    fun shutdown() {
        if (this.isRunning()) {
            server?.shutdown()
            println("access-wp-server shut down")
            server = null
        } else {
            println("access-wp-server is not running")
        }
    }

    fun isRunning(): Boolean {
        if (server !== null) {
            return HealthChecker.checkHealth(server!!.url("/health").toString(), "Access WP Server")
        }
        return false;
    }

    private fun setupMockEndpoints(server: MockWebServer) {
        val cardConfigResponseJson =
            CardConfiguration::class.java.getResource("remote_card_config.json")?.readText()!!

        server.dispatcher = object : okhttp3.mockwebserver.Dispatcher() {
            override fun dispatch(request: okhttp3.mockwebserver.RecordedRequest): MockResponse {
                return when (request.path) {
                    "/access-checkout/cardTypes.json" -> MockResponse().setResponseCode(200)
                        .setBody(cardConfigResponseJson)

                    "/health" -> MockResponse().setResponseCode(200).setBody("""{"status":"UP"}"""")
                    else -> MockResponse().setResponseCode(500)
                        .setBody("Unknown MockServer endpoint")
                }
            }
        }
    }

    private fun getSslContext(): SSLContext {
        val stream = TrustAllSSLSocketFactory::class.java.getResource("wiremock.bks")
            ?.openStream()
        val serverKeyStore = KeyStore.getInstance("BKS")
        serverKeyStore.load(stream, "".toCharArray())

        val kmfAlgorithm = KeyManagerFactory.getDefaultAlgorithm()
        val kmf = KeyManagerFactory.getInstance(kmfAlgorithm)
        kmf.init(serverKeyStore, "password".toCharArray())

        val trustManagerFactory = TrustManagerFactory.getInstance(kmfAlgorithm)
        trustManagerFactory.init(serverKeyStore)

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(kmf.keyManagers, trustManagerFactory.trustManagers, null)
        return sslContext
    }
}

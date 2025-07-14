//package com.worldpay.access.checkout.test.mocks
//
//import android.content.Context
//import com.worldpay.access.checkout.test.utils.HealthChecker
//import com.worldpay.access.checkout.test.utils.ResourceLoader
//import okhttp3.mockwebserver.Dispatcher
//import okhttp3.mockwebserver.MockResponse
//import okhttp3.mockwebserver.MockWebServer
//import okhttp3.mockwebserver.RecordedRequest
//import java.security.KeyStore
//import javax.net.ssl.KeyManagerFactory
//import javax.net.ssl.SSLContext
//import javax.net.ssl.TrustManagerFactory
//
//object AccessWPServiceMock {
//
//    private var server: MockWebServer? = null
//
//    fun start(): MockWebServer {
//        if (!this.isRunning()) {
//            println("Starting Mock access-wp-server")
//            server = MockWebServer()
//            server!!.useHttps(getSslContext().socketFactory, false)
//
//            setupMockEndpoints(server!!)
//
//            server!!.start()
//
//            val isHealthy = isRunning()
//            if (!isHealthy) {
//                println("Mock access-wp-server health check failed after starting.")
//                throw Exception("Could not start Mock access-wp-server")
//            }
//        } else {
//            println("access-wp-server was already running at: ${server!!.url("/")}")
//        }
//        return server!!
//    }
//
//    fun shutdown() {
//        println("Initiating Mock access-wp-server shutdown")
//        if (isRunning()) {
//            println("Mocker server was healthy, Stopping Mock access-wp-server")
//            server?.shutdown()
//            server = null
//            println("access-wp-server shut down successfully")
//        } else {
//            println("access-wp-server is not running")
//        }
//    }
//
//    fun isRunning(): Boolean {
//        if (server !== null) {
//            return HealthChecker.checkHealth(server!!.url("/health").toString(), "Access WP Server")
//        }
//        return false;
//    }
//
//    private fun setupMockEndpoints(server: MockWebServer) {
//        val cardConfigResponseJson = ResourceLoader.getResourceAsText("mockServer/responses/remote_card_config.json")
//        server.dispatcher = object : Dispatcher() {
//            override fun dispatch(request: RecordedRequest): MockResponse {
//                return when (request.path) {
//                    "/access-checkout/cardTypes.json" -> MockResponse().setResponseCode(200)
//                        .setBody(cardConfigResponseJson)
//
//                    "/health" -> MockResponse().setResponseCode(200).setBody("""{"status":"UP"}"""")
//                    else -> MockResponse().setResponseCode(500)
//                        .setBody("Unknown MockServer endpoint")
//                }
//            }
//        }
//    }
//
//    private fun getSslContext(): SSLContext {
//        val stream = ResourceLoader.getResourceAsStream("mockServer/self-signed-keystore.bks")
//        val serverKeyStore = KeyStore.getInstance("BKS")
//        serverKeyStore.load(stream, "".toCharArray())
//
//        val kmfAlgorithm = KeyManagerFactory.getDefaultAlgorithm()
//        val kmf = KeyManagerFactory.getInstance(kmfAlgorithm)
//        kmf.init(serverKeyStore, "password".toCharArray())
//
//        val trustManagerFactory = TrustManagerFactory.getInstance(kmfAlgorithm)
//        trustManagerFactory.init(serverKeyStore)
//
//        val sslContext = SSLContext.getInstance("SSL")
//        sslContext.init(kmf.keyManagers, trustManagerFactory.trustManagers, null)
//        return sslContext
//    }
//
//    fun startWithContext(
//        application: Context,
//        port: Int = 8084
//    ) {
//
//    }
//}

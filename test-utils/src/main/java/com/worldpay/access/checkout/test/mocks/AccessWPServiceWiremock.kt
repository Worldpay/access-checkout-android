package com.worldpay.access.checkout.test.mocks

import android.content.Context
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.worldpay.access.checkout.test.utils.HealthChecker
import com.worldpay.access.checkout.test.utils.ResourceLoader
import java.io.File

object AccessWPServiceWiremock {
    var server: WireMockServer? = null;

    private fun createSelfSignedServer(
        port: Int = 0,
        mappingsPath: String = ResourceLoader.getResourcePath("wiremock/access-wp"),
        keystorePath: String = ResourceLoader.getResourcePath("wiremock/self-signed-keystore.bks"),
        keystorePassword: String = "password",
        keystoreType: String = "bks"
    ): WireMockServer {

        println(port)
        println(keystorePath)
        println(keystoreType)
        println(keystorePassword)

        if (port == 0) {
            return WireMockServer(
                WireMockConfiguration.options()
                    .port(0)
                    .dynamicHttpsPort()
                    .usingFilesUnderDirectory(mappingsPath)
                    .keystorePath(keystorePath)
                    .keystorePassword(keystorePassword)
                    .trustStorePassword(keystorePassword)
                    .keystoreType(keystoreType)
            )
        }
        return WireMockServer(
            WireMockConfiguration.options()
                .port(0)
                .httpsPort(port)
                .usingFilesUnderDirectory(mappingsPath)
                .keystorePath(keystorePath)
                .keystorePassword(keystorePassword)
                .keystoreType(keystoreType)
        )
    }

    fun start(): WireMockServer {
        if (!isRunning()) {
            try {
                if (server == null) {
                    server = createSelfSignedServer()
                }
                println("Starting Mock access-wp-service")

                server?.start()
            } catch (e: Exception) {
                throw e
            }

            val isHealthy = isRunning()
            if (!isHealthy) {
                println("Mock access-wp-service health check failed after starting.")
                throw Exception("Could not start Mock access-wp-service")
            }
        } else {
            println("Mock access-wp-service is already running at: ${server?.url("/")}")
        }
        return server!!
    }

    fun shutdown() {
        println("Initiating Mock access-wp-service shutdown")
        if (isRunning()) {
            println("Mocker server was healthy, Stopping Mock access-wp-service")
            server?.shutdown() // Ensures all resources and ports are released
            server = null      // Allow fresh start and port reuse
            println("access-wp-service shut down successfully")
        } else {
            println("Mock access-wp-service is not running")
        }
    }

    fun isRunning(): Boolean {
        if (server != null) {
            return HealthChecker.checkHealth(server!!.url("/health").toString(), "Access WP Server")
        }
        return false
    }


    fun startWithContext(
        context: Context,
        port: Int = 8084
    ): WireMockServer {

        val wiremockDest = File(context.cacheDir, "wiremock")
        ResourceLoader.copyAssetRecursively(context, "wiremock", wiremockDest)

        val cardBinDest = File(wiremockDest, "access-wp")
        val keystoreDest = File(wiremockDest, "self-signed-keystore.bks")

        server = createSelfSignedServer(
            port = port,
            mappingsPath = cardBinDest.absolutePath,
            keystorePath = keystoreDest.absolutePath,
            keystoreType = "BKS",
            keystorePassword = "password"
        )

        return server!!
    }

    fun start(
        context: Context? = null,
        port: Int = 0
    ): WireMockServer {
        if (!isRunning()) {
            try {
                if (server == null) {

                    if (context != null) {
                        server =
                            startWithContext(context, port)
                    } else {
                        server = createSelfSignedServer()
                    }
                }
                println("Starting Mock access-wp-service")
                server?.start()
            } catch (e: Exception) {
                throw e
            }

            val isHealthy = isRunning()
            if (!isHealthy) {
                println("Mock access-wp-service health check failed after starting.")
                throw Exception("Could not start Mock access-wp-service")
            }
        } else {
            println("Mock access-wp-service is already running at: ${server?.url("/")}")
        }
        return server!!
    }
}
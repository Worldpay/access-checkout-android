package com.worldpay.access.checkout.api

import android.content.Context
import android.util.Log
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.google.android.gms.security.ProviderInstaller
import com.worldpay.access.checkout.api.ApiDiscoveryStubs.stubServiceDiscoveryResponses
import com.worldpay.access.checkout.api.ssl.client.TrustAllSSLSocketFactory
import com.worldpay.access.checkout.api.ssl.server.CustomHttpServerFactory
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object MockServer {

    private lateinit var context: Context
    private lateinit var wireMockServer: WireMockServer
    private lateinit var baseUrl: String

    private var hasStarted = false

    fun startWiremock(context: Context, port: Int = 8084) {
        ProviderInstaller.installIfNeeded(context)

        Log.d("MockServer", "Starting WireMock server!")

        MockServer.context = context

        val keyStoreFile = File(context.cacheDir, "wiremock.bks")
        val keystoreInputStream = context.assets.open("wiremock.bks")
        keystoreInputStream.copyTo(FileOutputStream(keyStoreFile))

        HttpsURLConnection.setDefaultSSLSocketFactory(TrustAllSSLSocketFactory())

        wireMockServer = WireMockServer(
            WireMockConfiguration
                .options()
                .notifier(ConsoleNotifier(true))
                .httpsPort(port)
                .httpServerFactory(CustomHttpServerFactory())
                .keystorePath(keyStoreFile.absolutePath)
                .keystoreType("BKS")
                .keystorePassword("password")
                .extensions(ResponseTemplateTransformer(false))
        )

        Thread(
            Runnable {
                wireMockServer.start()
                stubServiceDiscoveryResponses()
                hasStarted = true
            }
        ).start()

        waitForWiremock()
    }

    fun stopWiremock() {
        wireMockServer.stop()
    }

    fun getBaseUrl(): URL {
        return URL(baseUrl)
    }

    fun getStringBaseUrl(): String {
        return baseUrl
    }

    private fun waitForWiremock() {
        do {
            Thread.sleep(1000)
            Log.d("MockServer", "Waiting for wiremock to start!")
        } while (!hasStarted)
        Log.d("MockServer", "Started wiremock!!")
        baseUrl = wireMockServer.baseUrl()
    }
}

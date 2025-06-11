package com.worldpay.access.checkout.sample

import android.content.Context
import android.os.Build
import android.util.Log
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.google.android.gms.security.ProviderInstaller
import com.worldpay.access.checkout.sample.ssl.client.TrustAllSSLSocketFactory
import com.worldpay.access.checkout.sample.ssl.server.CustomHttpServerFactory
import com.worldpay.access.checkout.sample.stub.BrandLogoMockStub.stubLogos
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.stubCardConfiguration
import com.worldpay.access.checkout.sample.stub.RootResourseMockStub.rootResourceMapping
import com.worldpay.access.checkout.sample.stub.SessionsMockStub.stubSessionsCardRequest
import com.worldpay.access.checkout.sample.stub.SessionsMockStub.stubSessionsPaymentCvcRequest
import com.worldpay.access.checkout.sample.stub.SessionsMockStub.stubSessionsTokenRootRequest
import java.io.File
import java.io.FileOutputStream
import javax.net.ssl.HttpsURLConnection

object MockServer {

    private lateinit var context: Context
    private lateinit var wireMockServer: WireMockServer
    private lateinit var baseUrl: String

    private var hasStarted = false

    object Paths {
        const val SESSIONS_ROOT_PATH = "sessions"
        const val SESSIONS_PAYMENTS_CVC_PATH = "sessions/payments/cvc"
        const val SESSIONS_CARD_PATH = "sessions/card"

        const val CARD_LOGO_PATH = "access-checkout/assets"
        const val CARD_CONFIGURATION_PATH = "access-checkout/cardTypes.json"
    }

    fun startWiremock(context: Context, port: Int = 8443) {
        ProviderInstaller.installIfNeeded(context)

        Log.d("MockServer", "Starting WireMock server!")

        MockServer.context = context

        val keyStoreFile = File(context.cacheDir, "wiremock.bks")
        val keystoreInputStream = context.assets.open("wiremock.bks")
        keystoreInputStream.copyTo(FileOutputStream(keyStoreFile))

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            HttpsURLConnection.setDefaultSSLSocketFactory(TrustAllSSLSocketFactory())
        }

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
                defaultStubMappings(
                    context
                )
                hasStarted = true
            }
        ).start()

        waitForWiremock()
    }

    fun stopWiremock() {
        wireMockServer.stop()
    }

    fun stubFor(mappingBuilder: MappingBuilder) {
        wireMockServer.stubFor(mappingBuilder)
    }

    fun getBaseUrl(): String {
        return baseUrl
    }

    fun defaultStubMappings(context: Context) {
        Log.d("MockServer", "Stubbing root endpoint with 200 response")
        try {
            wireMockServer.stubFor(rootResourceMapping())
            // sessions service root endpoint
            stubSessionsTokenRootRequest()

            // card and cvc sessions endpoints
            stubSessionsCardRequest(context)
            stubSessionsPaymentCvcRequest(context)

            stubCardConfiguration(context)
            stubLogos(context)
        } catch (e:Exception) {
            Log.d("MockServer", "Failed to set up default stub mappings ${e.message}",)
        }

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

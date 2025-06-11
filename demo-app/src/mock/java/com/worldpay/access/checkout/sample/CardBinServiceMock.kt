package com.worldpay.access.checkout.sample

import android.content.Context
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import java.io.File

object CardBinServiceMock {
    private var server: WireMockServer? = null;
    private fun copyAssetRecursively(context: Context, assetPath: String, dest: File) {
        val assetManager = context.assets
        val assets = assetManager.list(assetPath)
        if (assets.isNullOrEmpty()) {
            // It's a file
            println("Copying asset file: $assetPath -> ${dest.absolutePath}")
            dest.parentFile?.mkdirs()
            assetManager.open(assetPath).use { input ->
                dest.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } else {
            // It's a directory
            dest.mkdirs()
            for (file in assets) {
                copyAssetRecursively(context, "$assetPath/$file", File(dest, file))
            }
        }
    }

    private fun createSelfSignedServer(context: Context,): WireMockServer {
        val wiremockDest = File(context.cacheDir, "wiremock")
        copyAssetRecursively(context, "wiremock", wiremockDest)

        val cardBinDest = File(wiremockDest, "card-bin")
        val keystoreDest = File(wiremockDest, "self-signed-keystore.bks")

        return WireMockServer(
            WireMockConfiguration.options()
                .port(0)
                .httpsPort(3003)
                .usingFilesUnderDirectory(cardBinDest.absolutePath)
                .keystorePath(keystoreDest.absolutePath)
                .keystoreType("BKS")
                .keystorePassword("password")
        )
    }

    fun start(context: Context,): WireMockServer {
        if (server == null) {
            server = createSelfSignedServer(context)
        }

        if (server?.isRunning == false) {
            println("Starting Mock card-bin-service")
            try {
                server?.start()
                println("Starting Mock card-bin-service: ${server?.isRunning}")

            } catch (ex: Exception) {
                println("card-bin-service was already running ${ex.message}")
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
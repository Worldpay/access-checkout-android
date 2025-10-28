package com.worldpay.access.checkout.util

import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object HealthChecker {

    private val client: OkHttpClient = createUnsafeClient()

    private fun createUnsafeClient(): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("TLSv1.3") // Explicitly use TLSv1.3
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }


    fun checkHealth(endpoint: String, serverName: String): Boolean {
        val isHealthy = checkHealthWithRetries(endpoint)
        println("Health check for $serverName at $endpoint is healthy: $isHealthy")

        return isHealthy
    }

    private fun checkHealthWithRetries(endpoint: String, maxRetries: Int = 10): Boolean {
        var attempt = 0
        var delayMillis = 1000L // Initial delay of 1 second

        while (attempt < maxRetries) {
            if (checkHealth(endpoint)) {
                return true
            }

            attempt++
            if (attempt < maxRetries) {
                println("[$attempt/$maxRetries] Retrying health check for $endpoint...")
                Thread.sleep(delayMillis)
                delayMillis *= 2 // Exponential backoff
            }
        }

        return false
    }

    private fun checkHealth(endpoint: String): Boolean {
        val request = Request.Builder()
            .url(endpoint)
            .get()
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            println("Health check failed for $endpoint: ${e.message}")
            false
        }
    }
}
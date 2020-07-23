package com.worldpay.access.checkout.sample.stub

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.MockServer.defaultStubMappings
import com.worldpay.access.checkout.sample.MockServer.getBaseUrl
import com.worldpay.access.checkout.sample.MockServer.startWiremock
import com.worldpay.access.checkout.sample.MockServer.stopWiremock
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.stub.RootResourseMockStub.simulateRootResourceTemporaryServerError
import okhttp3.OkHttpClient
import okhttp3.Request.Builder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class MockServerTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    private val baseURL = "http://localhost:8080"

    @Before
    fun setup() {
        stopWiremock()
        startWiremock(activityRule.activity)
        defaultStubMappings(activityRule.activity)
    }

    @Test
    fun shouldReturnSessionsUrl_whenCallingRootResource() {
        val client = OkHttpClient()

        val request = Builder()
            .url("$baseURL/")
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            assertNotNull(responseBody)
            assertTrue(responseBody.contains("service:sessions"))
        }
    }

    @Test
    fun shouldReturnPaymentsCvcUrl_whenCallingSessionsUrl() {
        val client = OkHttpClient()

        val request = Builder()
            .url("$baseURL/sessions")
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            assertNotNull(responseBody)
            assertTrue(responseBody.contains("sessions:paymentsCvc"))
        }
    }

    @Test
    fun shouldBeAbleToReturnValidResponseAfterRetry_whenFirstCallToRootResourceFails() {
        simulateRootResourceTemporaryServerError()

        val client = OkHttpClient()

        val request = Builder()
            .url("$baseURL/")
            .build()

        client.newCall(request).execute().use { response ->
            assertEquals(500, response.code)
        }

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            assertNotNull(responseBody)
            assertTrue(responseBody.contains("service:verifiedTokens"))
        }
    }

    @Test
    fun shouldBeAbleToFetchTheConfigurationExternally_whenCardConfigIsHosted() {
        val client = OkHttpClient()

        val request = Builder()
            .url("$baseURL/access-checkout/cardTypes.json")
            .build()

        val cardConfigurationInputStream = activityRule.activity.resources.openRawResource(
            R.raw.card_types
        )
        val cardConfigurationAsString = cardConfigurationInputStream.reader(Charsets.UTF_8).readText()
        val substitutedResponseTemplateString = cardConfigurationAsString.replace("{{request.requestLine.baseUrl}}", getBaseUrl())

        client.newCall(request).execute().use { response ->
            assertEquals(substitutedResponseTemplateString, response.body?.string())
        }
    }

}

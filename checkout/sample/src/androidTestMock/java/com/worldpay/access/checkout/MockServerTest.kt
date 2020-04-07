package com.worldpay.access.checkout

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.MockServer.defaultStubMappings
import com.worldpay.access.checkout.MockServer.startWiremock
import com.worldpay.access.checkout.MockServer.stopWiremock
import com.worldpay.access.checkout.RootResourseMockStub.simulateRootResourceTemporaryServerError
import okhttp3.OkHttpClient
import okhttp3.Request.Builder
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MockServerTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    private val sessionsContentType = "application/vnd.worldpay.sessions-v1.hal+json"

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
            assertThat(response.body()?.string(), containsString("service:sessions"))
        }
    }

    @Test
    fun shouldReturnPaymentsCvcUrl_whenCallingSessionsUrl() {
        val client = OkHttpClient()

        val request = Builder()
            .url("$baseURL/sessions")
            .build()

        client.newCall(request).execute().use { response ->
            assertThat(response.body()?.string(), containsString("sessions:paymentsCvc"))
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
            assertThat(response.code(), CoreMatchers.equalTo(500))
        }

        client.newCall(request).execute().use { response ->
            assertThat(response.body()?.string(), containsString("service:verifiedTokens"))
        }
    }

    @Test
    fun shouldBeAbleToFetchTheConfigurationExternally_whenCardConfigIsHosted() {
        val client = OkHttpClient()

        val request = Builder()
            .url("$baseURL/access-checkout/cardConfiguration.json")
            .build()

        val cardConfigurationInputStream = activityRule.activity.resources.openRawResource(R.raw.card_configuration_file)
        val cardConfigurationAsString = cardConfigurationInputStream.reader(Charsets.UTF_8).readText()
        val substitutedResponseTemplateString = cardConfigurationAsString.replace("{{request.requestLine.baseUrl}}", MockServer.baseUrl)

        client.newCall(request).execute().use { response ->
            assertThat(response.body()?.string(), CoreMatchers.equalTo(substitutedResponseTemplateString))
        }
    }

}
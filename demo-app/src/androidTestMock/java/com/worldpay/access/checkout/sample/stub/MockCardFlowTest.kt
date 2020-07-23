package com.worldpay.access.checkout.sample.stub

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.MatchesJsonPathPattern
import com.worldpay.access.checkout.BuildConfig
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.MockServer.Paths.VERIFIED_TOKENS_SESSIONS_PATH
import com.worldpay.access.checkout.sample.MockServer.defaultStubMappings
import com.worldpay.access.checkout.sample.MockServer.startWiremock
import com.worldpay.access.checkout.sample.MockServer.stopWiremock
import com.worldpay.access.checkout.sample.MockServer.stubFor
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.stub.VerifiedTokenMockStub.VERIFIED_TOKENS_MEDIA_TYPE
import com.worldpay.access.checkout.sample.stub.VerifiedTokenMockStub.simulateHttpRedirect
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request.Builder
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class MockCardFlowTest {

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
    fun shouldReturnVerifiedTokenUrl_whenCallingRootResource() {
        val client = OkHttpClient()

        val request = Builder()
            .url("$baseURL/")
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            assertNotNull(responseBody)
            assertTrue(responseBody.contains("service:verifiedTokens"))
        }
    }

    @Test
    fun shouldReturnVerifiedTokenSessionsUrl_whenCallingVerifiedTokenUrl() {
        val client = OkHttpClient()

        val request = Builder()
            .url("$baseURL/verifiedTokens")
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            assertNotNull(responseBody)
            assertTrue(responseBody.contains("verifiedTokens:sessions"))
        }
    }

    @Test
    fun shouldReturnLinkToSessionReference_whenCallingVerifiedTokenWithValidSDKHeaderForReleaseVersion() {
        val client = OkHttpClient()

        val version = "access-checkout-android/${BuildConfig.VERSION_CODE}.0.0"

        val request = Builder()
            .method("POST", "{}".toRequestBody(VERIFIED_TOKENS_MEDIA_TYPE.toMediaTypeOrNull()))
            .header("Accept", VERIFIED_TOKENS_MEDIA_TYPE)
            .header("X-WP-SDK", version)
            .url("$baseURL/$VERIFIED_TOKENS_SESSIONS_PATH")
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            assertNotNull(responseBody)
            assertTrue(
                responseBody.contains(
                activityRule.activity.resources.getString(
                        R.string.verified_token_session_reference
                    )
                )
            )
        }
    }

    @Test
    fun shouldReturnLinkToSessionReference_whenCallingVerifiedTokenWithValidSDKHeaderForSnapshotVersion() {
        val client = OkHttpClient()

        val version = "access-checkout-android/${BuildConfig.VERSION_CODE}.0.0-SNAPSHOT"

        val request = Builder()
            .method("POST", "{}".toRequestBody(VERIFIED_TOKENS_MEDIA_TYPE.toMediaTypeOrNull()))
            .header("Accept", VERIFIED_TOKENS_MEDIA_TYPE)
            .header("X-WP-SDK", version)
            .url("$baseURL/$VERIFIED_TOKENS_SESSIONS_PATH")
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            assertNotNull(responseBody)
            assertTrue(
                responseBody.contains(
                    activityRule.activity.resources.getString(
                        R.string.verified_token_session_reference
                    )
                )
            )
        }
    }

    @Test
    fun shouldReturn404Status_whenCallingVerifiedTokenWithInvalidSDKHeader() {
        val client = OkHttpClient()

        val request = Builder()
            .method("POST", "{}".toRequestBody(VERIFIED_TOKENS_MEDIA_TYPE.toMediaTypeOrNull()))
            .header("Accept", VERIFIED_TOKENS_MEDIA_TYPE)
            .header("X-WP-SDK", "access-checkout-android/bad_version")
            .url("$baseURL/$VERIFIED_TOKENS_SESSIONS_PATH")
            .build()

        client.newCall(request).execute().use { response ->
            assertEquals(404, response.code)
        }
    }

    @Test
    fun shouldReturn400Status_whenCallingVerifiedTokenWithInvalidCardNumber() {
        val pan = "7687655651111111113"
        simulateErrorResponse(pan)

        val client = OkHttpClient()

        val expectedResponse = """{
                                "errorName": "bodyDoesNotMatchSchema",
                                "message": "The json body provided does not match the expected schema",
                                "validationErrors": [
                                    {
                                        "errorName": "panFailedLuhnCheck",
                                        "message": "The identified field contains a PAN that has failed the Luhn check.",
                                        "jsonPath": "$.cardNumber"
                                    }
                                ]
                            }""".trimIndent()

        val request = Builder()
            .method("POST", "{ \"cardNumber\":\"${pan}\" }".toRequestBody(VERIFIED_TOKENS_MEDIA_TYPE.toMediaTypeOrNull()))
            .header("Accept", VERIFIED_TOKENS_MEDIA_TYPE)
            .url("$baseURL/$VERIFIED_TOKENS_SESSIONS_PATH")
            .build()

        client.newCall(request).execute().use { response ->
            assertEquals(400, response.code)
            assertEquals(expectedResponse, response.body?.string())
        }
    }

    @Test
    fun shouldFollowNewUrl_whenReceivingHttpRedirectResponse() {
        simulateHttpRedirect(activityRule.activity)

        val client = OkHttpClient()
            .newBuilder()
            .followRedirects(false)
            .build()

        val body = "{}".toRequestBody(VERIFIED_TOKENS_MEDIA_TYPE.toMediaTypeOrNull())
        
        val request = Builder()
            .method("POST", body)
            .header("Accept", VERIFIED_TOKENS_MEDIA_TYPE)
            .header("X-WP-SDK", "access-checkout-android/${BuildConfig.VERSION_CODE}.0.0")
            .url("$baseURL/$VERIFIED_TOKENS_SESSIONS_PATH")
            .build()

        val relocatedRequest = Builder()
            .method("POST", body)
            .header("Accept", VERIFIED_TOKENS_MEDIA_TYPE)
            .header("X-WP-SDK", "access-checkout-android/${BuildConfig.VERSION_CODE}.0.0")
            .url("$baseURL/newVerifiedTokensLocation/sessions")
            .build()

        client.newCall(request).execute().use { response ->
            assertEquals(308, response.code)
            assertEquals("$baseURL/newVerifiedTokensLocation/sessions", response.header("Location"))
        }

        client.newCall(relocatedRequest).execute().use { response ->
            val responseBody = response.body?.string()
            assertNotNull(responseBody)
            assertTrue(
                responseBody.contains(activityRule.activity.resources.getString(
                R.string.verified_token_session_reference
            ))
            )
        }
    }

    @Test
    fun shouldBeAbleToFetchLogosExternally_whenLogosAreHosted() {
        val client = OkHttpClient()

        val logos = listOf("visa", "amex", "mastercard")

        logos.forEach {
            val request = Builder()
                .url("$baseURL/access-checkout/assets/$it.svg")
                .build()

            val logoInputStream = activityRule.activity.assets.open("$it.svg")
            val logoAsString = logoInputStream.reader(Charsets.UTF_8).readText()

            client.newCall(request).execute().use { response ->
                assertEquals("image/svg+xml", response.header("Content-Type"))
                assertEquals(logoAsString, response.body?.string(), "Logo did not match expected for $it")
            }
        }

    }

    private fun simulateErrorResponse(pan: String) {
        stubFor(
            post(urlEqualTo("/$VERIFIED_TOKENS_SESSIONS_PATH"))
                .withHeader("Accept", WireMock.equalTo(VERIFIED_TOKENS_MEDIA_TYPE))
                .withHeader("Content-Type", containing(VERIFIED_TOKENS_MEDIA_TYPE))
                .withRequestBody(MatchesJsonPathPattern("$[?(@.cardNumber=='${pan}')]"))
                .willReturn(
                    aResponse()
                        .withFixedDelay(2000)
                        .withStatus(400)
                        .withBody(
                            """{
                                "errorName": "bodyDoesNotMatchSchema",
                                "message": "The json body provided does not match the expected schema",
                                "validationErrors": [
                                    {
                                        "errorName": "panFailedLuhnCheck",
                                        "message": "The identified field contains a PAN that has failed the Luhn check.",
                                        "jsonPath": "$.cardNumber"
                                    }
                                ]
                            }""".trimIndent()
                        )
                )
        )
    }

}

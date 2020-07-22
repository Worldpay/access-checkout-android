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
import com.worldpay.access.checkout.sample.stub.VerifiedTokenMockStub.simulateHttpRedirect
import okhttp3.MediaType.parse
import okhttp3.OkHttpClient
import okhttp3.Request.Builder
import okhttp3.RequestBody.create
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MockCardFlowTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(
        MainActivity::class.java)

    private val verifiedTokensContentType = "application/vnd.worldpay.verified-tokens-v1.hal+json"

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
            assertThat(
                response.body()?.string(),
                containsString("service:verifiedTokens")
            )
        }
    }

    @Test
    fun shouldReturnVerifiedTokenSessionsUrl_whenCallingVerifiedTokenUrl() {
        val client = OkHttpClient()

        val request = Builder()
            .url("$baseURL/verifiedTokens")
            .build()

        client.newCall(request).execute().use { response ->
            assertThat(response.body()?.string(), containsString("verifiedTokens:sessions"))
        }
    }

    @Test
    fun shouldReturnLinkToSessionReference_whenCallingVerifiedTokenWithValidSDKHeaderForReleaseVersion() {
        val client = OkHttpClient()

        val version = "access-checkout-android/${BuildConfig.VERSION_CODE}.0.0"

        val request = Builder()
            .method("POST", create(parse(verifiedTokensContentType), "{}"))
            .header("Accept", verifiedTokensContentType)
            .header("X-WP-SDK", version)
            .url("$baseURL/verifiedTokens/sessions")
            .build()

        client.newCall(request).execute().use { response ->
            assertThat(
                response.body()?.string(), containsString(
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
            .method("POST", create(parse(verifiedTokensContentType), "{}"))
            .header("Accept", verifiedTokensContentType)
            .header("X-WP-SDK", version)
            .url("$baseURL/verifiedTokens/sessions")
            .build()

        client.newCall(request).execute().use { response ->
            assertThat(
                response.body()?.string(), containsString(
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
            .method("POST", create(parse(verifiedTokensContentType), "{}"))
            .header("Accept", verifiedTokensContentType)
            .header("X-WP-SDK", "access-checkout-android/bad_version")
            .url("$baseURL/verifiedTokens/sessions")
            .build()

        client.newCall(request).execute().use { response ->
            assertThat(response.code(), equalTo(404))
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
            .method("POST",
                create(
                    parse(verifiedTokensContentType),
                    "{ \"cardNumber\":\"${pan}\" }"
                )
            )
            .header("Accept", verifiedTokensContentType)
            .url("$baseURL/verifiedTokens/sessions")
            .build()

        client.newCall(request).execute().use { response ->
            assertThat(response.code(), equalTo(400))
            assertThat(response.body()?.string(), equalTo(expectedResponse))
        }
    }

    @Test
    fun shouldFollowNewUrl_whenReceivingHttpRedirectResponse() {
        simulateHttpRedirect(activityRule.activity)

        val client = OkHttpClient()

        val request = Builder()
            .method("POST", create(parse(verifiedTokensContentType), "{}"))
            .header("Accept", verifiedTokensContentType)
            .header("X-WP-SDK", "access-checkout/${BuildConfig.VERSION_CODE}.0.0")
            .url("$baseURL/verifiedTokens/sessions")
            .build()

        val relocatedRequest = Builder()
            .method("POST", create(parse(verifiedTokensContentType), "{}"))
            .header("Accept", verifiedTokensContentType)
            .header("X-WP-SDK", "access-checkout-android/${BuildConfig.VERSION_CODE}.0.0")
            .url("$baseURL/newVerifiedTokensLocation/sessions")
            .build()

        client.newCall(request).execute().use { response ->
            assertThat(response.header("Location"),
                equalTo("$baseURL/newVerifiedTokensLocation/sessions")
            )
            assertThat(response.code(), equalTo(308))
        }

        client.newCall(relocatedRequest).execute().use { response ->
            assertThat(response.body()?.string(), containsString(activityRule.activity.resources.getString(
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
                assertThat(response.header("Content-Type"), equalTo("image/svg+xml"))
                assertThat("Logo did not match expected for $it", response.body()?.string(), CoreMatchers.equalTo(logoAsString))
            }
        }

    }

    private fun simulateErrorResponse(pan: String) {
        stubFor(
            post(urlEqualTo("/$VERIFIED_TOKENS_SESSIONS_PATH"))
                .withHeader("Accept", WireMock.equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", containing("application/vnd.worldpay.verified-tokens-v1.hal+json"))
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

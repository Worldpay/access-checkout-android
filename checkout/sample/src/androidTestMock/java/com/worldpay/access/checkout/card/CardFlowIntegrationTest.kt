package com.worldpay.access.checkout.card

import android.content.Context
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.MatchesJsonPathPattern
import com.worldpay.access.checkout.MockServer.Paths.VERIFIED_TOKENS_SESSIONS_PATH
import com.worldpay.access.checkout.MockServer.getCurrentContext
import com.worldpay.access.checkout.MockServer.stubFor
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.VerifiedTokenMockStub.VerifiedTokenResponses.validResponseWithDelay
import com.worldpay.access.checkout.VerifiedTokenMockStub.simulateHttpRedirect
import com.worldpay.access.checkout.card.testutil.AbstractCardFlowUITest
import com.worldpay.access.checkout.card.testutil.CardFragmentTestUtils
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
import com.worldpay.access.checkout.testutil.UITestUtils.assertDisplaysResponseFromServer
import com.worldpay.access.checkout.testutil.UITestUtils.reopenApp
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CardFlowIntegrationTest : AbstractCardFlowUITest() {

    private val amexCard = "343434343434343"
    private val amexCvv = "1234"
    private val month = "12"
    private val year = "99"

    @Test
    fun shouldBeAbleToRetrieveSuccessfulResponse_withRedirects() {
        simulateHttpRedirect(activityRule.activity)

        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = amexCard, cvv = amexCvv, month = month, year = year)
            .clickSubmitButton()
            .requestIsInProgress()

        assertDisplaysResponseFromServer(
            mapOf(VERIFIED_TOKEN_SESSION to activityRule.activity.getString(R.string.verified_token_session_reference)).toString(),
            activityRule.activity.window.decorView
        )

        cardFragmentTestUtils.isInInitialState()
    }

    @Test
    fun shouldBeAbleToRetrieveSuccessfulResponse_withoutRedirects() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = amexCard, cvv = amexCvv, month = month, year = year)
            .clickSubmitButton()
            .requestIsInProgress()

        assertDisplaysResponseFromServer(
            mapOf(VERIFIED_TOKEN_SESSION to activityRule.activity.getString(R.string.verified_token_session_reference)).toString(),
            activityRule.activity.window.decorView
        )

        cardFragmentTestUtils.isInInitialState()
    }

    @Test
    fun shouldKeepFieldValuesUponIncorrectSubmission() {
        simulateErrorResponse(activityRule.activity)

        val unknownCardError = activityRule.activity.applicationContext.resources.getString(R.string.error_response_card_number)

        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = unknownCardError, cvv = amexCvv, month = month, year = year)
            .clickSubmitButton()
            .requestIsInProgress()

        assertDisplaysResponseFromServer(
            "Error: The json body provided does not match the expected schema",
            activityRule.activity.window.decorView
        )

        cardFragmentTestUtils.isInErrorState(pan = unknownCardError, cvv = amexCvv, month = month, year = year)
    }

    @Test
    fun shouldContinueWithServiceCall_whenAppIsRotated_afterSubmission() {
        simulateDelayedResponse(activityRule.activity)

        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = amexCard, cvv = amexCvv, month = month, year = year)
            .clickSubmitButton()

        activityRule.activity.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE

        cardFragmentTestUtils.requestIsInProgress()

        assertDisplaysResponseFromServer(
            mapOf(VERIFIED_TOKEN_SESSION to activityRule.activity.getString(R.string.verified_token_session_reference)).toString(),
            activityRule.activity.window.decorView
        )

        val cardFragmentTestUtils = CardFragmentTestUtils(activityRule.activity)
        cardFragmentTestUtils.isInInitialState()
    }

    @Test
    fun shouldContinueWithServiceCall_whenAppIsReopened_afterSubmission() {
        simulateDelayedResponse(activityRule.activity, 10000)

        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = amexCard, cvv = amexCvv, month = month, year = year)
            .clickSubmitButton()

        reopenApp()

        assertDisplaysResponseFromServer(
            mapOf(VERIFIED_TOKEN_SESSION to activityRule.activity.getString(R.string.verified_token_session_reference)).toString(),
            activityRule.activity.window.decorView
        )

        val cardFragmentTestUtils = CardFragmentTestUtils(activityRule.activity)
        cardFragmentTestUtils.isInInitialState()
    }

    private fun simulateDelayedResponse(context: Context, delay: Int = 7000) {
        stubFor(
            post(urlEqualTo("/$VERIFIED_TOKENS_SESSIONS_PATH"))
                .withHeader("Accept", equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", containing("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(MatchesJsonPathPattern("$[?(@.cardNumber=='${getCurrentContext().getString(
                    R.string.long_delay_card_number
                )}')]"))
                .willReturn(validResponseWithDelay(context, delay))
        )
    }

    private fun simulateErrorResponse(context: Context) {
        stubFor(
            post(urlEqualTo("/$VERIFIED_TOKENS_SESSIONS_PATH"))
                .withHeader("Accept", equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", containing("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(MatchesJsonPathPattern("$[?(@.cardNumber=='${context.getString(
                    R.string.error_response_card_number
                )}')]"))
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
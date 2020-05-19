package com.worldpay.access.checkout.sample.card

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.MatchesJsonPathPattern
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
import com.worldpay.access.checkout.sample.MockServer.Paths.VERIFIED_TOKENS_SESSIONS_PATH
import com.worldpay.access.checkout.sample.MockServer.getCurrentContext
import com.worldpay.access.checkout.sample.MockServer.stubFor
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.testutil.CardFragmentTestUtils
import com.worldpay.access.checkout.sample.stub.VerifiedTokenMockStub.VerifiedTokenResponses.validResponseWithDelay
import com.worldpay.access.checkout.sample.stub.VerifiedTokenMockStub.simulateHttpRedirect
import com.worldpay.access.checkout.sample.testutil.UITestUtils.reopenApp
import com.worldpay.access.checkout.sample.testutil.UITestUtils.setOrientationLeft
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CardFlowIntegrationTest : AbstractCardFragmentTest() {

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
            .hasResponseDialogWithMessage(
                mapOf(VERIFIED_TOKEN_SESSION to activityRule.activity.getString(R.string.verified_token_session_reference)).toString()
            )
            .closeDialog()
            .isInInitialState()
    }

    @Test
    fun shouldBeAbleToRetrieveSuccessfulResponse_withoutRedirects() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = amexCard, cvv = amexCvv, month = month, year = year)
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(VERIFIED_TOKEN_SESSION to activityRule.activity.getString(R.string.verified_token_session_reference)).toString()
            )
            .closeDialog()
            .isInInitialState()
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
            .hasErrorDialogWithMessage(
                "The json body provided does not match the expected schema"
            )
            .closeDialog()
            .isInErrorState(pan = unknownCardError, cvv = amexCvv, month = month, year = year)
    }

    @Test
    fun shouldContinueWithServiceCall_whenAppIsRotated_afterSubmission() {
        simulateDelayedResponse(activityRule.activity)

        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = amexCard, cvv = amexCvv, month = month, year = year)
            .clickSubmitButton()

        // rotate landscape
        setOrientationLeft()

        CardFragmentTestUtils(activityRule)
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(VERIFIED_TOKEN_SESSION to activityRule.activity.getString(R.string.verified_token_session_reference)).toString()
            )
            .closeDialog()
            .isInInitialState()
    }

    @Test
    fun shouldContinueWithServiceCall_whenAppIsReopened_afterSubmission() {
        simulateDelayedResponse(activityRule.activity, 10000)

        CardFragmentTestUtils(activityRule)
            .isInInitialState()
            .enterCardDetails(pan = amexCard, cvv = amexCvv, month = month, year = year)
            .clickSubmitButton()

        reopenApp()

        CardFragmentTestUtils(activityRule)
            .hasResponseDialogWithMessage(
                mapOf(VERIFIED_TOKEN_SESSION to activityRule.activity.getString(R.string.verified_token_session_reference)).toString()
            )
            .closeDialog()
            .isInInitialState()
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
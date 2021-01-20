package com.worldpay.access.checkout.sample.card

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.MatchesJsonPathPattern
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.client.session.model.SessionType.CVC
import com.worldpay.access.checkout.sample.MockServer.Paths.VERIFIED_TOKENS_SESSIONS_PATH
import com.worldpay.access.checkout.sample.MockServer.stubFor
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.standard.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand
import com.worldpay.access.checkout.sample.card.standard.testutil.CardFragmentTestUtils
import com.worldpay.access.checkout.sample.stub.VerifiedTokenMockStub.VERIFIED_TOKENS_MEDIA_TYPE
import com.worldpay.access.checkout.sample.stub.VerifiedTokenMockStub.VerifiedTokenResponses.validResponseWithDelay
import com.worldpay.access.checkout.sample.stub.VerifiedTokenMockStub.simulateHttpRedirect
import com.worldpay.access.checkout.sample.testutil.UITestUtils.reopenApp
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotateLandscape
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CardFlowIntegrationTest : AbstractCardFragmentTest() {

    private val amexCard = "343434343434343"
    private val amexCvc = "1234"

    @Test
    fun shouldBeAbleToRetrieveSuccessfulResponse_withRedirects() {
        simulateHttpRedirect(activityRule.activity)

        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = amexCard, cvc = amexCvc, expiryDate = "1299")
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(CARD to activityRule.activity.getString(R.string.verified_token_session_reference)).toString()
            )
            .closeDialog()
            .isInInitialState()
    }

    @Test
    fun shouldBeAbleToProcessSingleSessionAfterPreviousMultiSessionRequestFailed() {
        val unknownCardError = "7687655651111111113"
        simulateErrorResponse(unknownCardError)

        // failing scenario with payments cvc session
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = unknownCardError, cvc = amexCvc, expiryDate = "1299")
            .setPaymentsCvcSwitchState(checked = true)
            .clickSubmitButton()
            .requestIsInProgress()
            .hasErrorDialogWithMessage(
                "bodyDoesNotMatchSchema : The json body provided does not match the expected schema"
            )
            .closeDialog()
            .isInErrorState(pan = unknownCardError, cvc = amexCvc, expiryDate = "12/99")
            .paymentsCvcSessionCheckedState(checked = true)

        // passing scenario with single session
        cardFragmentTestUtils
            .enterCardDetails(pan = amexCard, cvc = amexCvc, expiryDate = "1299")
            .setPaymentsCvcSwitchState(checked = false)
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(CARD to activityRule.activity.getString(R.string.verified_token_session_reference)).toString()
            )
            .closeDialog()
            .isInInitialState()
    }

    @Test
    fun shouldBeAbleToRetrieveSuccessfulResponse_withoutRedirects() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = amexCard, cvc = amexCvc, expiryDate = "1299")
            .clickSubmitButton()
            .requestIsInProgress()
            .hasResponseDialogWithMessage(
                mapOf(CARD to activityRule.activity.getString(R.string.verified_token_session_reference)).toString()
            )
            .closeDialog()
            .isInInitialState()
    }

    @Test
    fun shouldKeepFieldValuesUponIncorrectSubmission() {
        val unknownCardError = "7687655651111111113"
        simulateErrorResponse(unknownCardError)

        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = unknownCardError, cvc = amexCvc, expiryDate = "1299")
            .clickSubmitButton()
            .requestIsInProgress()
            .hasErrorDialogWithMessage(
                "bodyDoesNotMatchSchema : The json body provided does not match the expected schema"
            )
            .closeDialog()
            .isInErrorState(pan = unknownCardError, cvc = amexCvc, expiryDate = "12/99")
    }

    @Test
    fun shouldKeepFieldValuesUponIncorrectSubmission_withToggleOn() {
        val unknownCardError = "7687655651111111113"
        simulateErrorResponse(unknownCardError)

        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = unknownCardError, cvc = amexCvc, expiryDate = "1299")
            .setPaymentsCvcSwitchState(checked = true)
            .clickSubmitButton()
            .requestIsInProgress()
            .hasErrorDialogWithMessage(
                "bodyDoesNotMatchSchema : The json body provided does not match the expected schema"
            )
            .closeDialog()
            .isInErrorState(pan = unknownCardError, cvc = amexCvc, expiryDate = "12/99")
            .paymentsCvcSessionCheckedState(checked = true)
    }

    @Test
    fun shouldContinueWithServiceCall_whenAppIsRotated_afterSubmission() {
        simulateDelayedResponse(activityRule.activity, amexCard)

        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = amexCard, cvc = amexCvc, expiryDate = "1299")
            .clickSubmitButton()

        // rotate landscape
        rotateLandscape(activityRule)

        cardFragmentTestUtils.requestIsInProgress()

        CardFragmentTestUtils(activityRule)
            .hasResponseDialogWithMessage(
                mapOf(CARD to activityRule.activity.getString(R.string.verified_token_session_reference)).toString()
            )
            .closeDialog()
            .isInInitialState()
    }

    @Test
    fun shouldContinueWithServiceCall_whenAppIsReopened_afterSubmission() {
        simulateDelayedResponse(activityRule.activity, amexCard)

        CardFragmentTestUtils(activityRule)
            .isInInitialState()
            .enterCardDetails(pan = amexCard, cvc = amexCvc, expiryDate = "1299")
            .clickSubmitButton()

        reopenApp()

        CardFragmentTestUtils(activityRule)
            .hasResponseDialogWithMessage(
                mapOf(CARD to activityRule.activity.getString(R.string.verified_token_session_reference)).toString()
            )
            .closeDialog()
            .isInInitialState()
    }

    @Test
    fun shouldReturnVerifiedTokenAndPaymentCvcTokenAndResetToOffOnSuccess_whenToggleIsOn() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = "4111111111111111", cvc = "123", expiryDate = "1140")
            .cardDetailsAre(pan = "4111111111111111", cvc = "123", expiryDate = "11/40")
            .hasBrand(CardBrand.VISA)
            .validationStateIs(pan = true, cvc = true, expiryDate = true)
            .enabledStateIs(submitButton = true)
            .setPaymentsCvcSwitchState(checked = true)
            .clickSubmitButton()
            .hasResponseDialogWithMessage(
                mapOf(
                    CVC to activityRule.activity.getString(R.string.payments_cvc_session_reference),
                    CARD to activityRule.activity.getString(R.string.verified_token_session_reference)
                ).toString()
            )
            .closeDialog()
            .isInInitialState()
    }

    private fun simulateDelayedResponse(context: Context, pan: String, delay: Int = 10000) {
        stubFor(
            post(urlEqualTo("/$VERIFIED_TOKENS_SESSIONS_PATH"))
                .withHeader("Accept", equalTo(VERIFIED_TOKENS_MEDIA_TYPE))
                .withHeader("Content-Type", containing(VERIFIED_TOKENS_MEDIA_TYPE))
                .withRequestBody(MatchesJsonPathPattern("$[?(@.cardNumber=='${pan}')]"))
                .willReturn(validResponseWithDelay(context, delay))
        )
    }

    private fun simulateErrorResponse(pan: String) {
        stubFor(
            post(urlEqualTo("/$VERIFIED_TOKENS_SESSIONS_PATH"))
                .withHeader("Accept", equalTo(VERIFIED_TOKENS_MEDIA_TYPE))
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

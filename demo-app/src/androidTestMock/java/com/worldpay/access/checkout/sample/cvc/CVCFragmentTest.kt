package com.worldpay.access.checkout.sample.cvc

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.worldpay.access.checkout.sample.testutil.UITestUtils.onCvcOnlyCvcView
import com.worldpay.access.checkout.sample.testutil.UITestUtils.reopenApp
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotateLandscape
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CVCFragmentTest : AbstractCvcFragmentTest() {

    @Test
    fun shouldHaveAllElementsInExpectedState() {
        cvcFragmentTestUtils
            .isInInitialState()
            .enabledStateIs(cvc = true, submitButton = false)
    }

    @Test
    fun shouldKeepDisabledSubmitButtonOn1and2digitsEntered() {
        cvcFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvc = "1")
            .cardDetailsAre(cvc = "1")
            .enabledStateIs(submitButton = false)
            .enterCardDetails(cvc = "12")
            .cardDetailsAre(cvc = "12")
            .enabledStateIs(submitButton = false)
    }

    @Test
    fun shouldEnableSubmitButtonOn3or4digitsEntered() {
        cvcFragmentTestUtils
            .isInInitialState()
            .enabledStateIs(submitButton = false)
            .enterCardDetails(cvc = "123")
            .cardDetailsAre(cvc = "123")
            .enabledStateIs(submitButton = true)
            .enterCardDetails(cvc = "1234")
            .cardDetailsAre(cvc = "1234")
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldIgnoreLettersInCvcInput() {
        cvcFragmentTestUtils.isInInitialState()

        onCvcOnlyCvcView()
            .perform(click(), typeText("sdovidsiv23odfvj0d"))

        cvcFragmentTestUtils
            .cardDetailsAre(cvc = "230")
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldKeepStateOnRotationAfterEnteringValidCVC() {
        cvcFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvc = "1111")
            .cardDetailsAre(cvc = "1111")
            .enabledStateIs(submitButton = true)

        // rotate landscape
        rotateLandscape(activityRule)

        CvcFragmentTestUtils(activityRule)
            .cardDetailsAre(cvc = "1111")
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldKeepStateOnRotationAfterEnteringInvalidCVC() {
        cvcFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvc = "11")
            .cardDetailsAre(cvc = "11")
            .enabledStateIs(submitButton = false)

        // rotate landscape
        rotateLandscape(activityRule)

        CvcFragmentTestUtils(activityRule)
            .cardDetailsAre(cvc = "11")
            .enabledStateIs(submitButton = false)
    }

    @Test
    fun shouldKeepValidationStateOnFieldsWhenAppIsReopened() {
        restartApp()

        try {
            cvcFragmentTestUtils
                .isInInitialState()
                .enterCardDetails(cvc = "12")
                .focusOff()
                .validationStateIs(false)
                .enabledStateIs(submitButton = false)

            reopenApp()

            cvcFragmentTestUtils
                .validationStateIs(false)
                .enabledStateIs(submitButton = false)
        } finally {
            restartApp()
        }
    }

    @Test
    fun shouldOnlyKeepMaxLengthUponPastingLengthyValue() {
        cvcFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvc = "11111111111111")
            .cardDetailsAre(cvc = "1111")
            .enabledStateIs(submitButton = true)
    }
}

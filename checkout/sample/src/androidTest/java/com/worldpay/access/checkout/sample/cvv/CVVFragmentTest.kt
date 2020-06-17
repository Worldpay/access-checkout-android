package com.worldpay.access.checkout.sample.cvv

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.cvv.testutil.AbstractCvvFragmentTest
import com.worldpay.access.checkout.sample.cvv.testutil.CvvFragmentTestUtils
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotateLandscape
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CVVFragmentTest: AbstractCvvFragmentTest() {

    @Test
    fun shouldHaveAllElementsInExpectedState() {
        cvvFragmentTestUtils
            .isInInitialState()
            .enabledStateIs(cvv = true, submitButton = false)
    }

    @Test
    fun shouldKeepDisabledSubmitButtonOn1and2digitsEntered() {
        cvvFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvv = "1")
            .cardDetailsAre(cvv = "1")
            .enabledStateIs(submitButton = false)
            .enterCardDetails(cvv = "12")
            .cardDetailsAre(cvv = "12")
            .enabledStateIs(submitButton = false)
    }

    @Test
    fun shouldEnableSubmitButtonOn3or4digitsEntered() {
        cvvFragmentTestUtils
            .isInInitialState()
            .enabledStateIs(submitButton = false)
            .enterCardDetails(cvv = "123")
            .cardDetailsAre(cvv = "123")
            .enabledStateIs(submitButton = true)
            .enterCardDetails(cvv = "1234")
            .cardDetailsAre(cvv = "1234")
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldIgnoreLettersInCvvInput() {
        cvvFragmentTestUtils.isInInitialState()

        onView(withId(R.id.cvv_flow_text_cvv))
            .perform(click(), typeText("sdovidsiv23odfvj0d"))

        cvvFragmentTestUtils
            .cardDetailsAre(cvv = "230")
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldKeepStateOnRotationAfterEnteringValidCVV() {
        cvvFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvv = "1111")
            .cardDetailsAre(cvv = "1111")
            .enabledStateIs(submitButton = true)

        // rotate landscape
        rotateLandscape(activityRule)

        CvvFragmentTestUtils(activityRule)
            .cardDetailsAre(cvv = "1111")
            .enabledStateIs(submitButton = true)
    }

    @Test
    fun shouldKeepStateOnRotationAfterEnteringInvalidCVV() {
        cvvFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvv = "11")
            .cardDetailsAre(cvv = "11")
            .enabledStateIs(submitButton = false)

        // rotate landscape
        rotateLandscape(activityRule)

        CvvFragmentTestUtils(activityRule)
            .cardDetailsAre(cvv = "11")
            .enabledStateIs(submitButton = false)
    }
    
    @Test
    fun shouldOnlyKeepMaxLengthUponPastingLengthyValue() {
        cvvFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(cvv = "11111111111111")
            .cardDetailsAre(cvv = "1111")
            .enabledStateIs(submitButton = true)
    }
    
}

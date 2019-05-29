package com.worldpay.access.checkout

import android.content.pm.ActivityInfo
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.worldpay.access.checkout.MockServer.defaultStubMappings
import com.worldpay.access.checkout.MockServer.simulateDelayedResponse
import com.worldpay.access.checkout.MockServer.simulateErrorResponse
import com.worldpay.access.checkout.MockServer.simulateHttpRedirect
import com.worldpay.access.checkout.UITestUtils.assertDisplaysResponseFromServer
import com.worldpay.access.checkout.UITestUtils.assertFieldsAlpha
import com.worldpay.access.checkout.UITestUtils.assertInProgressState
import com.worldpay.access.checkout.UITestUtils.assertValidInitialUIFields
import com.worldpay.access.checkout.UITestUtils.cardNumberMatcher
import com.worldpay.access.checkout.UITestUtils.checkSubmitInState
import com.worldpay.access.checkout.UITestUtils.cvvMatcher
import com.worldpay.access.checkout.UITestUtils.monthMatcher
import com.worldpay.access.checkout.UITestUtils.typeFormInputs
import com.worldpay.access.checkout.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.UITestUtils.updateCVVDetails
import com.worldpay.access.checkout.UITestUtils.yearMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class SessionRequestMockIntegrationTest {

    private val amexCard = "343434343434343"
    private val amexCvv = "1234"
    private val month = "12"
    private val year = "99"

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup() {
        defaultStubMappings(activityRule.activity)
    }

    @Test
    fun givenDiscoveryIsDoneOnStartUp_AndValidDataIsInsertedAndUserPressesSubmit_UiFieldsWillBeDisabledAndUiDisplaysResponse() {
        assertValidInitialUIFields()
        typeFormInputs(amexCard, amexCvv, month, year)
        assertFieldsAlpha(1.0f)
        uiObjectWithId(R.id.submit).exists()
        uiObjectWithId(R.id.submit).click()

        assertInProgressState()

        assertDisplaysResponseFromServer(activityRule.activity.getString(R.string.session_reference), activityRule.activity.window.decorView)
    }

    @Test
    fun givenRequestIsRedirected_AndValidDataIsInsertedAndUserPressesSubmit_UiFieldsWillBeDisabledAndUiDisplaysResponse() {
        simulateHttpRedirect(activityRule.activity)

        assertValidInitialUIFields()
        typeFormInputs(amexCard, amexCvv, month, year)
        assertFieldsAlpha(1.0f)
        uiObjectWithId(R.id.submit).exists()
        uiObjectWithId(R.id.submit).click()

        assertInProgressState()

        assertDisplaysResponseFromServer(activityRule.activity.getString(R.string.session_reference), activityRule.activity.window.decorView)
    }

    @Test
    fun givenValidDataIsInsertedAndUserPressesSubmitAndAnyResponseIsReceived_UiFieldsWillBeReEnabled() {
        assertValidInitialUIFields()
        typeFormInputs(amexCard, amexCvv, month, year)
        assertFieldsAlpha(1.0f)

        uiObjectWithId(R.id.submit).exists()
        uiObjectWithId(R.id.submit).click()
        assertInProgressState()

        assertFieldsAlpha(1.0f)
    }

    @Test
    fun givenValidDataIsInsertedAndUserPressesSubmitAndValidResponseIsReceived_UiFieldsWillBeClearedAndFormWillBeRevalidated() {
        assertValidInitialUIFields()
        typeFormInputs(amexCard, amexCvv, month, year)
        assertFieldsAlpha(1.0f)

        uiObjectWithId(R.id.submit).exists()
        uiObjectWithId(R.id.submit).click()
        assertInProgressState()

        assertFieldsAlpha(1.0f)
        onView(cardNumberMatcher)
            .check(ViewAssertions.matches(withText("")))
        onView(cvvMatcher)
            .check(ViewAssertions.matches(withText("")))
        onView(monthMatcher)
            .check(ViewAssertions.matches(withText("")))
        onView(yearMatcher)
            .check(ViewAssertions.matches(withText("")))

        typeFormInputs(amexCard, "", month, year)

        checkSubmitInState(enabled = false)
    }

    @Test
    fun givenDataIsInsertedAndUserPressesSubmitAndInvalidResponseIsReceived_UiFieldsWillBeKeptAndFormCanBeResubmittedAfterValidChanges() {
        simulateErrorResponse(activityRule.activity)

        val unknownCardError = getResourceString(R.string.error_response_card_number)
        val expectedToastErrorMessage = "Error: The json body provided does not match the expected schema"
        assertValidInitialUIFields()
        typeFormInputs(unknownCardError, amexCvv, month, year)
        assertFieldsAlpha(1.0f)

        uiObjectWithId(R.id.submit).exists()
        uiObjectWithId(R.id.submit).click()
        assertInProgressState()

        assertFieldsAlpha(1.0f)

        onView(cardNumberMatcher)
            .check(ViewAssertions.matches(withText(unknownCardError)))
        onView(cvvMatcher)
            .check(ViewAssertions.matches(withText(amexCvv)))
        onView(monthMatcher)
            .check(ViewAssertions.matches(withText(month)))
        onView(yearMatcher)
            .check(ViewAssertions.matches(withText(year)))

        assertDisplaysResponseFromServer(expectedToastErrorMessage, activityRule.activity.window.decorView)

        checkSubmitInState(enabled = true)
        updateCVVDetails("123")
        checkSubmitInState(enabled = true)
    }

    @Test
    fun givenValidDataIsInserted_AndUserPressesSubmit_AndScreenRotationPerformed_ThenAppShouldSurviveAndContinueToMakeCallToService() {
        simulateDelayedResponse(activityRule.activity)

        val cardNumber = getResourceString(R.string.long_delay_card_number)
        assertValidInitialUIFields()
        typeFormInputs(cardNumber, amexCvv, month, year)
        assertFieldsAlpha(1.0f)
        uiObjectWithId(R.id.submit).exists()
        uiObjectWithId(R.id.submit).click()
        activityRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        assertInProgressState()

        assertDisplaysResponseFromServer(activityRule.activity.getString(R.string.session_reference), activityRule.activity.window.decorView)
    }

    private fun getResourceString(resId: Int): String =
        activityRule.activity.applicationContext.resources.getString(resId)


}
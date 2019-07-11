package com.worldpay.access.checkout

import android.widget.EditText
import android.widget.ImageView
import com.worldpay.access.checkout.UITestUtils.assertUiObjectExistsAndIsDisabled
import com.worldpay.access.checkout.UITestUtils.assertUiObjectExistsAndIsEnabled
import com.worldpay.access.checkout.UITestUtils.cardNumberMatcher
import com.worldpay.access.checkout.UITestUtils.closeKeyboard
import com.worldpay.access.checkout.UITestUtils.moveToField
import com.worldpay.access.checkout.UITestUtils.rotateToLandscapeAndWait
import com.worldpay.access.checkout.UITestUtils.rotateToPortraitAndWait
import com.worldpay.access.checkout.UITestUtils.updateCVVDetails
import com.worldpay.access.checkout.UITestUtils.updateMonthDetails
import com.worldpay.access.checkout.UITestUtils.updatePANDetails
import com.worldpay.access.checkout.UITestUtils.updateYearDetails
import com.worldpay.access.checkout.UITestUtils.yearMatcher
import com.worldpay.access.checkout.matchers.BrandVectorImageMatcher
import com.worldpay.access.checkout.views.resIdByName
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CardConfigurationRotationIntegrationTest: AbstractUITest() {

    private val timeoutInMillis = 3000L

    private val luhnValidMastercardCard = "5555555555554444"
    private val luhnInvalidMastercardCard = "55555555555111"
    private val unknownCvv = "12"
    private val month = "12"
    private val year = "99"

    fun activity(): MainActivity = activityRule.activity

    @Test
    fun givenScreenIsRotated_ThenFieldsShouldKeepValidationState() {
        assertExpectedLogo("card_unknown_logo")

        // Re-enter a luhn valid, mastercard identified card and valid date
        updatePANDetails(luhnInvalidMastercardCard)
        updateMonthDetails("13")
        updateYearDetails(year)
        updateCVVDetails(unknownCvv)
        assertExpectedLogo("card_mastercard_logo")

        moveToField(yearMatcher)

        assertViewState(failColor(), failColor(), failColor(), failColor())
        assertUiObjectExistsAndIsDisabled(R.id.submit)

        //rotate and assert state is the same
        fun assertionCondition1() = assertFieldConditions(failColor(), failColor(), failColor(), failColor(), "card_mastercard_logo")
        rotateToLandscapeAndWait(activity(), timeoutInMillis, ::assertionCondition1)

        assertUiObjectExistsAndIsDisabled(R.id.submit)

        // Re-enter a luhn valid, mastercard identified card and valid date
        updatePANDetails(luhnValidMastercardCard)
        updateMonthDetails(month)
        updateCVVDetails("123")
        assertExpectedLogo("card_mastercard_logo")
        assertUiObjectExistsAndIsEnabled(R.id.submit)

        assertViewState(successColor(), successColor(), successColor(), successColor())

        fun assertionCondition2() = assertFieldConditions(successColor(), successColor(), successColor(), successColor(), "card_mastercard_logo")
        rotateToPortraitAndWait(activity(), timeoutInMillis, ::assertionCondition2)

        // Verify that all the fields are now in a success state and can be submitted
        closeKeyboard()
        assertViewState(successColor(), successColor(), successColor(), successColor())
        assertUiObjectExistsAndIsEnabled(R.id.submit)

        updateCVVDetails("12")
        moveToField(cardNumberMatcher)
        assertUiObjectExistsAndIsDisabled(R.id.submit)

        //rotate and assert state is the same
        fun assertionCondition3() = assertFieldConditions(successColor(), failColor(), successColor(), successColor(), "card_mastercard_logo")
        rotateToLandscapeAndWait(activity(), timeoutInMillis, ::assertionCondition3)

        assertUiObjectExistsAndIsDisabled(R.id.submit)

        updateCVVDetails("123")
        assertUiObjectExistsAndIsEnabled(R.id.submit)
    }

    private fun cardEditText(): EditText = activity().findViewById(R.id.card_number_edit_text)
    private fun cvvEditText(): EditText = activity().findViewById(R.id.cardCVVText)
    private fun monthEditText(): EditText = activity().findViewById(R.id.month_edit_text)
    private fun yearEditText(): EditText = activity().findViewById(R.id.year_edit_text)
    private fun failColor() = activity().getColor(R.color.FAIL)
    private fun successColor() = activity().getColor(R.color.SUCCESS)

    private fun assertFieldConditions(expectedPANColor: Int, expectedCVVColor: Int, expectedMonthColor: Int,
                                      expectedYearColor: Int, logoResName: String): Boolean {
        return try {
            assertViewState(expectedPANColor, expectedCVVColor, expectedMonthColor, expectedYearColor)
            assertExpectedLogo(logoResName)
            true
        } catch (ex: AssertionError) {
            false
        } catch (ex: Exception) {
            false
        }
    }

    private fun assertViewState(expectedPANColor: Int, expectedCVVColor: Int, expectedMonthColor: Int,
                                expectedYearColor: Int) {
        assertEquals(expectedPANColor, cardEditText().currentTextColor)
        assertEquals(expectedMonthColor, monthEditText().currentTextColor)
        assertEquals(expectedYearColor, yearEditText().currentTextColor)
        assertEquals(expectedCVVColor, cvvEditText().currentTextColor)
    }

    private fun assertExpectedLogo(logoResName: String) {
        val logoView = activity().findViewById<ImageView>(R.id.logo_view)
        val expectedLogoResourceId = activity().resIdByName(logoResName, "drawable")
        assertTrue { BrandVectorImageMatcher.equalsBrandVectorImage(logoView, expectedLogoResourceId) }
    }
}
package com.worldpay.access.checkout

import android.widget.EditText
import com.worldpay.access.checkout.AbstractUITest.CardBrand.MASTERCARD
import com.worldpay.access.checkout.UITestUtils.assertBrandImage
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
import org.junit.Test
import kotlin.test.assertEquals

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
        assertBrandImage(R.drawable.card_unknown_logo)

        // Re-enter a luhn valid, mastercard identified card and valid date
        updatePANDetails(luhnInvalidMastercardCard)
        updateMonthDetails("13")
        updateYearDetails(year)
        updateCVVDetails(unknownCvv)
        assertBrandImage(MASTERCARD)

        moveToField(yearMatcher)

        assertViewState(failColor(), failColor(), failColor(), failColor())
        assertUiObjectExistsAndIsDisabled(R.id.submit)

        //rotate and assert state is the same
        fun assertionCondition1() = assertFieldConditions(failColor(), failColor(), failColor(), failColor(), MASTERCARD)
        rotateToLandscapeAndWait(activity(), timeoutInMillis, ::assertionCondition1)

        assertUiObjectExistsAndIsDisabled(R.id.submit)

        // Re-enter a luhn valid, mastercard identified card and valid date
        updatePANDetails(luhnValidMastercardCard)
        updateMonthDetails(month)
        updateCVVDetails("123")
        assertBrandImage(MASTERCARD)
        assertUiObjectExistsAndIsEnabled(R.id.submit)

        assertViewState(successColor(), successColor(), successColor(), successColor())

        fun assertionCondition2() = assertFieldConditions(successColor(), successColor(), successColor(), successColor(), MASTERCARD)
        rotateToPortraitAndWait(activity(), timeoutInMillis, ::assertionCondition2)

        // Verify that all the fields are now in a success state and can be submitted
        closeKeyboard()
        assertViewState(successColor(), successColor(), successColor(), successColor())
        assertUiObjectExistsAndIsEnabled(R.id.submit)

        updateCVVDetails("12")
        moveToField(cardNumberMatcher)
        assertUiObjectExistsAndIsDisabled(R.id.submit)

        //rotate and assert state is the same
        fun assertionCondition3() = assertFieldConditions(successColor(), failColor(), successColor(), successColor(), MASTERCARD)
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
                                      expectedYearColor: Int, cardBrand: CardBrand): Boolean {
        return try {
            assertViewState(expectedPANColor, expectedCVVColor, expectedMonthColor, expectedYearColor)
            assertBrandImage(cardBrand)
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
}
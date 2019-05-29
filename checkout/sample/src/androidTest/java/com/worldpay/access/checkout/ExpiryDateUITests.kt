package com.worldpay.access.checkout

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v4.content.res.ResourcesCompat
import com.worldpay.access.checkout.EditTextColorMatcher.Companion.withEditTextColor
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@LargeTest
@RunWith(AndroidJUnit4::class)
class ExpiryDateUITests {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun givenAppLaunches_ThenExpiryDateExists() {
        onView(withId(R.id.cardExpiryText))
            .check(matches(isDisplayed()))

        onView(withId(R.id.month_edit_text))
            .check(matches(isDisplayed()))

        onView(withId(R.id.year_edit_text))
            .check(matches(isDisplayed()))
    }

    @Test
    fun givenValidDates_ThenFieldShouldBeValid() {
        closeSoftKeyboard()
        val months: Array<String> = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")

        enterYear(getCurrentYearWithOffset(1))
        months.forEach {
            enterMonth(it)
            onView(withId(R.id.card_number_edit_text))
                .perform(click(), closeSoftKeyboard())
            validateFullExpiryField(true)
        }
    }

    @Test
    fun givenInvalidMonthAsThirteen_ThenMonthFieldShouldBeInvalid() {
        closeSoftKeyboard()
        enterMonth("13")
        closeSoftKeyboard()
        validateMonthField(false)
    }

    @Test
    fun givenInvalidMonthAsZero_ThenMonthFieldShouldBeInvalid() {
        closeSoftKeyboard()
        enterMonth("00")
        closeSoftKeyboard()
        validateMonthField(false)
        closeSoftKeyboard()
    }

    @Test
    fun givenIncompleteMonth_ThenMonthFieldShouldBeInvalidOnLostFocus() {
        closeSoftKeyboard()
        enterMonth("1")
        closeSoftKeyboard()
        validateMonthField(true)
        closeSoftKeyboard()
        moveToYear()
        closeSoftKeyboard()
        validateMonthField(false)
        closeSoftKeyboard()
    }

    @Test
    fun givenInvalidYear_ThenYearFieldShouldBeInvalid() {
        closeSoftKeyboard()
        enterYear(getCurrentYearWithOffset(-1))
        validateYearField(false)
        closeSoftKeyboard()
    }

    @Test
    fun givenValidYear_AndEmptyMonth_ThenYearFieldShouldBeValidUntilLosesFocus() {
        closeSoftKeyboard()
        enterYear(getCurrentYearWithOffset(0))
        closeSoftKeyboard()
        validateYearField(true)
        closeSoftKeyboard()
        moveToCardNumberField()
        closeSoftKeyboard()
        validateFullExpiryField(false)
    }

    @Test
    fun givenValidMonth_AndEmptyYear_ThenMonthFieldShouldBeValidUntilLosesFocus() {
        closeSoftKeyboard()
        enterMonth("01")
        validateMonthField(true)
        moveToCardNumberField()
        validateFullExpiryField(false)
    }

    @Test
    fun givenIncompleteYear_ThenYearFieldShouldBeInvalidOnLostFocus() {
        closeSoftKeyboard()
        enterYear(getCurrentYearWithOffset(1).substring(0, 1))
        validateYearField(true)
        moveToMonth()
        validateYearField(false)
    }

    @Test
    fun givenIncompleteMonth_ThenMonthAndYearShouldBeInvalidOnLostFocus() {
        enterYear(getCurrentYearWithOffset(1))
        validateYearField(true)
        enterMonth("0")
        validateMonthField(true)
        moveToCardNumberField()
        validateFullExpiryField(false)
    }

    @Test
    fun givenIncompleteYear_ThenMonthAndYearShouldBeInvalidOnLostFocus() {
        closeSoftKeyboard()
        enterMonth("01")
        closeSoftKeyboard()
        validateMonthField(true)
        closeSoftKeyboard()
        enterYear(getCurrentYearWithOffset(1).substring(0, 1))
        closeSoftKeyboard()
        validateYearField(true)
        closeSoftKeyboard()
        moveToCardNumberField()
        closeSoftKeyboard()
        validateFullExpiryField(false)
    }

    @Test
    fun givenValidMonth_AndInvalidYear_ThenFieldShouldBeInvalidUponHittingLastDigit() {
        closeSoftKeyboard()
        enterMonth("01")
        validateMonthField(true)
        enterYear(getCurrentYearWithOffset(-1))
        validateFullExpiryField(false)
    }

    @Test
    fun givenValidYear_AndInvalidMonth_ThenFieldShouldBeInvalidUponHittingLastDigit() {
        closeSoftKeyboard()
        enterYear(getCurrentYearWithOffset(1))
        validateYearField(true)
        enterMonth("13")
        validateFullExpiryField(false)
    }

    @Test
    fun givenUserAttemptsToTypeInMoreThanAllowedDigitsForMonthAndYear_ThenShouldRestrictToValidLength() {
        closeSoftKeyboard()
        enterMonth("012", true)
        onView(withId(R.id.month_edit_text))
            .check(matches(withText("01")))
        enterYear("012", true)
        onView(withId(R.id.year_edit_text))
            .check(matches(withText("01")))
    }

    private fun enterMonth(month: String, typeText: Boolean = false) {
        closeSoftKeyboard()
        val typeAction = if (typeText) typeText(month) else replaceText(month)
        onView(withId(R.id.month_edit_text))
            .check(matches(isDisplayed()))
            .perform(click(), typeAction, closeSoftKeyboard())
    }

    private fun enterYear(year: String, typeText: Boolean = false) {
        closeSoftKeyboard()
        val typeAction = if (typeText) typeText(year) else replaceText(year)
        onView(withId(R.id.year_edit_text))
            .check(matches(isDisplayed()))
            .perform(click(), typeAction, closeSoftKeyboard())
    }

    private fun validateFullExpiryField(shouldBeValid: Boolean) {
        val expectedColor = when (shouldBeValid) {
            true -> getSuccessColor()
            false -> getFailColor()
        }

        onView(withId(R.id.month_edit_text))
            .check(matches(withEditTextColor(expectedColor)))

        onView(withId(R.id.year_edit_text))
            .check(matches(withEditTextColor(expectedColor)))
    }

    private fun moveToYear() {
        onView(withId(R.id.year_edit_text))
            .perform(click(), closeSoftKeyboard())
    }

    private fun moveToMonth() {
        onView(withId(R.id.month_edit_text))
            .perform(click(), closeSoftKeyboard())
    }

    private fun moveToCardNumberField() {
        onView(withId(R.id.card_number_edit_text))
            .perform(click(), closeSoftKeyboard())
    }


    private fun validateMonthField(shouldBeValid: Boolean) {
        val expectedMonthColor = when (shouldBeValid) {
            true -> getSuccessColor()
            false -> getFailColor()
        }

        onView(withId(R.id.month_edit_text))
            .check(matches(withEditTextColor(expectedMonthColor)))
    }

    private fun validateYearField(shouldBeValid: Boolean) {
        val expectedYearColor = when (shouldBeValid) {
            true -> getSuccessColor()
            false -> getFailColor()
        }

        onView(withId(R.id.year_edit_text))
            .check(matches(withEditTextColor(expectedYearColor)))
    }

    private fun getSuccessColor() =
        ResourcesCompat.getColor(
            this@ExpiryDateUITests.activityRule.activity.resources,
            R.color.SUCCESS,
            this@ExpiryDateUITests.activityRule.activity.theme
        )

    private fun getFailColor() =
        ResourcesCompat.getColor(
            this@ExpiryDateUITests.activityRule.activity.resources,
            R.color.FAIL,
            this@ExpiryDateUITests.activityRule.activity.theme
        )

    private fun getCurrentYearWithOffset(offset: Int): String {

        return (Calendar.getInstance().get(Calendar.YEAR)+offset).toString().substring(2)
    }

}
package com.worldpay.access.checkout.card

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiSelector
import com.worldpay.access.checkout.AbstractUITest
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.testutil.matchers.EditTextColorMatcher
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CardSessionRequestTest: AbstractUITest() {

    private lateinit var cardNumberMatcher: Matcher<View>
    private lateinit var cvvMatcher: Matcher<View>
    private lateinit var cardExpiryMatcher: Matcher<View>
    private lateinit var buttonMatcher: Matcher<View>
    private lateinit var monthMatcher: Matcher<View>
    private lateinit var yearMatcher: Matcher<View>
    private lateinit var progressMatcher: Matcher<View>

    @Before
    fun setupMatchers() {
        cardNumberMatcher = withId(R.id.card_number_edit_text)
        cvvMatcher = withId(R.id.card_flow_text_cvv)
        cardExpiryMatcher = withId(R.id.card_flow_text_exp)
        buttonMatcher = withId(R.id.card_flow_btn_submit)
        monthMatcher = withId(R.id.month_edit_text)
        yearMatcher = withId(R.id.year_edit_text)
        progressMatcher = withId(R.id.loading_bar)
    }

    @Test
    fun submitButton_exists() {
        onView(buttonMatcher)
            .check(matches(isDisplayed()))

    }

    @Test
    fun givenAppHasJustBeenLaunched_ThenSubmitButtonShouldBeDisabledByDefault() {
        onView(buttonMatcher)
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))
    }

    @Test
    fun givenValidFields_ThenSubmitButtonShouldBeEnabled() {
        onView(buttonMatcher)
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        onView(cardNumberMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("5555555555554444"), closeSoftKeyboard())

        onView(cvvMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("123"), closeSoftKeyboard())

        onView(monthMatcher)
            .check(matches(isDisplayed()))
            .perform(click(), replaceText("12"), closeSoftKeyboard())

        onView(yearMatcher)
            .check(matches(isDisplayed()))
            .perform(click(), replaceText("20"), closeSoftKeyboard())

        onView(buttonMatcher)
            .check(matches(isEnabled()))
    }

    @Test
    fun givenValidFieldsEnteredThenCardFieldIsChangedToBeInvalid_ThenSubmitButtonShouldBeDisabled() {
        closeSoftKeyboard()
        onView(buttonMatcher)
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        onView(cardNumberMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("5555555555554444"), closeSoftKeyboard())

        onView(cvvMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("123"), closeSoftKeyboard())

        onView(monthMatcher)
            .check(matches(isDisplayed()))
            .perform(click(), replaceText("12"), closeSoftKeyboard())

        onView(yearMatcher)
            .check(matches(isDisplayed()))
            .perform(click(), replaceText("20"), closeSoftKeyboard())

        onView(buttonMatcher)
            .check(matches(isEnabled()))

        onView(cardNumberMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("5555555555554446"), closeSoftKeyboard())
            .check(matches(EditTextColorMatcher.withEditTextColor(getFailColor())))

        onView(buttonMatcher)
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

    }

    @Test
    fun givenValidFieldsEnteredThenCardFieldIsChangedToBePartial_ThenSubmitButtonShouldBeDisabled() {
        onView(buttonMatcher)
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        onView(cardNumberMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("5555555555554444"), closeSoftKeyboard())

        onView(cvvMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("123"), closeSoftKeyboard())

        onView(monthMatcher)
            .check(matches(isDisplayed()))
            .perform(click(), replaceText("12"), closeSoftKeyboard())

        onView(yearMatcher)
            .check(matches(isDisplayed()))
            .perform(click(), replaceText("20"), closeSoftKeyboard())

        onView(buttonMatcher)
            .check(matches(isEnabled()))

        onView(cardNumberMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("55555555555544"))
            .check(matches(EditTextColorMatcher.withEditTextColor(getSuccessColor())))

        onView(buttonMatcher)
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

    }

    @Test
    fun givenValidFieldsEnteredThenCVVIsChangedToPartial_ThenSubmitShouldBeDisabled() {
        onView(buttonMatcher)
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        onView(cardNumberMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("5555555555554444"), closeSoftKeyboard())

        onView(cvvMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("123"), closeSoftKeyboard())

        onView(monthMatcher)
            .check(matches(isDisplayed()))
            .perform(click(), replaceText("12"), closeSoftKeyboard())

        onView(yearMatcher)
            .check(matches(isDisplayed()))
            .perform(click(), replaceText("20"), closeSoftKeyboard())

        onView(buttonMatcher)
            .check(matches(isEnabled()))

        onView(cardNumberMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("55555555555544"), closeSoftKeyboard())
            .check(matches(EditTextColorMatcher.withEditTextColor(getSuccessColor())))

        onView(buttonMatcher)
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        onView(cvvMatcher)
            .perform(click(), replaceText("9"), closeSoftKeyboard())

        onView(buttonMatcher)
            .check(matches(not(isEnabled())))
    }

    @Test
    fun givenValidFieldsEnteredThenDateIsChangedToPartial_ThenSubmitShouldBeDisabled() {
        onView(buttonMatcher)
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        onView(cardNumberMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("5555555555554444"), closeSoftKeyboard())

        onView(cvvMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), typeText("123"), closeSoftKeyboard())

        onView(monthMatcher)
            .check(matches(isDisplayed()))
            .perform(click(), replaceText("12"), closeSoftKeyboard())

        onView(yearMatcher)
            .check(matches(isDisplayed()))
            .perform(click(), replaceText("20"), closeSoftKeyboard())

        onView(buttonMatcher)
            .check(matches(isEnabled()))

        onView(cardNumberMatcher)
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .perform(click(), replaceText("55555555555544"), closeSoftKeyboard())
            .check(matches(EditTextColorMatcher.withEditTextColor(getSuccessColor())))

        onView(buttonMatcher)
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        onView(yearMatcher)
            .perform(click(), replaceText("17"), closeSoftKeyboard())

        onView(buttonMatcher)
            .check(matches(not(isEnabled())))
    }

    companion object {
        private fun progressBar(): UiObject =
            uiObjectWithId(
                R.id.loading_bar
            )

        private fun uiObjectWithId(resId: Int): UiObject {
            val resName = InstrumentationRegistry.getInstrumentation().targetContext.resources.getResourceName(resId)
            val selector = UiSelector().resourceId(resName)
            return UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).findObject(selector)
        }
    }

    private fun assertUiObjectExistsAndIsDisabled(resId: Int) {
        val uiObject =
            uiObjectWithId(
                resId
            )
        uiObject.exists()
        Assert.assertFalse(uiObject.isEnabled)
    }

    private fun getSuccessColor() =
        ResourcesCompat.getColor(
            this@CardSessionRequestTest.activityRule.activity.resources,
            R.color.SUCCESS,
            this@CardSessionRequestTest.activityRule.activity.theme
        )

    private fun getFailColor() =
        ResourcesCompat.getColor(
            this@CardSessionRequestTest.activityRule.activity.resources,
            R.color.FAIL,
            this@CardSessionRequestTest.activityRule.activity.theme
        )
}
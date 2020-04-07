package com.worldpay.access.checkout

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions.navigateTo
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.MockServer.defaultStubMappings
import org.junit.Before
import org.junit.Rule

abstract class AbstractUITest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup() {
        defaultStubMappings(activityRule.activity)
    }

    protected fun navigateToCVVFlow(fragmentId: Int) {
        onView(withId(R.id.drawer_layout))
            .perform(open())
            .check(matches(isOpen()))
        onView(withId(R.id.nav_view))
            .check(matches(isDisplayed()))
            .perform(navigateTo(fragmentId))
    }

}
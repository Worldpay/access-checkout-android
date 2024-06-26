package com.worldpay.access.checkout.sample.navigation

import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.close
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.testutil.UITestUtils.navigateTo
import com.worldpay.access.checkout.sample.testutil.matchers.EspressoTestMatchers.withDrawable
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationUITest {

    @get:Rule
    val activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    private val cardFlowTitle = "Card Flow"
    private val restrictedCardFlowTitle = "Restricted Card Flow"
    private val cvcFlowTitle = "CVC Flow"

    /**
     * We pause after navigating to a different view
     * This is to fix a major flakiness issue with this test suite
     * This practice is not documented but shown on this page https://github.com/operando/NavigationViewActionsSample
     */
    private val pauseAfterNavigate = 500L

    @After
    fun cleanUp() {
        activityRule.scenario.close()
    }

    @Test
    fun shouldHaveOnly2ItemsInNavigationDrawer() {
        onView(withId(R.id.drawer_layout)).perform(open()).check(matches(isOpen()))

        onView(
            allOf(
                withTagKey(R.menu.drawer_menu),
                hasSibling(withText(cardFlowTitle)),
                isDisplayed()
            )
        )
        onView(
            allOf(
                withTagKey(R.menu.drawer_menu),
                hasSibling(withText(cvcFlowTitle)),
                isDisplayed()
            )
        )

        onView(withId(R.id.drawer_layout)).perform(close()).check(matches(isClosed()))
    }

    @Test
    fun shouldLandInitiallyOnCardFlow() {
        onView(withId(R.id.fragment_card_flow))
            .check(matches(isDisplayed()))

        assertToolbarTitle(cardFlowTitle)
    }

    @Test
    fun shouldBeAbleToNavigateToCVCFlow() {
        navigateTo(R.id.nav_cvc_flow)
        waitFor(pauseAfterNavigate)

        onView(withId(R.id.fragment_cvc_flow))
            .check(matches(isDisplayed()))

        assertToolbarTitle(cvcFlowTitle)
    }

    @Test
    fun shouldBeAbleToNavigateBackToCardFlow_fromCvcFlow() {
        navigateTo(R.id.nav_cvc_flow)
        waitFor(pauseAfterNavigate)

        onView(withId(R.id.fragment_cvc_flow))
            .check(matches(isDisplayed()))

        assertToolbarTitle(cvcFlowTitle)

        navigateTo(R.id.nav_card_flow)
        waitFor(pauseAfterNavigate)

        onView(withId(R.id.fragment_card_flow))
            .check(matches(isDisplayed()))

        assertToolbarTitle(cardFlowTitle)
    }

    @Test
    fun shouldBeAbleToNavigateBackToCardFlow_fromRestrictedCardFlow() {
        navigateTo(R.id.nav_restricted_card_flow)
        waitFor(pauseAfterNavigate)

        onView(withId(R.id.fragment_restricted_card_flow))
            .check(matches(isDisplayed()))

        assertToolbarTitle(restrictedCardFlowTitle)

        navigateTo(R.id.nav_card_flow)
        waitFor(pauseAfterNavigate)

        onView(withId(R.id.fragment_card_flow))
            .check(matches(isDisplayed()))

        assertToolbarTitle(cardFlowTitle)
    }

    @Test
    fun shouldHaveExpectedLogoInNavHeader() {
        onView(withId(R.id.drawer_layout))
            .perform(open()).check(matches(isOpen()))

        onView(withId(R.id.nav_header_logo))
            .check(matches(isDisplayed()))

        onView(withId(R.id.drawer_layout))
            .perform(close()).check(matches(isClosed()))

        onView(withId(R.id.nav_header_logo))
            .check(matches(withDrawable(R.drawable.ic_worldpay_logo_white)))
            .check(matches(not(isDisplayed())))
    }

    private fun waitFor(sleepTimeInMs: Long) {
        Thread.sleep(sleepTimeInMs)
    }

    private fun assertToolbarTitle(title: String) {
        onView(allOf(instanceOf(TextView::class.java), withParent(withId(R.id.toolbar_main))))
            .check(matches(withText(title)))
    }
}

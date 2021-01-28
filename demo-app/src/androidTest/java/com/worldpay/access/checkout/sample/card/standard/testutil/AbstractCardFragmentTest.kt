package com.worldpay.access.checkout.sample.card.standard.testutil

import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.MockServer.defaultStubMappings
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotatePortrait
import org.junit.Before
import org.junit.Rule

abstract class AbstractCardFragmentTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    lateinit var cardFragmentTestUtils: CardFragmentTestUtils

    @Before
    fun setup() {
        cardFragmentTestUtils = CardFragmentTestUtils(activityRule)
        defaultStubMappings(activityRule.activity)
        closeSoftKeyboard()
        rotatePortrait(activityRule)
    }

}

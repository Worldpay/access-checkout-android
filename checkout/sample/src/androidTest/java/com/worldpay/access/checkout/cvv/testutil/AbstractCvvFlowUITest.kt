package com.worldpay.access.checkout.cvv.testutil

import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.MainActivity
import com.worldpay.access.checkout.MockServer.defaultStubMappings
import org.junit.Before
import org.junit.Rule

abstract class AbstractCvvFlowUITest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup() {
        defaultStubMappings(activityRule.activity)
    }

}
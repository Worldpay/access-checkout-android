package com.worldpay.access.checkout

import android.support.test.rule.ActivityTestRule
import com.worldpay.access.checkout.MockServer.defaultStubMappings
import org.junit.Before
import org.junit.Rule

abstract class AbstractUITest {

    enum class CardBrand(val cardBrandName: String) {
        AMEX("amex"),
        VISA("visa"),
        MASTERCARD("mastercard")
    }

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup() {
        defaultStubMappings(activityRule.activity)
    }
}
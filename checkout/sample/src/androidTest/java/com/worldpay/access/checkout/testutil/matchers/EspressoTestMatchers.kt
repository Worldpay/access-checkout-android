package com.worldpay.access.checkout.testutil.matchers

import android.view.View
import org.hamcrest.Matcher

object EspressoTestMatchers {

    fun withDrawable(resourceId: Int): Matcher<View> {
        return DrawableMatcher(
            resourceId
        )
    }

}
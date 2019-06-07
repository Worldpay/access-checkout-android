package com.worldpay.access.checkout.matchers

import android.support.test.espresso.matcher.BoundedMatcher
import android.view.View
import org.hamcrest.Description

class AlphaMatcher private constructor(private val alpha: Float): BoundedMatcher<View, View>(View::class.java){
    override fun describeTo(description: Description) {
        description.appendText("view with alpha: ").appendValue(alpha)
    }

    override fun matchesSafely(item: View?) = item?.alpha == alpha

    companion object {
        @JvmStatic
        fun withAlpha(alpha: Float) = AlphaMatcher(alpha)
    }
}

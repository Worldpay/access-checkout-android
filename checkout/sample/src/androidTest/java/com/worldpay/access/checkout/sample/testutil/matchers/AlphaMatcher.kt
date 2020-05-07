package com.worldpay.access.checkout.sample.testutil.matchers

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description

class AlphaMatcher private constructor(private val alpha: Float): BoundedMatcher<View, View>(View::class.java){
    override fun describeTo(description: Description) {
        description.appendText("view with alpha: ").appendValue(alpha)
    }

    override fun matchesSafely(item: View?) = item?.alpha == alpha

    companion object {
        @JvmStatic
        fun withAlpha(alpha: Float) =
            AlphaMatcher(alpha)
    }
}

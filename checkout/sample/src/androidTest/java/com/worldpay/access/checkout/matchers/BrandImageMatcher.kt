package com.worldpay.access.checkout.matchers

import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description

internal class BrandImageMatcher private constructor(private val id: Int) :
    BoundedMatcher<View, ImageView>(ImageView::class.java) {
    override fun matchesSafely(item: ImageView): Boolean {
        val context = item.context
        val expectedBitmap = context.getDrawable(id) as BitmapDrawable
        return (item.drawable as BitmapDrawable).bitmap.sameAs(expectedBitmap.bitmap)
    }

    override fun describeTo(description: Description) {
        description.appendText("with drawable ID:")
            .appendValue(id)
    }

    companion object {
        fun withBrandImageId(id: Int): BrandImageMatcher {
            return BrandImageMatcher(id)
        }
    }
}

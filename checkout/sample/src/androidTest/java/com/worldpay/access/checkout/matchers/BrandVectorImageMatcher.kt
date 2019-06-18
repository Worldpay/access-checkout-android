package com.worldpay.access.checkout.matchers

import android.support.test.espresso.matcher.BoundedMatcher
import android.view.View
import android.widget.ImageView
import com.worldpay.access.checkout.views.PANLayout
import org.hamcrest.Description

internal class BrandVectorImageMatcher private constructor(private val id: Int) :
    BoundedMatcher<View, ImageView>(ImageView::class.java) {
    override fun matchesSafely(item: ImageView): Boolean {
        val context = item.context
        val expectedResName = context.resources.getResourceEntryName(id)//getDrawable(id) as BitmapDrawable
        return (item.getTag(PANLayout.CARD_TAG)).equals(expectedResName)
    }

    override fun describeTo(description: Description) {
        description.appendText("with drawable ID:")
            .appendValue(id)
    }

    companion object {

        fun withBrandVectorImageId(id: Int): BrandVectorImageMatcher {
            return BrandVectorImageMatcher(id)
        }
    }
}
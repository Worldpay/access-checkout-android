package com.worldpay.access.checkout.matchers

import android.support.test.espresso.matcher.BoundedMatcher
import android.view.View
import android.widget.ImageView
import com.worldpay.access.checkout.views.PANLayout
import org.hamcrest.Description

internal class BrandVectorImageMatcher private constructor(private val expectedId: Int) :
    BoundedMatcher<View, ImageView>(ImageView::class.java) {
    override fun matchesSafely(actualItem: ImageView): Boolean {
        return equalsBrandVectorImage(actualItem, expectedId)
    }

    override fun describeTo(description: Description) {
        description.appendText("with drawable ID:")
            .appendValue(expectedId)
    }

    companion object {

        fun withBrandVectorImageId(id: Int): BrandVectorImageMatcher {
            return BrandVectorImageMatcher(id)
        }

        fun equalsBrandVectorImage(actualItem: ImageView, expectedId: Int): Boolean {
            val context = actualItem.context
            val expectedResName = context.resources.getResourceEntryName(expectedId)
            return (actualItem.getTag(PANLayout.CARD_TAG)) == expectedResName
        }
    }
}
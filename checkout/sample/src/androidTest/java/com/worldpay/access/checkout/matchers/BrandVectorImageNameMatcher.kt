package com.worldpay.access.checkout.matchers

import android.support.test.espresso.matcher.BoundedMatcher
import android.view.View
import android.widget.ImageView
import com.worldpay.access.checkout.AbstractUITest.CardBrand
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.views.PANLayout
import org.hamcrest.Description

internal class BrandVectorImageNameMatcher private constructor(private val expectedBrand: CardBrand) :
    BoundedMatcher<View, ImageView>(ImageView::class.java) {
    override fun matchesSafely(actualItem: ImageView): Boolean {

        var actualTag = ""
        return try {
            actualTag = getActualTag(actualItem)
            debugLog("BrandVectorImageNameMatcher",
                "Checking actual image tag: $actualTag matches expected: $expectedBrand")
            actualTag == expectedBrand.cardBrandName
        } catch (ex: Exception) {
            debugLog(
                "BrandVectorImageNameMatcher",
                "Actual image tag: $actualTag does not match expected: $expectedBrand"
            )
            false
        }
    }

    override fun describeTo(description: Description) {
        description.appendText("with brand name:")
            .appendValue(expectedBrand.cardBrandName)
    }

    companion object {

        fun getActualTag(actualItem: ImageView): String {
            return actualItem.getTag(PANLayout.CARD_TAG).toString()
        }

        fun withBrandVectorImageName(brand: CardBrand): BrandVectorImageNameMatcher {
            return BrandVectorImageNameMatcher(brand)
        }
    }
}
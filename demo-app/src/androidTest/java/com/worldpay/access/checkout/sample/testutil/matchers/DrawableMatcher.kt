package com.worldpay.access.checkout.sample.testutil.matchers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class DrawableMatcher(private val resourceId: Int) : TypeSafeMatcher<View>() {

    override fun describeTo(description: Description?) {
        description?.appendText("with drawable resource: $resourceId")
    }

    override fun matchesSafely(item: View): Boolean {
        if (!(item is ImageView || item is TextView)) {
            return false
        }

        val actualDrawables = mutableListOf<Drawable>()

        if (item is ImageView) {
            val actual = item.drawable
            if (actual != null) {
                actualDrawables.add(actual)
            }
        } else {
            if (item is TextView) {
                for (compoundDrawable in item.compoundDrawables) {
                    if (compoundDrawable != null) {
                        actualDrawables.add(compoundDrawable)
                    }
                }
            }
        }

        val resources = item.context.resources
        val expectedDrawable = resources.getDrawable(resourceId, null)

        if (actualDrawables.size == 0 || expectedDrawable == null) {
            return false
        }

        for (actualDrawable in actualDrawables) {
            val bounds = Rect(0, 0, 64, 64)
            expectedDrawable.bounds = bounds

            actualDrawable.bounds = bounds

            val bitmap1 = toBitmap(expectedDrawable)
            val bitmap2 = toBitmap(actualDrawable)

            if (bitmap1.sameAs(bitmap2)) {
                return true
            }
        }

        return false
    }

    private fun toBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun drawDrawableToBitmap(drawable: Drawable): Bitmap {
        val bounds = drawable.bounds
        val bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        return bitmap
    }
}

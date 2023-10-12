package com.worldpay.access.checkout.ui

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet

class AttributeValues(
    private val context: Context,
    private val attrs: AttributeSet?
) {
    companion object {
        private const val defaultNamespace = "http://schemas.android.com/apk/res/android"
    }

    internal fun stringOf(attributeName: String): String? {
        val resId = attrs?.getAttributeResourceValue(defaultNamespace, attributeName, 0)
        return if (resId != 0 && resId != null) {
            try {
                context.resources.getString(resId!!)
            } catch (e: Resources.NotFoundException) {
                null
            }
        } else {
            attrs?.getAttributeValue(defaultNamespace, attributeName)
        }
    }

    internal fun stringOfOther(attributeName: String): Int? {
        val resId = attrs?.getAttributeResourceValue(defaultNamespace, attributeName, 0)
        return if (resId != 0 && resId != null) {
            try {
                resId
            } catch (e: Resources.NotFoundException) {
                null
            }
        } else {
            resId
        }
    }
}

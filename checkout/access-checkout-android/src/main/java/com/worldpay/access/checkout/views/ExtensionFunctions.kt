package com.worldpay.access.checkout.views

import android.content.Context

fun Context.resIdByName(resIdName: String, resType: String): Int {
    return resources.getIdentifier(resIdName, resType, packageName)
}
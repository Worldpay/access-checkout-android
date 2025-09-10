package com.worldpay.access.checkout.util

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

class BuildVersionProvider {
    private fun sdkVersion() = Build.VERSION.SDK_INT

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    fun isAtLeastO() = sdkVersion() >= Build.VERSION_CODES.O

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.M)
    fun isAtLeastM() = sdkVersion() >= Build.VERSION_CODES.M

    fun currentVersion(): Int = Build.VERSION.SDK_INT
}

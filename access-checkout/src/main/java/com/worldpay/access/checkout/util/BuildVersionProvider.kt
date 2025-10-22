package com.worldpay.access.checkout.util

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

interface IBuildVersionProvider {
    val sdkInt: Int
    fun isAtLeastO(): Boolean
    fun isAtLeastM(): Boolean
}

class BuildVersionProvider(private val sdkVersion: Int) : IBuildVersionProvider {
    override val sdkInt: Int
        get() = sdkVersion

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    override fun isAtLeastO(): Boolean = sdkInt >= Build.VERSION_CODES.O

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.M)
    override fun isAtLeastM(): Boolean = sdkInt >= Build.VERSION_CODES.M
}

internal object BuildVersionProviderHolder {
    var instance: IBuildVersionProvider = BuildVersionProvider(Build.VERSION.SDK_INT)
}

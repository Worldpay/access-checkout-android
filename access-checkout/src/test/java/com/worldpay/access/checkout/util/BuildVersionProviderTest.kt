package com.worldpay.access.checkout.util

import android.os.Build
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class BuildVersionProviderTest {
    private val buildVersionProvider = BuildVersionProvider()

    @Test
    fun `currentVersion should return the current SDK version`() {
        assert(buildVersionProvider.sdkInt == Build.VERSION.SDK_INT)
    }

    @Test
    fun `isAtLeastO should return correct value when sdk version is 23`() {
        if (Build.VERSION.SDK_INT >= 23) {
            assertTrue(buildVersionProvider.isAtLeastM())
        } else {
            assertFalse(buildVersionProvider.isAtLeastM())
        }
    }

    @Test
    fun `isAtLeastO should return correct value when sdk version is 26`() {
        if (Build.VERSION.SDK_INT >= 26) {
            assertTrue(buildVersionProvider.isAtLeastO())
        } else {
            assertFalse(buildVersionProvider.isAtLeastO())
        }
    }
}

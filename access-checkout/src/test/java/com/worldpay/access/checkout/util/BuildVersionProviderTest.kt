package com.worldpay.access.checkout.util

import android.os.Build
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.intArrayOf
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class BuildVersionProviderTest {
    val buildVersionProvider = BuildVersionProvider()

    @Test
    @Config(sdk = [26])
    fun `currentVersion should return the current SDK version`() {
        assert(buildVersionProvider.currentVersion() == 26)
    }

    @Test
    @Config(sdk = [26])
    fun `isAtLeastO should return true when sdk is 26 or greater`() {
        assertTrue(buildVersionProvider.isAtLeastO())
    }
//
//    @Test
//    @Config(sdk = [25])
//    fun `isAtLeastO should return false when sdk is less than 26`() {
//        assertFalse(buildVersionProvider.isAtLeastO())
//    }
//
//    @Test
//    @Config(sdk = [23])
//    fun `isAtLeastM should return true when sdk is 23 or greater`() {
//        assertTrue(buildVersionProvider.isAtLeastM())
//    }
//
//    @Test
//    @Config(sdk = [22])
//    fun `isAtLeastM should return false when sdk is less than 23`() {
//        assertFalse(buildVersionProvider.isAtLeastM())
//    }
}

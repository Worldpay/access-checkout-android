package com.worldpay.access.checkout.util

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class BuildVersionProviderTest {

    @Test
    fun `sdkInt should return the current SDK version`() {
        val buildVersionProvider = BuildVersionProvider(23)
        assert(buildVersionProvider.sdkInt == 23)
    }

    @Test
    fun `isAtLeastM should return correct value when sdk version is 23 or greater`() {
        val buildVersionProvider = BuildVersionProvider(23)
        assertTrue(buildVersionProvider.isAtLeastM())

    }

    @Test
    fun `isAtLeastM should return correct value when sdk version is less than 23`() {
        val buildVersionProvider = BuildVersionProvider(22)
        assertFalse(buildVersionProvider.isAtLeastM())
    }

    @Test
    fun `isAtLeastO should return correct value when sdk version is 26 or greater`() {
        val buildVersionProvider = BuildVersionProvider(26)
        assertTrue(buildVersionProvider.isAtLeastO())
    }

    @Test
    fun `isAtLeastO should return correct value when sdk version is less than 26`() {
        val buildVersionProvider = BuildVersionProvider(25)
        assertFalse(buildVersionProvider.isAtLeastO())
    }

    @Test
    fun `BuildVersionProviderHolder instance can be replaced`() {
        val original = BuildVersionProviderHolder.instance
        val other = BuildVersionProvider(19)
        BuildVersionProviderHolder.instance = other
        assertEquals(19, BuildVersionProviderHolder.instance.sdkInt)
    }
}

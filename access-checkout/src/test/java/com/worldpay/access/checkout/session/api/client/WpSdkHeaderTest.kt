package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.BuildConfig
import org.junit.Test
import java.lang.RuntimeException
import kotlin.test.AfterTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class WpSdkHeaderTest {
    private val expectedErrorMessage =
        "Unsupported version format. This functionality only supports access-checkout-react-native semantic versions or default access-checkout-android version."

    @AfterTest
    fun tearDown() {
        WpSdkHeader.overrideValue(WpSdkHeader.DEFAULT_VALUE)
    }

    @Test
    fun testAllowsToOverrideVersionWithAccessCheckoutReactNativeVersion() {
        val newVersion = "access-checkout-react-native/10.3.15"

        WpSdkHeader.overrideValue(newVersion)

        assertEquals(newVersion, WpSdkHeader.value)
    }

    @Test
    fun testThrowsErrorWhenSemanticVersionIsNotInXYZFormat() {
        val newVersion = "access-checkout-react-native/10.3"

        assertFailsWith<RuntimeException>(
            message = expectedErrorMessage,
            block = {
                WpSdkHeader.overrideValue(newVersion)
            }
        )
    }

    @Test
    fun testThrowsErrorWhenAccessCheckoutReactNativeDoesNotHaveCorrectCase() {
        val newVersion = "Access-Checkout-REACT-native/10.3.15"

        assertFailsWith<RuntimeException>(
            message = expectedErrorMessage,
            block = {
                WpSdkHeader.overrideValue(newVersion)
            }
        )
    }

    @Test
    fun testThrowsErrorWhenAttemptingToOverrideWithNonAccessCheckoutReactNativeVersion() {
        val newVersion = "something-else/10.3.15"

        assertFailsWith<RuntimeException>(
            message = expectedErrorMessage,
            block = {
                WpSdkHeader.overrideValue(newVersion)
            }
        )
    }

    @Test
    fun testAllowsToOverrideVersionWithDefaultAccessCheckoutAndroidVersion() {
        val newVersion = "access-checkout-android/" + BuildConfig.VERSION_NAME

        WpSdkHeader.overrideValue(newVersion)

        assertEquals(newVersion, WpSdkHeader.value)
    }

    @Test
    fun testThrowsErrorWhenAttemptingToOverrideWithAccessCheckoutAndroidIfDifferentSemanticVersion() {
        val newVersion = "access-checkout-android/1.2.3"

        assertFailsWith<RuntimeException>(
            message = expectedErrorMessage,
            block = {
                WpSdkHeader.overrideValue(newVersion)
            }
        )
    }
}


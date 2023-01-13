package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.BuildConfig
import java.lang.RuntimeException

class WpSdkHeader private constructor() {
    companion object {
        private const val INVALID_VERSION_ERROR_MESSAGE =
            "Unsupported version format. This functionality only supports access-checkout-react-native semantic versions or default access-checkout-android version."

        private const val PRODUCT_NAME = "access-checkout-android/"
        internal const val DEFAULT_VALUE = PRODUCT_NAME + BuildConfig.VERSION_NAME

        internal const val name = "X-WP-SDK"

        private var valueField = DEFAULT_VALUE
        internal val value get() = valueField

        fun overrideValue(newValue: String) {
            if (!validateVersionForOverride(newValue)) {
                throw RuntimeException(INVALID_VERSION_ERROR_MESSAGE)
            }

            valueField = newValue
        }

        private fun validateVersionForOverride(untrustedVersion: String): Boolean {
            if (untrustedVersion.equals(DEFAULT_VALUE)) {
                return true
            }

            val pattern =
                "access\\-checkout\\-react\\-native\\/[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{1,2}"
            return pattern.toRegex().matches(untrustedVersion)
        }
    }
}
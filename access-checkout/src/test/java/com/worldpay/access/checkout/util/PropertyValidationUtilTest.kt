package com.worldpay.access.checkout.util

import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.util.PropertyValidationUtil.validateNotNull
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.Test

class PropertyValidationUtilTest {

    @Test
    fun `should throw AccessCheckoutException with expected message when property is null`() {
        val exception = assertFailsWith<AccessCheckoutException> {
            validateNotNull(null, "property-name")
        }

        assertEquals("Expected property-name to be provided but was not", exception.message)
    }

    @Test
    fun `should not throw AccessCheckoutException when property is not null`() {
        validateNotNull("some-value", "property-name")
    }
}

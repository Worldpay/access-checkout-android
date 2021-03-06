package com.worldpay.access.checkout.util

import com.worldpay.access.checkout.util.PropertyValidationUtil.validateNotNull
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.Test

class PropertyValidationUtilTest {

    @Test
    fun `should throw illegal argument exception with expected message when property is null`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            validateNotNull(null, "property-name")
        }

        assertEquals("Expected property-name to be provided but was not", exception.message)
    }

    @Test
    fun `should not throw illegal argument exception when property is not null`() {
        validateNotNull("some-value", "property-name")
    }
}

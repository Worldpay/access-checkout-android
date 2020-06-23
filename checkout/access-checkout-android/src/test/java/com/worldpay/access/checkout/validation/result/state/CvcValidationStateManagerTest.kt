package com.worldpay.access.checkout.validation.result.state

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CvcValidationStateManagerTest {

    private val validationStateManager = CvcValidationStateManager

    @Test
    fun `should be static`() {
        val validationStateManager1 = CvcValidationStateManager
        val validationStateManager2 = CvcValidationStateManager

        assertEquals(validationStateManager1, validationStateManager2)
    }

    @Test
    fun `should return true when cvc is valid`() {
        assertFalse(validationStateManager.isAllValid())

        validationStateManager.cvcValidationState.validationState = true

        assertTrue(validationStateManager.isAllValid())
    }

    @Test
    fun `should return false when cvc is invalid`() {
        assertFalse(validationStateManager.isAllValid())

        validationStateManager.cvcValidationState.validationState = false

        assertFalse(validationStateManager.isAllValid())
    }
}

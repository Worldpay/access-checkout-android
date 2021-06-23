package com.worldpay.access.checkout.validation.result.state

import com.nhaarman.mockitokotlin2.mock
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Test

class CvcValidationStateManagerTest {

    private val validationStateManager = CvcValidationStateManager(mock())

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

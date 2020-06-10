package com.worldpay.access.checkout.validation.result

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidationStateManagerTest {

    private val validationStateManager = ValidationStateManager()

    @Test
    fun `should start state as invalid`() {
        assertFalse(validationStateManager.isAllValid())
        assertFalse(validationStateManager.panValidated.get())
        assertFalse(validationStateManager.expiryDateValidated.get())
        assertFalse(validationStateManager.cvvValidated.get())
    }

    @Test
    fun `should return true when all is valid`() {
        assertFalse(validationStateManager.isAllValid())

        validationStateManager.panValidated.set(true)
        validationStateManager.expiryDateValidated.set(true)
        validationStateManager.cvvValidated.set(true)

        assertTrue(validationStateManager.isAllValid())
    }

    @Test
    fun `should return false when isAllValid and pan is invalid`() {
        assertFalse(validationStateManager.isAllValid())

        validationStateManager.panValidated.set(false)
        validationStateManager.expiryDateValidated.set(true)
        validationStateManager.cvvValidated.set(true)

        assertFalse(validationStateManager.isAllValid())
    }

    @Test
    fun `should return false when isAllValid and cvv is invalid`() {
        assertFalse(validationStateManager.isAllValid())

        validationStateManager.panValidated.set(true)
        validationStateManager.expiryDateValidated.set(true)
        validationStateManager.cvvValidated.set(false)

        assertFalse(validationStateManager.isAllValid())
    }

}

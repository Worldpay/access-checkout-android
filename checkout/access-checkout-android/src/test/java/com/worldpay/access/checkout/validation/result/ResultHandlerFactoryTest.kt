package com.worldpay.access.checkout.validation.result

import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.validation.state.CardValidationStateManager
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class ResultHandlerFactoryTest {

    private val accessCheckoutValidationListener = mock<AccessCheckoutCardValidationListener>()
    private val fieldValidationStateManager = mock<CardValidationStateManager>()

    private lateinit var resultHandlerFactory : ResultHandlerFactory

    @Before
    fun setup() {
        resultHandlerFactory = ResultHandlerFactory(
            accessCheckoutValidationListener, fieldValidationStateManager
        )
    }

    @Test
    fun `should return a cvc validation result handler`() {
        assertNotNull(resultHandlerFactory.getCvvValidationResultHandler())
    }

    @Test
    fun `should return same instance of cvc validation result handler on multiple calls using same instance of factory`() {
        val handler1 = resultHandlerFactory.getCvvValidationResultHandler()
        val handler2 = resultHandlerFactory.getCvvValidationResultHandler()

        assertEquals(handler1, handler2)

        val resultHandlerFactory = ResultHandlerFactory(
            accessCheckoutValidationListener, fieldValidationStateManager
        )

        val handler3 = resultHandlerFactory.getCvvValidationResultHandler()

        assertNotEquals(handler2, handler3)
    }

    @Test
    fun `should return a pan validation result handler`() {
        assertNotNull(resultHandlerFactory.getPanValidationResultHandler())
    }

    @Test
    fun `should return same instance of pan validation result handler on multiple calls using same instance of factory`() {
        val handler1 = resultHandlerFactory.getPanValidationResultHandler()
        val handler2 = resultHandlerFactory.getPanValidationResultHandler()

        assertEquals(handler1, handler2)

        val resultHandlerFactory = ResultHandlerFactory(
            accessCheckoutValidationListener, fieldValidationStateManager
        )

        val handler3 = resultHandlerFactory.getPanValidationResultHandler()

        assertNotEquals(handler2, handler3)
    }

    @Test
    fun `should return a expiry date validation result handler`() {
        assertNotNull(resultHandlerFactory.getExpiryDateValidationResultHandler())
    }

    @Test
    fun `should return same instance of expiry date validation result handler on multiple calls using same instance of factory`() {
        val handler1 = resultHandlerFactory.getExpiryDateValidationResultHandler()
        val handler2 = resultHandlerFactory.getExpiryDateValidationResultHandler()

        assertEquals(handler1, handler2)

        val resultHandlerFactory = ResultHandlerFactory(
            accessCheckoutValidationListener, fieldValidationStateManager
        )

        val handler3 = resultHandlerFactory.getExpiryDateValidationResultHandler()

        assertNotEquals(handler2, handler3)
    }

    @Test
    fun `should return a brand change handler`() {
        assertNotNull(resultHandlerFactory.getBrandChangedHandler())
    }

    @Test
    fun `should return same instance of brand change validation result handler on multiple calls using same instance of factory`() {
        val handler1 = resultHandlerFactory.getBrandChangedHandler()
        val handler2 = resultHandlerFactory.getBrandChangedHandler()

        assertEquals(handler1, handler2)

        val resultHandlerFactory = ResultHandlerFactory(
            accessCheckoutValidationListener, fieldValidationStateManager
        )

        val handler3 = resultHandlerFactory.getBrandChangedHandler()

        assertNotEquals(handler2, handler3)
    }

}

package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.validation.result.state.CardValidationStateManager
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.kotlin.mock

class ResultHandlerFactoryTest {

    private val accessCheckoutValidationListener = mock<AccessCheckoutCardValidationListener>()
    private val fieldValidationStateManager = CardValidationStateManager(mock(), mock(), mock())
    private val lifecycleOwner = mock<LifecycleOwner>()
    private val lifecycle = mock<Lifecycle>()

    private lateinit var resultHandlerFactory: ResultHandlerFactory

    @Before
    fun setup() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)
        resultHandlerFactory = ResultHandlerFactory(
            accessCheckoutValidationListener, fieldValidationStateManager,
            lifecycleOwner
        )
    }

    @Test
    fun `should return a cvc validation result handler`() {
        assertNotNull(resultHandlerFactory.getCvcValidationResultHandler())
    }

    @Test
    fun `should return same instance of cvc validation result handler on multiple calls using same instance of factory`() {
        val handler1 = resultHandlerFactory.getCvcValidationResultHandler()
        val handler2 = resultHandlerFactory.getCvcValidationResultHandler()

        assertEquals(handler1, handler2)

        val resultHandlerFactory = ResultHandlerFactory(
            accessCheckoutValidationListener, fieldValidationStateManager,
            lifecycleOwner
        )

        val handler3 = resultHandlerFactory.getCvcValidationResultHandler()

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
            accessCheckoutValidationListener, fieldValidationStateManager,
            lifecycleOwner
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
            accessCheckoutValidationListener, fieldValidationStateManager,
            lifecycleOwner
        )

        val handler3 = resultHandlerFactory.getExpiryDateValidationResultHandler()

        assertNotEquals(handler2, handler3)
    }

    @Test
    fun `should return a brand change handler`() {
        assertNotNull(resultHandlerFactory.getBrandsChangedHandler())
    }

    @Test
    fun `should return same instance of brand change validation result handler on multiple calls using same instance of factory`() {
        val handler1 = resultHandlerFactory.getBrandsChangedHandler()
        val handler2 = resultHandlerFactory.getBrandsChangedHandler()

        assertEquals(handler1, handler2)

        val resultHandlerFactory = ResultHandlerFactory(
            accessCheckoutValidationListener, fieldValidationStateManager,
            lifecycleOwner
        )

        val handler3 = resultHandlerFactory.getBrandsChangedHandler()

        assertNotEquals(handler2, handler3)
    }
}

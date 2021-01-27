package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.validation.result.state.FieldValidationState
import org.junit.Before
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class AbstractValidationResultHandlerTest {

    private val lifeCycleOwner = mock<LifecycleOwner>()
    private lateinit var fieldValidationState: FieldValidationState

    private lateinit var testResultHandler: TestResultHandler

    @Before
    fun setup() {
        val lifecycle = mock<Lifecycle>()
        given(lifeCycleOwner.lifecycle).willReturn(lifecycle)

        fieldValidationState = spy(FieldValidationState(Random.nextInt()))

        testResultHandler = spy(TestResultHandler(fieldValidationState, lifeCycleOwner))
        verify(lifecycle).addObserver(any<TestResultHandler>())
    }

    @Test
    fun `should notify listener when isValid is true and forceNotify is true and validation state was previously valid`() {
        fieldValidationState.validationState = true

        testResultHandler.handleResult(isValid = true, forceNotify = true)

        verify(testResultHandler).notifyListener(true)

        assertTrue(fieldValidationState.validationState)
        assertTrue(fieldValidationState.notificationSent)
    }

    @Test
    fun `should notify listener when isValid is true and forceNotify is true and validation state was previously invalid`() {
        testResultHandler.handleResult(isValid = true, forceNotify = true)

        verify(testResultHandler).notifyListener(true)

        assertTrue(fieldValidationState.validationState)
        assertTrue(fieldValidationState.notificationSent)
    }

    @Test
    fun `should notify listener when isValid is false and forceNotify is true and validation state was previously valid`() {
        fieldValidationState.validationState = true

        testResultHandler.handleResult(isValid = false, forceNotify = true)

        verify(testResultHandler).notifyListener(false)

        assertFalse(fieldValidationState.validationState)
        assertTrue(fieldValidationState.notificationSent)
    }

    @Test
    fun `should notify listener when isValid is false and forceNotify is true and validation state was previously invalid`() {
        testResultHandler.handleResult(isValid = false, forceNotify = true)

        verify(testResultHandler).notifyListener(false)

        assertFalse(fieldValidationState.validationState)
        assertTrue(fieldValidationState.notificationSent)
    }

    @Test
    fun `should notify when isValid is true and forceNotify is false and validation state was previously false`() {
        testResultHandler.handleResult(isValid = true, forceNotify = false)

        verify(testResultHandler).notifyListener(true)

        assertTrue(fieldValidationState.validationState)
        assertTrue(fieldValidationState.notificationSent)
    }

    @Test
    fun `should notify when isValid is true and forceNotify is false and validation state was previously false - without specifying the forceNotify argument`() {
        testResultHandler.handleResult(isValid = true)

        verify(testResultHandler).notifyListener(true)

        assertTrue(fieldValidationState.validationState)
        assertTrue(fieldValidationState.notificationSent)
    }

    @Test
    fun `should not notify when isValid is true and forceNotify is false and validation state was previously true`() {
        fieldValidationState.validationState = true

        testResultHandler.handleResult(isValid = true, forceNotify = false)

        verify(testResultHandler, never()).notifyListener(any())

        assertTrue(fieldValidationState.validationState)
        assertFalse(fieldValidationState.notificationSent)
    }

    @Test
    fun `should not notify when isValid is false and forceNotify is false and validation state was previously false`() {
        testResultHandler.handleResult(isValid = false, forceNotify = false)

        verify(testResultHandler, never()).notifyListener(any())

        assertFalse(fieldValidationState.validationState)
        assertFalse(fieldValidationState.notificationSent)
    }

    @Test
    fun `should notify when isValid is true and forceNotify is false and onStop has been called and with a previous notification sent`() {
        // given
        testResultHandler.handleResult(isValid = true, forceNotify = false)
        verify(testResultHandler).notifyListener(true)
        assertTrue(fieldValidationState.validationState)
        assertTrue(fieldValidationState.notificationSent)
        reset(testResultHandler)

        // when
        testResultHandler.onStop()
        testResultHandler.handleResult(isValid = true, forceNotify = false)

        // then
        verify(testResultHandler).notifyListener(true)
        assertTrue(fieldValidationState.validationState)
        assertTrue(fieldValidationState.notificationSent)
    }

    @Test
    fun `should notify when isValid is false and forceNotify is false and onStop has been called and with a previous notification sent`() {
        // given
        fieldValidationState.validationState = true
        testResultHandler.handleResult(isValid = false, forceNotify = false)
        verify(testResultHandler).notifyListener(false)
        assertFalse(fieldValidationState.validationState)
        assertTrue(fieldValidationState.notificationSent)
        reset(testResultHandler)

        // when
        testResultHandler.onStop()
        testResultHandler.handleResult(isValid = false, forceNotify = false)

        // then
        verify(testResultHandler).notifyListener(false)
        assertFalse(fieldValidationState.validationState)
        assertTrue(fieldValidationState.notificationSent)
    }

    @Test
    fun `should notify listener when handle focus change is called and a notification has not been sent before and not in lifecycle event`() {
        testResultHandler.handleFocusChange()

        verify(testResultHandler).notifyListener(false)

        assertFalse(fieldValidationState.validationState)
        assertTrue(fieldValidationState.notificationSent)
    }

    @Test
    fun `should not notify listener when handle focus change is called and a notification has been sent before`() {
        fieldValidationState.notificationSent = true

        testResultHandler.handleFocusChange()

        verify(testResultHandler, never()).notifyListener(any())

        assertFalse(fieldValidationState.validationState)
        assertTrue(fieldValidationState.notificationSent)
    }

    @Test
    fun `should not notify listener when handle focus change is called and currently in a lifecycle event`() {
        testResultHandler.onPause()
        testResultHandler.handleFocusChange()

        verify(testResultHandler, never()).notifyListener(any())

        assertFalse(fieldValidationState.validationState)
        assertFalse(fieldValidationState.notificationSent)
    }

    @Test
    fun `should notify listener when handle focus change is called and not in a lifecycle event after leaving one`() {
        testResultHandler.onPause()
        testResultHandler.handleFocusChange()

        verify(testResultHandler, never()).notifyListener(any())

        assertFalse(fieldValidationState.validationState)
        assertFalse(fieldValidationState.notificationSent)

        testResultHandler.onResume()
        testResultHandler.handleFocusChange()

        verify(testResultHandler).notifyListener(false)

        assertFalse(fieldValidationState.validationState)
        assertTrue(fieldValidationState.notificationSent)
    }

    @Test
    fun `should notify when restarting the application`() {
        val fieldValidationState = FieldValidationState(Random.nextInt())
        val testResultHandler = spy(TestResultHandler(fieldValidationState, lifeCycleOwner))

        testResultHandler.handleResult(isValid = true, forceNotify = false)
        verify(testResultHandler).notifyListener(true)

        assertTrue(fieldValidationState.validationState)
        assertTrue(fieldValidationState.notificationSent)

        reset(testResultHandler)
        testResultHandler.onStop()
        assertTrue(fieldValidationState.validationState)
        assertFalse(fieldValidationState.notificationSent)

        reset(testResultHandler)
        testResultHandler.onResume()

        testResultHandler.handleResult(isValid = true, forceNotify = false)
        verify(testResultHandler).notifyListener(true)

        assertTrue(fieldValidationState.validationState)
        assertTrue(fieldValidationState.notificationSent)
    }

    @Test
    fun `should not notify twice if handled twice`() {
        val fieldValidationState = FieldValidationState(123)
        val testResultHandler = spy(TestResultHandler(fieldValidationState, lifeCycleOwner))

        testResultHandler.handleResult(isValid = true, forceNotify = false)
        assertTrue(fieldValidationState.notificationSent)
        assertTrue(fieldValidationState.validationState)
        verify(testResultHandler).notifyListener(true)
        reset(testResultHandler)

        testResultHandler.handleResult(isValid = true, forceNotify = false)

        verify(testResultHandler, never()).notifyListener(true)
    }

    @Test
    fun `should not fail when there is no stored state`() {
        testResultHandler.onResume()
        testResultHandler.onStop()
        assertFalse(fieldValidationState.validationState)
        assertFalse(fieldValidationState.notificationSent)
    }

    internal class TestResultHandler(
        private val fieldValidationState: FieldValidationState,
        lifecycleOwner: LifecycleOwner
    ) : AbstractValidationResultHandler(lifecycleOwner) {

        override fun notifyListener(isValid: Boolean) {
        }

        override fun getState(): FieldValidationState {
            return fieldValidationState
        }

    }
}

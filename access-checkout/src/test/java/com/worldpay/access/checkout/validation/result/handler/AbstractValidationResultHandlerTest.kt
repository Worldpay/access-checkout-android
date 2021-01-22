package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.validation.result.state.FieldValidationState
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class AbstractValidationResultHandlerTest {

    private val lifeCycleOwner = mock<LifecycleOwner>()
    private val fieldValidationState = mock<FieldValidationState>()

    private lateinit var testResultHandler: TestResultHandler

    @Before
    fun setup() {
        val lifecycle = mock<Lifecycle>()
        given(lifeCycleOwner.lifecycle).willReturn(lifecycle)

        given(fieldValidationState.id).willReturn(Random.nextInt())

        testResultHandler = spy(TestResultHandler(fieldValidationState, lifeCycleOwner))
        verify(lifecycle).addObserver(any<TestResultHandler>())
    }

    @Test
    fun `should notify listener when isValid is true and forceNotify is true and validation state was previously valid`() {
        setInitialValidationStateAs(true)

        testResultHandler.handleResult(isValid = true, forceNotify = true)

        verify(testResultHandler).notifyListener(true)

        verifyFieldValidationStateInvokedWith(validationState = true)
    }

    @Test
    fun `should notify listener when isValid is true and forceNotify is true and validation state was previously invalid`() {
        setInitialValidationStateAs(false)

        testResultHandler.handleResult(isValid = true, forceNotify = true)

        verify(testResultHandler).notifyListener(true)

        verify(fieldValidationState).validationState = true
        verify(fieldValidationState).notificationSent = true
    }

    @Test
    fun `should notify listener when isValid is false and forceNotify is true and validation state was previously valid`() {
        setInitialValidationStateAs(true)

        testResultHandler.handleResult(isValid = false, forceNotify = true)

        verify(testResultHandler).notifyListener(false)

        verifyFieldValidationStateInvokedWith(validationState = false)
    }

    @Test
    fun `should notify listener when isValid is false and forceNotify is true and validation state was previously invalid`() {
        setInitialValidationStateAs(false)

        testResultHandler.handleResult(isValid = false, forceNotify = true)

        verify(testResultHandler).notifyListener(false)

        verifyFieldValidationStateInvokedWith(validationState = false)
    }

    @Test
    fun `should notify when isValid is true and forceNotify is false and validation state was previously false`() {
        setInitialValidationStateAs(false)

        testResultHandler.handleResult(isValid = true, forceNotify = false)

        verify(testResultHandler).notifyListener(true)

        verifyFieldValidationStateInvokedWith(validationState = true)
    }

    @Test
    fun `should notify when isValid is true and forceNotify is false and validation state was previously false - without specifying the forceNotify argument`() {
        setInitialValidationStateAs(false)

        testResultHandler.handleResult(isValid = true)

        verify(testResultHandler).notifyListener(true)

        verifyFieldValidationStateInvokedWith(validationState = true)
    }

    @Test
    fun `should not notify when isValid is true and forceNotify is false and validation state was previously true`() {
        setInitialValidationStateAs(true)

        testResultHandler.handleResult(isValid = true, forceNotify = false)

        verify(testResultHandler, never()).notifyListener(any())

        verifyFieldValidationStateNotInvoked()
    }

    @Test
    fun `should not notify when isValid is false and forceNotify is false and validation state was previously false`() {
        setInitialValidationStateAs(false)

        testResultHandler.handleResult(isValid = false, forceNotify = false)

        verify(testResultHandler, never()).notifyListener(any())

        verifyFieldValidationStateNotInvoked()
    }

    @Test
    fun `should notify when isValid is true and forceNotify is false and onStop has been called and with a previous notification sent`() {
        // given
        setInitialValidationStateAs(false)
        testResultHandler.handleResult(isValid = true, forceNotify = false)
        verify(testResultHandler).notifyListener(true)
        verifyFieldValidationStateInvokedWith(validationState = true)
        reset(testResultHandler, fieldValidationState)

        // when
        testResultHandler.onStop()
        testResultHandler.handleResult(isValid = true, forceNotify = false)

        // then
        verify(testResultHandler).notifyListener(true)
        verifyFieldValidationStateInvokedWith(validationState = true)
    }

    @Test
    fun `should notify when isValid is false and forceNotify is false and onStop has been called and with a previous notification sent`() {
        // given
        setInitialValidationStateAs(true)
        testResultHandler.handleResult(isValid = false, forceNotify = false)
        verify(testResultHandler).notifyListener(false)
        verifyFieldValidationStateInvokedWith(validationState = false)
        reset(testResultHandler, fieldValidationState)

        // when
        testResultHandler.onStop()
        testResultHandler.handleResult(isValid = false, forceNotify = false)

        // then
        verify(testResultHandler).notifyListener(false)
        verifyFieldValidationStateInvokedWith(validationState = false)
    }

    @Test
    fun `should notify listener when handle focus change is called and a notification has not been sent before and not in lifecycle event`() {
        setInitialValidationStateAs(false)
        given(fieldValidationState.notificationSent).willReturn(false)

        testResultHandler.handleFocusChange()

        verify(testResultHandler).notifyListener(false)

        verifyFieldValidationStateInvokedWith(validationState = false)
    }

    @Test
    fun `should not notify listener when handle focus change is called and a notification has been sent before`() {
        setInitialValidationStateAs(false)
        given(fieldValidationState.notificationSent).willReturn(true)

        testResultHandler.handleFocusChange()

        verify(testResultHandler, never()).notifyListener(any())

        verifyFieldValidationStateNotInvoked()
    }

    @Test
    fun `should not notify listener when handle focus change is called and currently in a lifecycle event`() {
        setInitialValidationStateAs(false)
        given(fieldValidationState.notificationSent).willReturn(false)

        testResultHandler.onPause()
        testResultHandler.handleFocusChange()

        verify(testResultHandler, never()).notifyListener(any())

        verifyFieldValidationStateNotInvoked()
    }

    @Test
    fun `should notify listener when handle focus change is called and not in a lifecycle event after leaving one`() {
        setInitialValidationStateAs(false)
        given(fieldValidationState.notificationSent).willReturn(false)

        testResultHandler.onPause()
        testResultHandler.handleFocusChange()

        verify(testResultHandler, never()).notifyListener(any())

        verifyFieldValidationStateNotInvoked()

        testResultHandler.onResume()
        testResultHandler.handleFocusChange()

        verify(testResultHandler).notifyListener(false)

        verifyFieldValidationStateInvokedWith(validationState = false)
    }

    private fun verifyFieldValidationStateInvokedWith(validationState: Boolean) {
        verify(fieldValidationState).validationState = validationState
        verify(fieldValidationState).notificationSent = true
    }

    private fun verifyFieldValidationStateNotInvoked() {
        verify(fieldValidationState, never()).validationState = any()
        verify(fieldValidationState, never()).notificationSent = any()
    }

    private fun setInitialValidationStateAs(isValid: Boolean) {
        given(fieldValidationState.validationState).willReturn(isValid)
    }

    internal class TestResultHandler(
        private val fieldValidationState: FieldValidationState,
        lifecycleOwner: LifecycleOwner
    ): AbstractValidationResultHandler(lifecycleOwner) {

        override fun notifyListener(isValid: Boolean) {
        }

        override fun getState(): FieldValidationState {
            return fieldValidationState
        }

    }
}

package com.worldpay.access.checkout

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.CardValidator
import com.worldpay.access.checkout.validation.ValidationResult
import com.worldpay.access.checkout.views.CVVLengthFilter
import com.worldpay.access.checkout.views.CardCVVText
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AccessCheckoutCVVTest {

    private lateinit var cvvView: CardCVVText
    private lateinit var cardValidator: CardValidator
    private lateinit var cardConfiguration: CardConfiguration
    private lateinit var cardListener: CardListener
    private lateinit var cvvLengthFilter: CVVLengthFilter
    private lateinit var factory: AccessCheckoutCardDefaultFactory

    private val cvvValue = "123"

    @Before
    fun setup() {
        cvvView = mock()
        cardValidator = mock()
        cardConfiguration = mock()
        cardListener = mock()
        cvvLengthFilter = mock()
        factory = mock()

        given(cvvView.getInsertedText()).willReturn(cvvValue)
        given(cardValidator.cardConfiguration).willReturn(cardConfiguration)
        given(factory.getCVVLengthFilter(cardValidator, null)).willReturn(cvvLengthFilter)
    }

    @Test
    fun `should return true when calling isValid method for valid cvv`() {
        givenCvvIsCompletelyValid()

        val accessCheckoutCVV = AccessCheckoutCVV(cvvView)
        accessCheckoutCVV.cardValidator = cardValidator

        assertTrue { accessCheckoutCVV.isValid() }
    }

    @Test
    fun `should return false when calling isValid method for invalid cvv`() {
        givenCvvIsInvalid()

        val accessCheckoutCVV = AccessCheckoutCVV(cvvView)
        accessCheckoutCVV.cardValidator = cardValidator

        assertFalse { accessCheckoutCVV.isValid() }
    }

    @Test
    fun `should return true when calling isValid method when there is no card validator set`() {
        val accessCheckoutCVV = AccessCheckoutCVV(cvvView)
        accessCheckoutCVV.cardValidator = null

        assertTrue { accessCheckoutCVV.isValid() }
    }

    @Test
    fun `should return false when calling isValid method for partially valid cvv`() {
        givenCvvIsPartiallyValid()

        val accessCheckoutCVV = AccessCheckoutCVV(cvvView)
        accessCheckoutCVV.cardValidator = cardValidator

        assertFalse { accessCheckoutCVV.isValid() }
    }

    @Test
    fun `should raise an onUpdate event with valid state and onUpdateLengthFilter event twice - when cvv is partially valid and on focus and cardValidator is set`() {
        givenCvvIsOnFocus()
        givenCvvIsPartiallyValid()

        createNewAccessCheckoutCVV()

        verify(cardListener).onUpdate(cvvView, true)
        verify(cardListener, times(2)).onUpdateLengthFilter(cvvView, cvvLengthFilter)
    }

    @Test
    fun `should raise an onUpdate event with invalid state and onUpdateLengthFilter event twice - when cvv is invalid and on focus and cardValidator is set`() {
        givenCvvIsOnFocus()
        givenCvvIsInvalid()

        createNewAccessCheckoutCVV()

        verify(cardListener).onUpdate(cvvView, false)
        verify(cardListener, times(2)).onUpdateLengthFilter(cvvView, cvvLengthFilter)
    }

    @Test
    fun `should raise an onUpdate event with valid state and onUpdateLengthFilter event twice - when cvv is complete and valid and on focus and cardValidator is set`() {
        givenCvvIsOnFocus()
        givenCvvIsCompletelyValid()

        createNewAccessCheckoutCVV()

        verify(cardListener).onUpdate(cvvView, true)
        verify(cardListener, times(2)).onUpdateLengthFilter(cvvView, cvvLengthFilter)
    }

    @Test
    fun `should raise an onUpdate event with invalid state and onUpdateLengthFilter event once - when cvv is partially valid and off focus and cardValidator is set`() {
        givenCvvIsOffFocus()
        givenCvvIsPartiallyValid()

        createNewAccessCheckoutCVV()

        verify(cardListener).onUpdate(cvvView, false)
        verify(cardListener).onUpdateLengthFilter(cvvView, cvvLengthFilter)
    }

    @Test
    fun `should raise an onUpdate event with invalid state and onUpdateLengthFilter event once - when cvv is invalid and off focus and cardValidator is set`() {
        givenCvvIsOffFocus()
        givenCvvIsInvalid()

        createNewAccessCheckoutCVV()

        verify(cardListener).onUpdate(cvvView, false)
        verify(cardListener).onUpdateLengthFilter(cvvView, cvvLengthFilter)
    }

    @Test
    fun `should raise an onUpdate event with valid state and onUpdateLengthFilter event once - when cvv is complete and valid and off focus and cardValidator is set`() {
        givenCvvIsOffFocus()
        givenCvvIsCompletelyValid()

        createNewAccessCheckoutCVV()

        verify(cardListener).onUpdate(cvvView, true)
        verify(cardListener).onUpdateLengthFilter(cvvView, cvvLengthFilter)
    }

    @Test
    fun `should raise 1 onUpdate event with valid state and 1 onUpdateLengthFilter event - when cvv is partially valid and onUpdateCVV is called`() {
        givenCvvIsOnFocus()
        givenCvvIsPartiallyValid()

        val accessCheckoutCvv = createNewAccessCheckoutCVV()
        reset(cardListener)
        accessCheckoutCvv.onUpdateCVV(cvvValue)

        verify(cardListener).onUpdate(cvvView, true)
        verify(cardListener).onUpdateLengthFilter(cvvView, cvvLengthFilter)
    }

    @Test
    fun `should raise 1 onUpdate event with valid state and 1 onUpdateLengthFilter event - when cvv is complete and valid and onUpdateCVV is called`() {
        givenCvvIsCompletelyValid()

        val accessCheckoutCvv = createNewAccessCheckoutCVV()
        reset(cardListener)
        accessCheckoutCvv.onUpdateCVV(cvvValue)

        verify(cardListener).onUpdate(cvvView, true)
        verify(cardListener).onUpdateLengthFilter(cvvView, cvvLengthFilter)
    }

    @Test
    fun `should raise 1 onUpdate event with invalid state and 1 onUpdateLengthFilter event - when cvv is invalid and onUpdateCVV is called`() {
        givenCvvIsInvalid()

        val accessCheckoutCvv = createNewAccessCheckoutCVV()
        reset(cardListener)
        accessCheckoutCvv.onUpdateCVV(cvvValue)

        verify(cardListener).onUpdate(cvvView, false)
        verify(cardListener).onUpdateLengthFilter(cvvView, cvvLengthFilter)
    }

    @Test
    fun `should not raise any events when cardValidator is not set and onUpdateCVV is called`() {
        givenCvvIsInvalid()

        val accessCheckoutCVV = AccessCheckoutCVV(cvvView, factory)
        accessCheckoutCVV.cardListener = cardListener
        accessCheckoutCVV.cardValidator = null

        accessCheckoutCVV.onUpdateCVV(cvvValue)

        verifyZeroInteractions(cardValidator)
        verifyZeroInteractions(cardListener)
    }

    @Test
    fun `should raise 1 onUpdate event with invalid state - when cvv is partially valid and onEndUpdateCVV is called`() {
        givenCvvIsOffFocus()
        givenCvvIsPartiallyValid()

        val accessCheckoutCvv = createNewAccessCheckoutCVV()
        reset(cardListener)
        accessCheckoutCvv.onEndUpdateCVV(cvvValue)

        verify(cardListener).onUpdate(cvvView, false)
    }

    @Test
    fun `should raise onUpdate event with valid state - when cvv is complete and valid and onEndUpdateCVV is called`() {
        givenCvvIsOffFocus()
        givenCvvIsCompletelyValid()

        val accessCheckoutCvv = createNewAccessCheckoutCVV()
        reset(cardListener)
        accessCheckoutCvv.onEndUpdateCVV(cvvValue)

        verify(cardListener).onUpdate(cvvView, true)
    }

    @Test
    fun `should raise onUpdate event with invalid state - when cvv is invalid and onEndUpdateCVV is called`() {
        givenCvvIsOffFocus()
        givenCvvIsInvalid()

        val accessCheckoutCvv = createNewAccessCheckoutCVV()
        reset(cardListener)
        accessCheckoutCvv.onEndUpdateCVV(cvvValue)

        verify(cardListener).onUpdate(cvvView, false)
    }

    @Test
    fun `should not raise any events when cardValidator is not set and onEndUpdateCVV is called`() {
        givenCvvIsOffFocus()
        givenCvvIsInvalid()

        val accessCheckoutCVV = AccessCheckoutCVV(cvvView, factory)
        accessCheckoutCVV.cardListener = cardListener
        accessCheckoutCVV.cardValidator = null

        accessCheckoutCVV.onEndUpdateCVV(cvvValue)

        verifyZeroInteractions(cardValidator)
        verifyZeroInteractions(cardListener)
    }

    @Test
    fun `should not raise any update events if there is no cardListener set and cvv is on focus`() {
        givenCvvIsOnFocus()
        givenCvvIsPartiallyValid()

        val accessCheckoutCVV = AccessCheckoutCVV(cvvView, factory)
        accessCheckoutCVV.cardListener = null
        accessCheckoutCVV.cardValidator = cardValidator

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun `should not raise any update events if there is no cardListener set and cvv is off focus`() {
        givenCvvIsOffFocus()
        givenCvvIsPartiallyValid()

        val accessCheckoutCVV = AccessCheckoutCVV(cvvView, factory)
        accessCheckoutCVV.cardListener = null
        accessCheckoutCVV.cardValidator = cardValidator

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun `should not raise any onUpdateLengthFilter events when cvvLengthFilter is null`() {
        givenCvvIsOnFocus()
        givenCvvIsPartiallyValid()

        given(factory.getCVVLengthFilter(cardValidator, null)).willReturn(null)

        createNewAccessCheckoutCVV()

        verifyZeroInteractions(cvvLengthFilter)
        verify(cardListener, times(0)).onUpdateLengthFilter(cvvView, cvvLengthFilter)
    }

    @Test
    fun givenMethodsAreCalled() {
        // this test only exists for coverage - the class should be refactored so we are not required
        // to do this test

        val accessCheckoutCVV = AccessCheckoutCVV(cvvView)
        assertFailsWith<UnsupportedOperationException> { accessCheckoutCVV.onUpdatePAN("4444444444444") }
        assertFailsWith<UnsupportedOperationException> { accessCheckoutCVV.onEndUpdatePAN("4444444444444") }

        assertFailsWith<UnsupportedOperationException> { accessCheckoutCVV.onUpdateDate(null, null) }
        assertFailsWith<UnsupportedOperationException> { accessCheckoutCVV.onEndUpdateDate(null, null) }
    }

    private fun createNewAccessCheckoutCVV(): AccessCheckoutCVV {
        val accessCheckoutCVV = AccessCheckoutCVV(cvvView, factory)
        accessCheckoutCVV.cardListener = cardListener
        accessCheckoutCVV.cardValidator = cardValidator
        return accessCheckoutCVV
    }

    private fun givenCvvIsOffFocus() {
        given(cvvView.hasFocus()).willReturn(false)
    }

    private fun givenCvvIsOnFocus() {
        given(cvvView.hasFocus()).willReturn(true)
    }

    private fun givenCvvIsPartiallyValid() {
        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardValidator.validateCVV(cvvValue, null)).willReturn(Pair(validationResult, null))
    }

    private fun givenCvvIsCompletelyValid() {
        val validationResult = ValidationResult(partial = false, complete = true)
        given(cardValidator.validateCVV(cvvValue, null)).willReturn(Pair(validationResult, null))
    }

    private fun givenCvvIsInvalid() {
        val validationResult = ValidationResult(partial = false, complete = false)
        given(cardValidator.validateCVV(cvvValue, null)).willReturn(Pair(validationResult, null))
    }

}
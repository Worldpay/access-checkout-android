package com.worldpay.access.checkout

import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.testutils.mock
import com.worldpay.access.checkout.validation.CardValidator
import com.worldpay.access.checkout.validation.ValidationResult
import com.worldpay.access.checkout.views.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class AccessCheckoutCardTest {

    private val context = ShadowInstrumentation.getInstrumentation().targetContext.applicationContext

    private lateinit var card: Card
    private lateinit var panView: CardView
    private lateinit var cvvView: CardCVVText
    private lateinit var dateView: CardExpiryTextLayout
    private lateinit var cardValidator: CardValidator
    private lateinit var cardConfiguration: CardConfiguration
    private lateinit var cardListener: CardListener
    private lateinit var factory: CardFactory
    private lateinit var panLengthFilter: PANLengthFilter
    private lateinit var cvvLengthFilter: CVVLengthFilter
    private lateinit var monthLengthFilter: MonthLengthFilter
    private lateinit var yearLengthFilter: YearLengthFilter

    private val pan = "00000000"
    private val cvv = "123"
    private val month = "02"
    private val year = "24"

    @Before
    fun setup() {
        panView = mock()
        cvvView = mock()
        dateView = mock()
        factory = mock()
        cardValidator = mock()
        cardConfiguration = mock()
        cardListener = mock()
        panLengthFilter = mock()
        cvvLengthFilter = mock()
        monthLengthFilter = mock()
        yearLengthFilter = mock()
        given(factory.getCardConfiguration()).willReturn(cardConfiguration)
        given(factory.getPANLengthFilter(cardValidator, cardConfiguration)).willReturn(panLengthFilter)
        given(factory.getCVVLengthFilter(cardValidator, cardConfiguration, panView)).willReturn(cvvLengthFilter)
        given(factory.getCardValidator(cardConfiguration)).willReturn(cardValidator)
        given(factory.getMonthLengthFilter(cardConfiguration)).willReturn(monthLengthFilter)
        given(factory.getYearLengthFilter(cardConfiguration)).willReturn(yearLengthFilter)
        given(panView.getInsertedText()).willReturn(pan)
        given(cvvView.getInsertedText()).willReturn(cvv)
        given(dateView.getInsertedMonth()).willReturn(month)
        given(dateView.getInsertedYear()).willReturn(year)
        card = AccessCheckoutCard(context, panView, cvvView, dateView, factory)
        card.cardListener = cardListener
    }

    @Test
    fun givenOnlyDefaultConstructorArgs_ThenAccessCheckoutCardIsConstructed() {
        val card = AccessCheckoutCard(context, panView, cvvView, dateView)

        assertNotNull(card)
    }

    @Test
    fun givenEmptyCardValidatorInConstructorArgs_ThenAccessCheckoutCardIsConstructed() {
        val card = AccessCheckoutCard(context, panView, cvvView, dateView, cardValidator = null)

        assertNotNull(card)
    }

    @Test
    fun givenNoValidatorHasBeenSetOnTheCard_ThenCardShouldBeValid() {
        card.cardValidator = null

        assertTrue(card.isValid())
    }

    @Test
    fun givenValidatorHasBeenSetOnTheCard_AndAllFieldsAreValid_ThenCardShouldBeValid() {
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = true))

        assertTrue(card.isValid())
    }

    @Test
    fun givenValidatorHasBeenSetOnTheCard_AndOnlyPANValid_ThenCardShouldNotBeValid() {
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(ValidationResult(partial = false, complete = false), null))
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = false))

        assertFalse(card.isValid())
    }

    @Test
    fun givenValidatorHasBeenSetOnTheCard_AndOnlyPANAndCVVValid_ThenCardShouldNotBeValid() {
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = false))

        assertFalse(card.isValid())
    }

    @Test
    fun givenValidatorHasBeenSetOnTheCard_AndOnlyPANAndExpiryValid_ThenCardShouldNotBeValid() {
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(ValidationResult(partial = false, complete = false), null))
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = true))

        assertFalse(card.isValid())
    }

    @Test
    fun givenValidatorHasBeenSetOnTheCard_AndOnlyCVVValid_ThenCardShouldNotBeValid() {
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = false), null))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = false))

        assertFalse(card.isValid())
    }

    @Test
    fun givenValidatorHasBeenSetOnTheCard_AndOnlyCVVAndExpiryValid_ThenCardShouldNotBeValid() {
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = false), null))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = true))

        assertFalse(card.isValid())
    }

    @Test
    fun givenValidatorHasBeenSetOnTheCard_AndOnlyExpiryValid_ThenCardShouldNotBeValid() {
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = false), null))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(ValidationResult(partial = false, complete = false), null))
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = true))

        assertFalse(card.isValid())
    }

    @Test
    fun givenPANHasBeenUpdatedAndNoCardValidatorHasBeenSetThenCardListenerIsNotNotified() {
        card.cardValidator = null

        card.onUpdatePAN("1234")

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenPANHasBeenUpdatedAndNoCardListenerHasBeenSetThenCardListenerIsNotNotified() {
        val updatedPan = "1234"
        card.cardListener = null
        given(cardValidator.validatePAN(updatedPan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))
        given(cardValidator.validateCVV(cvv, updatedPan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))

        card.onUpdatePAN(updatedPan)

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenPANHasBeenUpdatedThenCardListenerIsNotifiedOfResult() {
        val updatedPan = "1234"
        val cardBrand = CardBrand("test", "test", null, emptyList())
        val panValidationResult = ValidationResult(partial = false, complete = true)
        val cvvValidationResult = ValidationResult(partial = false, complete = false)
        given(panView.getInsertedText()).willReturn(updatedPan)
        given(cardValidator.validatePAN(updatedPan)).willReturn(Pair(panValidationResult, cardBrand))
        given(cardValidator.validateCVV(cvv, updatedPan)).willReturn(Pair(cvvValidationResult, cardBrand))

        card.onUpdatePAN(updatedPan)

        verify(cardListener).onPANUpdateValidationResult(panValidationResult, cardBrand, panLengthFilter)
        verify(cardListener).onCVVEndUpdateValidationResult(cvvValidationResult)
        verify(cardListener).onValidationResult(ValidationResult(partial = false, complete = false))
    }

    @Test
    fun givenPANHasEndedUpdateAndNoCardValidatorHasBeenSetThenCardListenerIsNotNotified() {
        card.cardValidator = null

        card.onEndUpdatePAN("1234")

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenPANHasEndedUpdateAndNoCardListenerHasBeenSetThenCardListenerIsNotNotified() {
        val updatedPan = "1234"
        card.cardListener = null
        given(cardValidator.validatePAN(updatedPan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))
        given(cardValidator.validateCVV(cvv, updatedPan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))

        card.onEndUpdatePAN(updatedPan)

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenPANHasEndedUpdateThenCardListenerIsNotifiedOfResult() {
        val updatedPan = "1234"
        val cardBrand = CardBrand("test", "test", null, emptyList())
        val panValidationResult = ValidationResult(partial = false, complete = true)
        val cvvValidationResult = ValidationResult(partial = false, complete = false)
        given(panView.getInsertedText()).willReturn(updatedPan)
        given(cardValidator.validatePAN(updatedPan)).willReturn(Pair(panValidationResult, cardBrand))
        given(cardValidator.validateCVV(cvv, updatedPan)).willReturn(Pair(cvvValidationResult, cardBrand))

        card.onEndUpdatePAN(updatedPan)

        verify(cardListener).onPANEndUpdateValidationResult(panValidationResult, cardBrand)
        verify(cardListener).onCVVEndUpdateValidationResult(cvvValidationResult)
        verify(cardListener).onValidationResult(ValidationResult(partial = false, complete = false))
    }

    @Test
    fun givenCVVHasBeenUpdatedAndNoCardValidatorHasBeenSetThenCardListenerIsNotNotified() {
        card.cardValidator = null

        card.onUpdateCVV("1234")

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenCVVHasBeenUpdatedAndNoCardListenerHasBeenSetThenCardListenerIsNotNotified() {
        val updatedCVV = "1234"
        card.cardListener = null
        given(cardValidator.validateCVV(updatedCVV, pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))

        card.onUpdateCVV(updatedCVV)

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenCVVHasBeenUpdatedThenCardListenerIsNotifiedOfResult() {
        val updatedCVV = "1234"
        val cardBrand = CardBrand("test", "test", null, emptyList())
        val cvvValidationResult = ValidationResult(partial = false, complete = false)
        given(cvvView.getInsertedText()).willReturn(updatedCVV)
        given(cardValidator.validateCVV(updatedCVV, pan)).willReturn(Pair(cvvValidationResult, cardBrand))
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), cardBrand))

        card.onUpdateCVV(updatedCVV)

        verify(cardListener).onCVVUpdateValidationResult(cvvValidationResult, cvvLengthFilter)
        verify(cardListener).onValidationResult(ValidationResult(partial = false, complete = false))
    }

    @Test
    fun givenCVVHasEndedUpdateAndNoCardValidatorHasBeenSetThenCardListenerIsNotNotified() {
        card.cardValidator = null

        card.onEndUpdateCVV("1234")

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenCVVHasEndedUpdateAndNoCardListenerHasBeenSetThenCardListenerIsNotNotified() {
        val updatedCVV = "1234"
        card.cardListener = null
        given(cardValidator.validateCVV(updatedCVV, pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))

        card.onEndUpdateCVV(updatedCVV)

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenCVVHasEndedUpdateThenCardListenerIsNotifiedOfResult() {
        val updatedCVV = "1234"
        val cardBrand = CardBrand("test", "test", null, emptyList())
        val cvvValidationResult = ValidationResult(partial = false, complete = false)
        given(cvvView.getInsertedText()).willReturn(updatedCVV)
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), cardBrand))
        given(cardValidator.validateCVV(updatedCVV, pan)).willReturn(Pair(cvvValidationResult, cardBrand))

        card.onEndUpdateCVV(updatedCVV)

        verify(cardListener).onCVVEndUpdateValidationResult(cvvValidationResult)
        verify(cardListener).onValidationResult(ValidationResult(partial = false, complete = false))
    }

    @Test
    fun givenDateHasBeenUpdatedAndNoCardValidatorHasBeenSetThenCardListenerIsNotNotified() {
        card.cardValidator = null

        card.onUpdateDate("01", "20")

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenDateHasBeenUpdatedAndNoCardListenerHasBeenSetThenCardListenerIsNotNotified() {
        card.cardListener = null
        val month = "01"
        val year = "19"
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = false))

        card.onUpdateDate(month, year)

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenDateHasBeenUpdatedToCompleteDateThenCardListenerIsNotifiedOfFullValidationResult() {
        val month = "01"
        val year = "19"
        given(dateView.getInsertedMonth()).willReturn(month)
        given(dateView.getInsertedYear()).willReturn(year)
        given(cardValidator.canUpdate(month, year)).willReturn(false)
        val validationResult = ValidationResult(partial = false, complete = false)
        given(cardValidator.validateDate(month, year)).willReturn(validationResult)

        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(ValidationResult(partial = false, complete = false), null))
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))

        card.onUpdateDate(month, null)

        verify(cardListener).onDateUpdateValidationResult(validationResult, validationResult, monthLengthFilter, yearLengthFilter)
        verify(cardListener).onValidationResult(ValidationResult(partial = false, complete = false))
    }

    @Test
    fun givenDateHasBeenUpdatedToPartiallyCompleteDateThenCardListenerIsNotifiedOfValidationResult() {
        val month = "01"
        val year = "1"
        given(dateView.getInsertedMonth()).willReturn(month)
        given(dateView.getInsertedYear()).willReturn(year)
        given(cardValidator.canUpdate(month, year)).willReturn(true)

        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(ValidationResult(partial = false, complete = false), null))
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))

        val monthValidationResult = ValidationResult(partial = false, complete = true)
        given(cardValidator.validateDate(month, null)).willReturn(monthValidationResult)

        card.onUpdateDate(month, null)

        verify(cardListener).onDateUpdateValidationResult(monthValidationResult, null, monthLengthFilter, yearLengthFilter)
        verify(cardListener).onValidationResult(ValidationResult(partial = false, complete = false))
    }

    @Test
    fun givenMonthHasBeenUpdatedThenCardListenerIsNotifiedOfMonthValidationResult() {
        val month = "08"
        given(dateView.getInsertedYear()).willReturn("")
        given(dateView.getInsertedMonth()).willReturn(month)
        given(cardValidator.canUpdate(month, "")).willReturn(true)

        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(ValidationResult(partial = false, complete = false), null))
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))
        given(cardValidator.validateDate(month, null)).willReturn(ValidationResult(partial = false, complete = true))

        card.onUpdateDate(month, null)

        verify(cardListener).onDateUpdateValidationResult(ValidationResult(partial = false, complete = true), null, monthLengthFilter, yearLengthFilter)
        verify(cardListener).onValidationResult(ValidationResult(partial = false, complete = false))
    }

    @Test
    fun givenYearHasBeenUpdatedThenCardListenerIsNotifiedOfYearValidationResult() {
        val year = "29"
        given(dateView.getInsertedMonth()).willReturn("")
        given(dateView.getInsertedYear()).willReturn(year)
        given(cardValidator.canUpdate("", year)).willReturn(true)

        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(ValidationResult(partial = false, complete = false), null))
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))
        given(cardValidator.validateDate(null, year)).willReturn(ValidationResult(partial = false, complete = true))

        card.onUpdateDate(null, year)

        verify(cardListener).onDateUpdateValidationResult(null, ValidationResult(partial = false, complete = true), monthLengthFilter, yearLengthFilter)
        verify(cardListener).onValidationResult(ValidationResult(partial = false, complete = false))
    }

    @Test
    fun givenDateHasEndedUpdateAndNoCardValidatorHasBeenSetThenCardListenerIsNotNotified() {
        card.cardValidator = null

        card.onEndUpdateDate("01", "20")

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenDateHasEndedUpdateAndNoCardListenerHasBeenSetThenCardListenerIsNotNotified() {
        card.cardListener = null
        val month = "01"
        val year = "19"
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = false))

        card.onEndUpdateDate(month, year)

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenDateHasEndedUpdateThenCardListenerIsNotifiedOfResult() {
        val month = "01"
        val year = "19"
        val validationResult = ValidationResult(partial = false, complete = true)
        given(cardValidator.validateDate(month, year)).willReturn(validationResult)
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(ValidationResult(partial = false, complete = false), null))
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))

        card.onEndUpdateDate(month, year)

        verify(cardListener).onDateEndUpdateValidationResult(validationResult)
        verify(cardListener).onValidationResult(ValidationResult(partial = false, complete = false))
    }

    @Test
    fun givenMonthHasEndedUpdateThenCardListenerIsNotifiedOfResult() {
        val month = "01"
        val year = "19"
        val validationResult = ValidationResult(partial = false, complete = true)
        given(dateView.getInsertedYear()).willReturn(year)
        given(cardValidator.validateDate(month, year)).willReturn(validationResult)
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(ValidationResult(partial = false, complete = false), null))
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))

        card.onEndUpdateDate(month, null)

        verify(cardListener).onDateEndUpdateValidationResult(validationResult)
        verify(cardListener).onValidationResult(ValidationResult(partial = false, complete = false))
    }

    @Test
    fun givenYearHasEndedUpdateThenCardListenerIsNotifiedOfResult() {
        val month = "01"
        val year = "19"
        val validationResult = ValidationResult(partial = false, complete = true)
        given(dateView.getInsertedMonth()).willReturn(month)
        given(cardValidator.validateDate(month, year)).willReturn(validationResult)
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(ValidationResult(partial = false, complete = false), null))
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))

        card.onEndUpdateDate(null, year)

        verify(cardListener).onDateEndUpdateValidationResult(validationResult)
        verify(cardListener).onValidationResult(ValidationResult(partial = false, complete = false))
    }


}
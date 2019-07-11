package com.worldpay.access.checkout

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.validation.CardValidator
import com.worldpay.access.checkout.validation.ValidationResult
import com.worldpay.access.checkout.views.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class AccessCheckoutCardTest {

    private lateinit var card: Card
    private lateinit var panView: CardTextView
    private lateinit var cvvView: CardCVVText
    private lateinit var dateView: CardExpiryTextLayout
    private lateinit var cardValidator: CardValidator
    private lateinit var cardConfiguration: CardConfiguration
    private lateinit var cardListener: CardListener
    private lateinit var factory: CardFactory
    private lateinit var panLengthFilter: PANLengthFilter
    private lateinit var cvvLengthFilter: CVVLengthFilter
    private lateinit var dateLengthFilter: DateLengthFilter

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
        dateLengthFilter = mock()
        given(factory.getPANLengthFilter(cardValidator)).willReturn(panLengthFilter)
        given(factory.getCVVLengthFilter(cardValidator, panView)).willReturn(cvvLengthFilter)
        given(factory.getDateLengthFilter(cardConfiguration)).willReturn(dateLengthFilter)
        given(panView.getInsertedText()).willReturn(pan)
        given(cvvView.getInsertedText()).willReturn(cvv)
        given(dateView.getInsertedMonth()).willReturn(month)
        given(dateView.getInsertedYear()).willReturn(year)
        given(cardValidator.cardConfiguration).willReturn(cardConfiguration)
        card = AccessCheckoutCard(panView, cvvView, dateView, factory)
        card.cardListener = cardListener

        setCardValidator(card, cardValidator)
    }

    @Test
    fun givenOnlyDefaultConstructorArgs_ThenAccessCheckoutCardIsConstructed() {
        val card = AccessCheckoutCard(panView, cvvView, dateView)

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
        given(cardValidator.validateCVV(cvv, pan)).willReturn(
            Pair(
                ValidationResult(partial = false, complete = true),
                null
            )
        )
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = true))

        assertTrue(card.isValid())
    }

    @Test
    fun givenValidatorHasBeenSetOnTheCard_AndOnlyPANValid_ThenCardShouldNotBeValid() {
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(
            Pair(
                ValidationResult(partial = false, complete = false),
                null
            )
        )
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = false))

        assertFalse(card.isValid())
    }

    @Test
    fun givenValidatorHasBeenSetOnTheCard_AndOnlyPANAndCVVValid_ThenCardShouldNotBeValid() {
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(
            Pair(
                ValidationResult(partial = false, complete = true),
                null
            )
        )
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = false))

        assertFalse(card.isValid())
    }

    @Test
    fun givenValidatorHasBeenSetOnTheCard_AndOnlyPANAndExpiryValid_ThenCardShouldNotBeValid() {
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = true), null))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(
            Pair(
                ValidationResult(partial = false, complete = false),
                null
            )
        )
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = true))

        assertFalse(card.isValid())
    }

    @Test
    fun givenValidatorHasBeenSetOnTheCard_AndOnlyCVVValid_ThenCardShouldNotBeValid() {
        given(cardValidator.validatePAN(pan)).willReturn(
            Pair(
                ValidationResult(partial = false, complete = false),
                null
            )
        )
        given(cardValidator.validateCVV(cvv, pan)).willReturn(
            Pair(
                ValidationResult(partial = false, complete = true),
                null
            )
        )
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = false))

        assertFalse(card.isValid())
    }

    @Test
    fun givenValidatorHasBeenSetOnTheCard_AndOnlyCVVAndExpiryValid_ThenCardShouldNotBeValid() {
        given(cardValidator.validatePAN(pan)).willReturn(
            Pair(
                ValidationResult(partial = false, complete = false),
                null
            )
        )
        given(cardValidator.validateCVV(cvv, pan)).willReturn(
            Pair(
                ValidationResult(partial = false, complete = true),
                null
            )
        )
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = true))

        assertFalse(card.isValid())
    }

    @Test
    fun givenValidatorHasBeenSetOnTheCard_AndOnlyExpiryValid_ThenCardShouldNotBeValid() {
        given(cardValidator.validatePAN(pan)).willReturn(
            Pair(
                ValidationResult(partial = false, complete = false),
                null
            )
        )
        given(cardValidator.validateCVV(cvv, pan)).willReturn(
            Pair(
                ValidationResult(partial = false, complete = false),
                null
            )
        )
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = true))

        assertFalse(card.isValid())
    }

    @Test
    fun givenPANHasBeenUpdatedAndNoCardValidatorHasBeenSetThenCardListenerIsNotifiedOncePerFieldLengthFilter() {
        card.cardValidator = null

        card.onUpdatePAN("1234")

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenPANHasBeenUpdatedAndNoCardListenerHasBeenSetThenCardListenerIsNotifiedOncePerFieldLengthFilter() {
        val updatedPan = "1234"
        card.cardListener = null
        given(cardValidator.validatePAN(updatedPan)).willReturn(
            Pair(
                ValidationResult(partial = false, complete = true),
                null
            )
        )
        given(cardValidator.validateCVV(cvv, updatedPan)).willReturn(
            Pair(
                ValidationResult(
                    partial = false,
                    complete = true
                ), null
            )
        )

        card.onUpdatePAN(updatedPan)

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenPANHasBeenUpdatedToCompletelyValidThenCardListenerIsNotifiedOfResult() {
        val updatedPan = "1234"
        val cardBrand = CardBrand("test", emptyList(), null, emptyList())
        val panValidationResult = ValidationResult(partial = false, complete = true)
        val cvvValidationResult = ValidationResult(partial = false, complete = false)
        given(cardValidator.validatePAN(updatedPan)).willReturn(Pair(panValidationResult, cardBrand))
        given(cardValidator.validateCVV(cvv, updatedPan)).willReturn(Pair(cvvValidationResult, cardBrand))

        card.onUpdatePAN(updatedPan)

        verify(cardListener).onUpdate(panView, true)
        verify(cardListener).onUpdateCardBrand(cardBrand)
        verify(cardListener).onUpdateLengthFilter(panView, panLengthFilter)
        verify(cardListener).onUpdate(cvvView, false)
    }

    @Test
    fun givenPANHasBeenUpdatedToPartiallyValidThenCardListenerIsNotifiedOfResult() {
        val updatedPan = "1234"
        val cardBrand = CardBrand("test", emptyList(), null, emptyList())
        val panValidationResult = ValidationResult(partial = true, complete = false)
        val cvvValidationResult = ValidationResult(partial = false, complete = false)
        given(cardValidator.validatePAN(updatedPan)).willReturn(Pair(panValidationResult, cardBrand))
        given(cardValidator.validateCVV(cvv, updatedPan)).willReturn(Pair(cvvValidationResult, cardBrand))

        card.onUpdatePAN(updatedPan)

        verify(cardListener).onUpdate(panView, true)
        verify(cardListener).onUpdateCardBrand(cardBrand)
        verify(cardListener).onUpdateLengthFilter(panView, panLengthFilter)
        verify(cardListener).onUpdate(cvvView, false)
    }

    @Test
    fun givenPANHasBeenUpdatedToInvalidThenCardListenerIsNotifiedOfResult() {
        val updatedPan = "1234"
        val cardBrand = CardBrand("test", emptyList(), null, emptyList())
        val panValidationResult = ValidationResult(partial = false, complete = false)
        val cvvValidationResult = ValidationResult(partial = false, complete = false)
        given(cardValidator.validatePAN(updatedPan)).willReturn(Pair(panValidationResult, cardBrand))
        given(cardValidator.validateCVV(cvv, updatedPan)).willReturn(Pair(cvvValidationResult, cardBrand))

        card.onUpdatePAN(updatedPan)

        verify(cardListener).onUpdate(panView, false)
        verify(cardListener).onUpdateCardBrand(cardBrand)
        verify(cardListener).onUpdateLengthFilter(panView, panLengthFilter)
        verify(cardListener).onUpdate(cvvView, false)
    }

    @Test
    fun givenPANHasBeenUpdatedToPartiallyAndCompletelyValidThenCardListenerIsNotifiedOfResult() {
        val updatedPan = "1234"
        val cardBrand = CardBrand("test", emptyList(), null, emptyList())
        val panValidationResult = ValidationResult(partial = true, complete = true)
        val cvvValidationResult = ValidationResult(partial = false, complete = false)
        given(cardValidator.validatePAN(updatedPan)).willReturn(Pair(panValidationResult, cardBrand))
        given(cardValidator.validateCVV(cvv, updatedPan)).willReturn(Pair(cvvValidationResult, cardBrand))

        card.onUpdatePAN(updatedPan)

        verify(cardListener).onUpdate(panView, true)
        verify(cardListener).onUpdateCardBrand(cardBrand)
        verify(cardListener).onUpdateLengthFilter(panView, panLengthFilter)
        verify(cardListener).onUpdate(cvvView, false)
    }

    @Test
    fun givenCardValidatorIsUpdatedWithNoLengthFilterAndThenPANHasBeenUpdatedThenCardListenerIsOnlyNotifiedOfDefaultLengthFilterUpdate() {
        given(factory.getPANLengthFilter(cardValidator)).willReturn(null)
        setCardValidator(card, cardValidator)

        val updatedPan = "1234"
        val cardBrand = CardBrand("test", emptyList(), null, emptyList())
        val panValidationResult = ValidationResult(partial = true, complete = true)
        val cvvValidationResult = ValidationResult(partial = false, complete = false)
        given(cardValidator.validatePAN(updatedPan)).willReturn(Pair(panValidationResult, cardBrand))
        given(cardValidator.validateCVV(cvv, updatedPan)).willReturn(Pair(cvvValidationResult, cardBrand))

        card.onUpdatePAN(updatedPan)

        verify(cardListener).onUpdate(panView, true)
        verify(cardListener).onUpdateCardBrand(cardBrand)
        verify(cardListener, times(0)).onUpdateLengthFilter(eq(panView), any())
        verify(cardListener).onUpdate(cvvView, false)
    }

    @Test
    fun givenPANHasEndedUpdateAndNoCardValidatorHasBeenSetThenCardListenerIsNotifiedOncePerFieldLengthFilter() {
        card.cardValidator = null

        card.onEndUpdatePAN("1234")

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenPANHasEndedUpdateAndNoCardListenerHasBeenSetThenCardListenerIsNotifiedOncePerFieldLengthFilter() {
        val updatedPan = "1234"
        card.cardListener = null
        given(cardValidator.validatePAN(updatedPan)).willReturn(
            Pair(
                ValidationResult(partial = false, complete = true),
                null
            )
        )
        given(cardValidator.validateCVV(cvv, updatedPan)).willReturn(
            Pair(
                ValidationResult(
                    partial = false,
                    complete = true
                ), null
            )
        )

        card.onEndUpdatePAN(updatedPan)

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenPANHasEndedUpdateThenCardListenerIsNotifiedOfResult() {
        val updatedPan = "1234"
        val cardBrand = CardBrand("test", emptyList(), null, emptyList())
        val panValidationResult = ValidationResult(partial = false, complete = true)
        val cvvValidationResult = ValidationResult(partial = false, complete = false)
        given(cardValidator.validatePAN(updatedPan)).willReturn(Pair(panValidationResult, cardBrand))
        given(cardValidator.validateCVV(cvv, updatedPan)).willReturn(Pair(cvvValidationResult, cardBrand))

        card.onEndUpdatePAN(updatedPan)

        verify(cardListener).onUpdate(panView, true)
        verify(cardListener).onUpdateCardBrand(cardBrand)
        verify(cardListener).onUpdate(cvvView, false)
    }

    @Test
    fun givenCVVHasBeenUpdatedAndNoCardValidatorHasBeenSetThenCardListenerIsNotNotified() {
        card.cardValidator = null

        card.onUpdateCVV("1234")

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenCVVHasBeenUpdatedAndNoCardListenerHasBeenSetThenCardListenerIsNotifiedOncePerFieldLengthFilter() {
        val updatedCVV = "1234"
        card.cardListener = null
        given(cardValidator.validateCVV(updatedCVV, pan)).willReturn(
            Pair(
                ValidationResult(
                    partial = false,
                    complete = true
                ), null
            )
        )

        card.onUpdateCVV(updatedCVV)

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenCVVHasBeenUpdatedToCompletelyValidThenCardListenerIsNotifiedOfResult() {
        val updatedCVV = "1234"
        val cardBrand = CardBrand("test", emptyList(), null, emptyList())
        val cvvValidationResult = ValidationResult(partial = false, complete = true)
        given(cardValidator.validateCVV(updatedCVV, pan)).willReturn(Pair(cvvValidationResult, cardBrand))

        card.onUpdateCVV(updatedCVV)

        verify(cardListener).onUpdate(cvvView, true)
        verify(cardListener).onUpdateLengthFilter(cvvView, cvvLengthFilter)
    }

    @Test
    fun givenCVVHasBeenUpdatedToPartiallyValidThenCardListenerIsNotifiedOfResult() {
        val updatedCVV = "1234"
        val cardBrand = CardBrand("test", emptyList(), null, emptyList())
        val cvvValidationResult = ValidationResult(partial = true, complete = false)
        given(cardValidator.validateCVV(updatedCVV, pan)).willReturn(Pair(cvvValidationResult, cardBrand))

        card.onUpdateCVV(updatedCVV)

        verify(cardListener).onUpdate(cvvView, true)
        verify(cardListener).onUpdateLengthFilter(cvvView, cvvLengthFilter)
    }

    @Test
    fun givenCVVHasBeenUpdatedToInvalidThenCardListenerIsNotifiedOfResult() {
        val updatedCVV = "1234"
        val cardBrand = CardBrand("test", emptyList(), null, emptyList())
        val cvvValidationResult = ValidationResult(partial = false, complete = false)
        given(cardValidator.validateCVV(updatedCVV, pan)).willReturn(Pair(cvvValidationResult, cardBrand))

        card.onUpdateCVV(updatedCVV)

        verify(cardListener).onUpdate(cvvView, false)
        verify(cardListener).onUpdateLengthFilter(cvvView, cvvLengthFilter)
    }

    @Test
    fun givenCVVHasBeenUpdatedToPartiallyAndCompletelyValidThenCardListenerIsNotifiedOfResult() {
        val updatedCVV = "1234"
        val cardBrand = CardBrand("test", emptyList(), null, emptyList())
        val cvvValidationResult = ValidationResult(partial = true, complete = true)
        given(cardValidator.validateCVV(updatedCVV, pan)).willReturn(Pair(cvvValidationResult, cardBrand))

        card.onUpdateCVV(updatedCVV)

        verify(cardListener).onUpdate(cvvView, true)
        verify(cardListener).onUpdateLengthFilter(cvvView, cvvLengthFilter)
    }

    @Test
    fun givenCardValidatorIsUpdatedWithNoLengthFilterAndThenCVVHasBeenUpdatedThenCardListenerIsOnlyNotifiedOfDefaultLengthFilterUpdate() {
        given(factory.getCVVLengthFilter(cardValidator, panView)).willReturn(null)
        setCardValidator(card, cardValidator)

        val updatedCVV = "1234"
        val cardBrand = CardBrand("test", emptyList(), null, emptyList())
        val cvvValidationResult = ValidationResult(partial = true, complete = false)
        given(cardValidator.validateCVV(updatedCVV, pan)).willReturn(Pair(cvvValidationResult, cardBrand))

        card.onUpdateCVV(updatedCVV)

        verify(cardListener).onUpdate(cvvView, true)
        verify(cardListener, times(0)).onUpdateLengthFilter(eq(cvvView), any())
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
        given(cardValidator.validateCVV(updatedCVV, pan)).willReturn(
            Pair(
                ValidationResult(
                    partial = false,
                    complete = true
                ), null
            )
        )
        card.onEndUpdateCVV(updatedCVV)

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenCVVHasEndedUpdateThenCardListenerIsNotifiedOfResult() {
        val updatedCVV = "1234"
        val cardBrand = CardBrand("test", emptyList(), null, emptyList())
        val cvvValidationResult = ValidationResult(partial = false, complete = false)
        given(cardValidator.validateCVV(updatedCVV, pan)).willReturn(Pair(cvvValidationResult, cardBrand))

        card.onEndUpdateCVV(updatedCVV)

        verify(cardListener).onUpdate(cvvView, false)
    }

    @Test
    fun givenDateHasBeenUpdatedAndNoCardValidatorHasBeenSetThenCardListenerIsNotNotified() {
        card.cardValidator = null

        card.onUpdateDate("01", "20")

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenDateHasBeenUpdatedAndNoCardListenerHasBeenSetThenCardListenerIsNotifiedOncePerFieldLengthFilter() {
        card.cardListener = null
        val month = "01"
        val year = "19"
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = false))

        card.onUpdateDate(month, year)

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenCardValidatorIsUpdatedWithNoLengthFilterAndThenDateHasBeenUpdatedThenCardListenerIsOnlyNotifiedOfDefaultLengthFilterUpdate() {
        given(factory.getDateLengthFilter(cardConfiguration)).willReturn(null)
        setCardValidator(card, cardValidator)

        val month = "01"
        val year = "19"
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = false))

        card.onUpdateDate(month, year)

        verify(cardListener).onUpdate(dateView, false)
        verify(cardListener, times(0)).onUpdateLengthFilter(eq(dateView), any())
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

        card.onUpdateDate(month, null)

        verify(cardListener).onUpdate(dateView, false)
        verify(cardListener).onUpdateLengthFilter(dateView, dateLengthFilter)
    }

    @Test
    fun givenMonthHasBeenUpdatedToCompleteMonthThenCardListenerIsNotifiedOfValidationResult() {
        val month = "01"
        val year = "1"
        given(dateView.getInsertedMonth()).willReturn(month)
        given(dateView.getInsertedYear()).willReturn(year)
        given(cardValidator.canUpdate(month, year)).willReturn(true)

        val monthValidationResult = ValidationResult(partial = false, complete = true)
        given(cardValidator.validateDate(month, null)).willReturn(monthValidationResult)

        card.onUpdateDate(month, null)

        verify(cardListener).onUpdate(dateView, true)
        verify(cardListener).onUpdateLengthFilter(dateView, dateLengthFilter)
    }

    @Test
    fun givenMonthHasBeenUpdatedToPartiallyCompleteMonthThenCardListenerIsNotifiedOfValidationResult() {
        val month = "0"
        val year = "1"
        given(dateView.getInsertedMonth()).willReturn(month)
        given(dateView.getInsertedYear()).willReturn(year)
        given(cardValidator.canUpdate(month, year)).willReturn(true)

        val monthValidationResult = ValidationResult(partial = true, complete = false)
        given(cardValidator.validateDate(month, null)).willReturn(monthValidationResult)

        card.onUpdateDate(month, null)

        verify(cardListener).onUpdate(dateView, true)
        verify(cardListener).onUpdateLengthFilter(dateView, dateLengthFilter)
    }

    @Test
    fun givenMonthHasBeenUpdatedToInvalidMonthThenCardListenerIsNotifiedOfValidationResult() {
        val month = "13"
        val year = "1"
        given(dateView.getInsertedMonth()).willReturn(month)
        given(dateView.getInsertedYear()).willReturn(year)
        given(cardValidator.canUpdate(month, year)).willReturn(true)

        val monthValidationResult = ValidationResult(partial = false, complete = false)
        given(cardValidator.validateDate(month, null)).willReturn(monthValidationResult)

        card.onUpdateDate(month, null)

        verify(cardListener).onUpdate(dateView, false)
        verify(cardListener).onUpdateLengthFilter(dateView, dateLengthFilter)
    }

    @Test
    fun givenMonthHasBeenUpdatedThenCardListenerIsNotifiedOfMonthValidationResult() {
        val month = "08"
        given(dateView.getInsertedYear()).willReturn("")
        given(dateView.getInsertedMonth()).willReturn(month)
        given(cardValidator.canUpdate(month, "")).willReturn(true)

        given(cardValidator.validateDate(month, null)).willReturn(ValidationResult(partial = false, complete = true))

        card.onUpdateDate(month, null)

        verify(cardListener).onUpdate(dateView, true)
        verify(cardListener).onUpdateLengthFilter(dateView, dateLengthFilter)
    }

    @Test
    fun givenYearHasBeenUpdatedThenCardListenerIsNotifiedOfYearValidationResult() {
        val year = "29"
        given(dateView.getInsertedMonth()).willReturn("")
        given(dateView.getInsertedYear()).willReturn(year)
        given(cardValidator.canUpdate("", year)).willReturn(true)

        given(cardValidator.validateDate(null, year)).willReturn(ValidationResult(partial = false, complete = true))

        card.onUpdateDate(null, year)

        verify(cardListener).onUpdate(dateView, true)
        verify(cardListener).onUpdateLengthFilter(dateView, dateLengthFilter)
    }

    @Test
    fun givenYearHasBeenUpdatedToPartiallyValidThenCardListenerIsNotifiedOfYearValidationResult() {
        val year = "2"
        given(dateView.getInsertedMonth()).willReturn("")
        given(dateView.getInsertedYear()).willReturn(year)
        given(cardValidator.canUpdate("", year)).willReturn(true)

        given(cardValidator.validateDate(null, year)).willReturn(ValidationResult(partial = true, complete = false))

        card.onUpdateDate(null, year)

        verify(cardListener).onUpdate(dateView, true)
        verify(cardListener).onUpdateLengthFilter(dateView, dateLengthFilter)
    }

    @Test
    fun givenYearHasBeenUpdatedToInvalidThenCardListenerIsNotifiedOfYearValidationResult() {
        val year = "18"
        given(dateView.getInsertedMonth()).willReturn("")
        given(dateView.getInsertedYear()).willReturn(year)
        given(cardValidator.canUpdate("", year)).willReturn(true)

        given(cardValidator.validateDate(null, year)).willReturn(ValidationResult(partial = false, complete = false))

        card.onUpdateDate(null, year)

        verify(cardListener).onUpdate(dateView, false)
        verify(cardListener).onUpdateLengthFilter(dateView, dateLengthFilter)
    }

    @Test
    fun givenDateHasEndedUpdateAndNoCardValidatorHasBeenSetThenCardListenerIsNotifiedOncePerFieldLengthFilter() {
        card.cardValidator = null

        card.onEndUpdateDate("01", "20")

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenDateHasEndedUpdateAndNoCardListenerHasBeenSetThenCardListenerIsNotifiedOncePerFieldLengthFilter() {
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

        card.onEndUpdateDate(month, year)

        verify(cardListener).onUpdate(dateView, true)
    }

    @Test
    fun givenMonthHasEndedUpdateThenCardListenerIsNotifiedOfResult() {
        val month = "01"
        val year = "19"
        val validationResult = ValidationResult(partial = false, complete = true)
        given(dateView.getInsertedYear()).willReturn(year)
        given(cardValidator.validateDate(month, year)).willReturn(validationResult)

        card.onEndUpdateDate(month, null)

        verify(cardListener).onUpdate(dateView, true)
    }

    @Test
    fun givenYearHasEndedUpdateThenCardListenerIsNotifiedOfResult() {
        val month = "01"
        val year = "19"
        val validationResult = ValidationResult(partial = false, complete = true)
        given(dateView.getInsertedMonth()).willReturn(month)
        given(cardValidator.validateDate(month, year)).willReturn(validationResult)

        card.onEndUpdateDate(null, year)

        verify(cardListener).onUpdate(dateView, true)
    }

    @Test
    fun givenAValidatorIsUpdatedToNull_ThenCardListenerIsNotNotifiedOfUpdatedLengthFilters() {
        given(factory.getPANLengthFilter(null)).willReturn(null)
        given(factory.getCVVLengthFilter(null, panView)).willReturn(null)
        given(factory.getDateLengthFilter(null)).willReturn(null)

        card.cardValidator = null

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenAValidatorIsUpdated_ThenCardListenerIsNotNotifiedIfNotSet() {
        card.cardListener = null
        val validator = mock<CardValidator>()
        given(validator.cardConfiguration).willReturn(cardConfiguration)
        given(factory.getPANLengthFilter(validator)).willReturn(panLengthFilter)
        given(factory.getCVVLengthFilter(validator, panView)).willReturn(cvvLengthFilter)
        given(factory.getDateLengthFilter(cardConfiguration)).willReturn(dateLengthFilter)

        setCardValidator(card, validator)

        verifyZeroInteractions(cardListener)
    }

    @Test
    fun givenCardConfiguration_ThenAccessCheckoutCardDefaultFactoryShouldBuildLengthFilters() {
        val accessCheckoutCardDefaultFactory = AccessCheckoutCardDefaultFactory()

        assertNotNull(accessCheckoutCardDefaultFactory.getPANLengthFilter(cardValidator))
        assertNotNull(accessCheckoutCardDefaultFactory.getCVVLengthFilter(cardValidator, panView))
        assertNotNull(accessCheckoutCardDefaultFactory.getDateLengthFilter(cardConfiguration))
    }

    @Test
    fun givenEmptyCardConfiguration_ThenAccessCheckoutCardDefaultFactoryShouldReturnEmptyFilters() {
        val accessCheckoutCardDefaultFactory = AccessCheckoutCardDefaultFactory()

        assertNull(accessCheckoutCardDefaultFactory.getPANLengthFilter(null))
        assertNull(accessCheckoutCardDefaultFactory.getCVVLengthFilter(null, panView))
        assertNull(accessCheckoutCardDefaultFactory.getDateLengthFilter(null))
    }

    @Test
    fun givenCardValidatorIsSetThenACardShouldRevalidatePartialPANFieldIfOnFocus() {
        given(panView.hasFocus()).willReturn(true)
        val validationResult = ValidationResult(partial = true, complete = false)
        val cardBrand = CardBrand("test", emptyList(), null, emptyList())
        given(cardValidator.validatePAN(pan)).willReturn(Pair(validationResult, cardBrand))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(validationResult, cardBrand))
        given(cardValidator.validateDate(month, year)).willReturn(validationResult)

        card = AccessCheckoutCard(panView, cvvView, dateView, factory)
        card.cardListener = cardListener
        card.cardValidator = cardValidator

        verify(cardListener).onUpdate(panView, true)
        verify(cardListener).onUpdateCardBrand(cardBrand)
    }

    @Test
    fun givenCardValidatorIsSetThenACardShouldRevalidateCompletePANFieldIfOnFocus() {
        given(panView.hasFocus()).willReturn(false)
        val validationResult = ValidationResult(partial = true, complete = false)
        val cardBrand = CardBrand("test", emptyList(), null, emptyList())
        given(cardValidator.validatePAN(pan)).willReturn(Pair(validationResult, cardBrand))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(validationResult, cardBrand))
        given(cardValidator.validateDate(month, year)).willReturn(validationResult)

        card = AccessCheckoutCard(panView, cvvView, dateView, factory)
        card.cardListener = cardListener
        card.cardValidator = cardValidator

        verify(cardListener).onUpdate(panView, false)
    }

    @Test
    fun givenCardValidatorIsSetThenACardShouldRevalidatePartialCVVFieldIfOnFocus() {
        given(cvvView.hasFocus()).willReturn(true)
        val validationResult = ValidationResult(partial = true, complete = false)
        val cardBrand = CardBrand("test", emptyList(), null, emptyList())
        given(cardValidator.validatePAN(pan)).willReturn(Pair(validationResult, cardBrand))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(validationResult, cardBrand))
        given(cardValidator.validateDate(month, year)).willReturn(validationResult)

        card = AccessCheckoutCard(panView, cvvView, dateView, factory)
        card.cardListener = cardListener
        card.cardValidator = cardValidator

        verify(cardListener).onUpdate(cvvView, true)
    }

    @Test
    fun givenCardValidatorIsSetThenACardShouldRevalidateCompleteCVVFieldIfOnFocus() {
        given(cvvView.hasFocus()).willReturn(false)
        val validationResult = ValidationResult(partial = true, complete = false)
        val cardBrand = CardBrand("test", emptyList(), null, emptyList())
        given(cardValidator.validatePAN(pan)).willReturn(Pair(validationResult, cardBrand))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(validationResult, cardBrand))
        given(cardValidator.validateDate(month, year)).willReturn(validationResult)

        card = AccessCheckoutCard(panView, cvvView, dateView, factory)
        card.cardListener = cardListener
        card.cardValidator = cardValidator

        verify(cardListener, times(2)).onUpdate(cvvView, false)
    }

    @Test
    fun givenCardValidatorIsSetThenACardShouldRevalidatePartialDateFieldIfOnFocus() {
        given(dateView.hasFocus()).willReturn(true)
        val validationResult = ValidationResult(partial = true, complete = false)
        given(dateView.getInsertedMonth()).willReturn("")
        given(cardValidator.canUpdate("", year)).willReturn(true)
        given(cardValidator.validatePAN(pan)).willReturn(Pair(validationResult, null))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(validationResult, null))
        given(cardValidator.validateDate(null, year)).willReturn(validationResult)

        card = AccessCheckoutCard(panView, cvvView, dateView, factory)
        card.cardListener = cardListener
        card.cardValidator = cardValidator

        verify(cardListener).onUpdate(dateView, true)
    }

    @Test
    fun givenCardValidatorIsSetThenACardShouldRevalidateCompleteDateFieldIfOnFocus() {
        given(cvvView.hasFocus()).willReturn(false)
        val validationResult = ValidationResult(partial = true, complete = false)
        given(cardValidator.validatePAN(pan)).willReturn(Pair(validationResult, null))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(validationResult, null))
        given(cardValidator.validateDate(month, year)).willReturn(validationResult)

        card = AccessCheckoutCard(panView, cvvView, dateView, factory)
        card.cardListener = cardListener
        card.cardValidator = cardValidator

        verify(cardListener).onUpdate(dateView, false)
    }

    private fun setCardValidator(card: Card, cardValidator: CardValidator) {
        given(cardValidator.validatePAN(pan)).willReturn(Pair(ValidationResult(partial = false, complete = false), null))
        given(cardValidator.validateCVV(cvv, pan)).willReturn(Pair(ValidationResult(partial = false, complete = false), null))
        given(cardValidator.validateDate(month, year)).willReturn(ValidationResult(partial = false, complete = false))
        card.cardValidator = cardValidator
        reset(cardValidator)
        reset(cardListener)
    }

}
package com.worldpay.access.checkout.validation

import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.validation.watchers.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class CardValidationControllerTest {

    private val baseUrl: String = "base url"

    // fields
    private val pan = mock<EditText>()
    private val expiryMonth = mock<EditText>()
    private val expiryYear = mock<EditText>()
    private val cvv = mock<EditText>()

    // watchers
    private val textWatcherFactory = mock<TextWatcherFactory>()
    private val panTextWatcher = mock<PANTextWatcher>()
    private val expiryMonthTextWatcher = mock<ExpiryMonthTextWatcher>()
    private val expiryYearTextWatcher = mock<ExpiryYearTextWatcher>()
    private val cvvTextWatcher = mock<CVVTextWatcher>()

    private val cardConfigurationClient = mock<CardConfigurationClient>()

    private lateinit var callbackCaptor: KArgumentCaptor<Callback<CardConfiguration>>

    @Before
    fun setup() {
        callbackCaptor = argumentCaptor()
    }

    @Test
    fun `should make call to retrieve remote card configuration upon initialisation`() {
        createAccessCheckoutValidationController()

        verify(cardConfigurationClient).getCardConfiguration(eq(baseUrl), callbackCaptor.capture())

        assertNotNull(callbackCaptor.firstValue)
    }

    @Test
    fun `should add text changed listeners to each of the fields provided upon initialisation`() {
        createAccessCheckoutValidationController()

        verifyTextWatchersAreCreated(CARD_CONFIG_NO_BRAND)

        // verify that text changed listeners are added
        verify(pan).addTextChangedListener(panTextWatcher)
        verify(expiryMonth).addTextChangedListener(expiryMonthTextWatcher)
        verify(expiryYear).addTextChangedListener(expiryYearTextWatcher)
        verify(cvv).addTextChangedListener(cvvTextWatcher)

        verifyNoMoreInteractions(
            textWatcherFactory,
            pan,
            expiryMonth,
            expiryYear,
            cvv
        )
    }

    @Test
    fun `should reset the text changed listeners when remote card configuration is retrieved`() {
        createAccessCheckoutValidationController()

        verifyTextWatchersAreCreated(CARD_CONFIG_NO_BRAND)
        verify(cardConfigurationClient).getCardConfiguration(eq(baseUrl), callbackCaptor.capture())

        // call the callback with a successful card config
        mockTextWatcherCreation(CARD_CONFIG_BASIC)
        assertNotNull(callbackCaptor.firstValue)
        callbackCaptor.firstValue.onResponse(null, CARD_CONFIG_BASIC)

        // verify that text changed listeners are removed
        verify(pan).removeTextChangedListener(panTextWatcher)
        verify(expiryMonth).removeTextChangedListener(expiryMonthTextWatcher)
        verify(expiryYear).removeTextChangedListener(expiryYearTextWatcher)
        verify(cvv).removeTextChangedListener(cvvTextWatcher)

        // verify that the new text watchers are being created
        verifyTextWatchersAreCreated(CARD_CONFIG_BASIC)

        // verify that text changed listeners are added twice (once on initialisation and again after being removed)
        verify(pan, times(2)).addTextChangedListener(panTextWatcher)
        verify(expiryMonth, times(2)).addTextChangedListener(expiryMonthTextWatcher)
        verify(expiryYear, times(2)).addTextChangedListener(expiryYearTextWatcher)
        verify(cvv, times(2)).addTextChangedListener(cvvTextWatcher)

        verifyNoMoreInteractions(
            textWatcherFactory,
            pan,
            expiryMonth,
            expiryYear,
            cvv
        )
    }

    @Test
    fun `should not do anything when remote card configuration is errors`() {
        createAccessCheckoutValidationController()

        verifyTextWatchersAreCreated(CARD_CONFIG_NO_BRAND)

        // verify that text changed listeners are added
        verify(pan).addTextChangedListener(panTextWatcher)
        verify(expiryMonth).addTextChangedListener(expiryMonthTextWatcher)
        verify(expiryYear).addTextChangedListener(expiryYearTextWatcher)
        verify(cvv).addTextChangedListener(cvvTextWatcher)

        verify(cardConfigurationClient).getCardConfiguration(eq(baseUrl), callbackCaptor.capture())

        // call the callback with a successful card config
        assertNotNull(callbackCaptor.firstValue)
        callbackCaptor.firstValue.onResponse(Exception(), null)

        verifyNoMoreInteractions(
            textWatcherFactory,
            pan,
            expiryMonth,
            expiryYear,
            cvv
        )
    }

    private fun createAccessCheckoutValidationController() {
        mockTextWatcherCreation(CARD_CONFIG_NO_BRAND)

        CardValidationController(
            panEditText = pan,
            expiryMonthEditText = expiryMonth,
            expiryYearEditText = expiryYear,
            cvvEditText = cvv,
            baseUrl = baseUrl,
            cardConfigurationClient = cardConfigurationClient,
            textWatcherFactory = textWatcherFactory
        )
    }

    private fun mockTextWatcherCreation(cardConfiguration: CardConfiguration) {
        given(textWatcherFactory.createPanTextWatcher(pan, cardConfiguration)).willReturn(panTextWatcher)
        given(textWatcherFactory.createExpiryMonthTextWatcher(expiryMonth, cardConfiguration)).willReturn(expiryMonthTextWatcher)
        given(textWatcherFactory.createExpiryYearTextWatcher(expiryYear, cardConfiguration)).willReturn(expiryYearTextWatcher)
        given(textWatcherFactory.createCvvTextWatcher(cvv, pan, cardConfiguration)).willReturn(cvvTextWatcher)
    }

    private fun verifyTextWatchersAreCreated(cardConfiguration: CardConfiguration) {
        verify(textWatcherFactory).createPanTextWatcher(pan, cardConfiguration)
        verify(textWatcherFactory).createExpiryMonthTextWatcher(expiryMonth, cardConfiguration)
        verify(textWatcherFactory).createExpiryYearTextWatcher(expiryYear, cardConfiguration)
        verify(textWatcherFactory).createCvvTextWatcher(cvv, pan, cardConfiguration)
    }

}

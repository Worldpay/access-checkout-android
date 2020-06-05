package com.worldpay.access.checkout.validation.controller

import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.validation.watchers.CVVTextWatcher
import com.worldpay.access.checkout.validation.watchers.TextWatcherFactory
import org.junit.Before
import org.junit.Test

class CvvDetailsValidationControllerTest {

    // fields
    private val cvv = mock<EditText>()

    // watchers
    private val textWatcherFactory = mock<TextWatcherFactory>()
    private val cvvTextWatcher = mock<CVVTextWatcher>()

    private lateinit var callbackCaptor: KArgumentCaptor<Callback<CardConfiguration>>

    @Before
    fun setup() {
        callbackCaptor = argumentCaptor()
    }

    @Test
    fun `should add text changed listeners to each of the fields provided upon initialisation`() {
        createAccessCheckoutValidationController()

        verifyTextWatchersAreCreated(CARD_CONFIG_NO_BRAND)

        // verify that text changed listeners are added
        verify(cvv).addTextChangedListener(cvvTextWatcher)

        verifyNoMoreInteractions(
            textWatcherFactory,
            cvv
        )
    }

    private fun createAccessCheckoutValidationController() {
        mockTextWatcherCreation(CARD_CONFIG_NO_BRAND)

        CvvDetailsValidationController(
            cvvEditText = cvv,
            textWatcherFactory = textWatcherFactory
        )
    }

    private fun mockTextWatcherCreation(cardConfiguration: CardConfiguration) {
        given(textWatcherFactory.createCvvTextWatcher(cvv, null, cardConfiguration)).willReturn(cvvTextWatcher)
    }

    private fun verifyTextWatchersAreCreated(cardConfiguration: CardConfiguration) {
        verify(textWatcherFactory).createCvvTextWatcher(cvv, null, cardConfiguration)
    }

}

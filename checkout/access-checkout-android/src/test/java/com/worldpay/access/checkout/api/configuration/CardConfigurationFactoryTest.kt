package com.worldpay.access.checkout.api.configuration

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.Card
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.validation.AccessCheckoutCardValidator
import com.worldpay.access.checkout.validation.CardValidator
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import kotlin.test.assertNull

class CardConfigurationFactoryTest {

    private lateinit var card: Card
    private lateinit var cardValidator: AccessCheckoutCardValidator
    private lateinit var client: CardConfigurationClient
    private lateinit var callbackObject: Callback<CardConfiguration>
    private lateinit var cardConfiguration: CardConfiguration

    private val baseURL = "http://localhost"

    @Before
    fun setup() {
        card = mock(Card::class.java)
        cardValidator = mock(AccessCheckoutCardValidator::class.java)
        client = mock(CardConfigurationClient::class.java)
        callbackObject = mock()
        cardConfiguration = mock(CardConfiguration::class.java)
    }

    @Test
    fun shouldBeAbleToSetCardValidatorWithConfigurationOnSuccessfulCall() {
        val callbackCaptor = argumentCaptor<Callback<CardConfiguration>>()
        val cardConfigurationClient = mock<CardConfigurationClient>()

        CardConfigurationFactory.getRemoteCardConfiguration(card, baseURL, cardConfigurationClient)

        verify(cardConfigurationClient).getCardConfiguration(eq(baseURL), callbackCaptor.capture())

        val configuration = CardConfiguration(brands = listOf(CardBrand(name = "test")
        ))
        callbackCaptor.firstValue.onResponse(null, configuration)

        val cardValidatorCaptor = argumentCaptor<CardValidator>()

        verify(card, times(2)).cardValidator = cardValidatorCaptor.capture()

        assertNull(cardValidatorCaptor.firstValue.cardConfiguration)
        assertEquals(configuration, cardValidatorCaptor.secondValue.cardConfiguration)
    }

    @Test
    fun givenAnApiErrorCardShouldNotBeSetWithValidator() {
        val callbackCaptor = argumentCaptor<Callback<CardConfiguration>>()
        val cardConfigurationClient = mock<CardConfigurationClient>()

        CardConfigurationFactory.getRemoteCardConfiguration(card, baseURL, cardConfigurationClient)

        verify(cardConfigurationClient).getCardConfiguration(eq(baseURL), callbackCaptor.capture())
        val error = Exception()
        callbackCaptor.firstValue.onResponse(error, null)

        val cardValidatorCaptor = argumentCaptor<CardValidator>()

        verify(card, atMost(1)).cardValidator = cardValidatorCaptor.capture()

        assertNull(cardValidatorCaptor.firstValue.cardConfiguration)
    }

    @Test
    fun givenTheFactoryReceivesNoExternalClientWillInstantiateOne() {
        CardConfigurationFactory.getRemoteCardConfiguration(card, baseURL)
    }

}
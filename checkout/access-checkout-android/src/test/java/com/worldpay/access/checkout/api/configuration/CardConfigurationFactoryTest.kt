package com.worldpay.access.checkout.api.configuration

import com.worldpay.access.checkout.Card
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.testutils.argumentCaptor
import com.worldpay.access.checkout.testutils.capture
import com.worldpay.access.checkout.testutils.mock
import com.worldpay.access.checkout.testutils.typeSafeEq
import com.worldpay.access.checkout.validation.AccessCheckoutCardValidator
import com.worldpay.access.checkout.validation.CardValidator
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito.*


class CardConfigurationFactoryTest {


    private lateinit var card: Card
    private lateinit var cardValidator: AccessCheckoutCardValidator
    private lateinit var client: CardConfigurationClient
    private lateinit var callbackObject: Callback<CardConfiguration>
    private lateinit var cardConfiguration: CardConfiguration
    val baseURL = "http://localhost"
    @Captor
    private lateinit var callbackCaptor: ArgumentCaptor<Callback<CardConfiguration>>
    @Captor
    private lateinit var cardValidatorCaptor: ArgumentCaptor<CardValidator>

    @Before
    fun setup() {
        card = mock(Card::class.java)
        cardValidator = mock(AccessCheckoutCardValidator::class.java)
        client = mock(CardConfigurationClient::class.java)
        callbackObject = mock()
        cardConfiguration = mock(CardConfiguration::class.java)
    }

    @Test
    fun givenACardShouldFetchRemoteConfiguration() {

        callbackCaptor = argumentCaptor<Callback<CardConfiguration>>()
        val cardConfigurationClient = mock<CardConfigurationClient>()

        CardConfigurationFactory.getRemoteCardConfiguration(card, baseURL, cardConfigurationClient)
        verify(cardConfigurationClient).getCardConfiguration(typeSafeEq(baseURL), capture(callbackCaptor))
        val configuration = CardConfiguration(brands = listOf(CardBrand("test", "test", null, listOf())))
        callbackCaptor.value.onResponse(null, configuration)
        cardValidatorCaptor = argumentCaptor()

        verify(card, atLeastOnce()).cardValidator = capture(cardValidatorCaptor)
        val cardValidatorCaptured = cardValidatorCaptor.value
        assertEquals(configuration, cardValidatorCaptured.cardConfiguration)
    }

    @Test
    fun givenAnApiErrorCardShouldNotBeSetWithValidator() {

        callbackCaptor = argumentCaptor<Callback<CardConfiguration>>()
        val cardConfigurationClient = mock<CardConfigurationClient>()

        CardConfigurationFactory.getRemoteCardConfiguration(card, baseURL, cardConfigurationClient)

        verify(cardConfigurationClient).getCardConfiguration(typeSafeEq(baseURL), capture(callbackCaptor))
        val error = Exception()
        callbackCaptor.value.onResponse(error, null)
        cardValidatorCaptor = argumentCaptor()
        verify(card, never()).cardValidator = capture(cardValidatorCaptor)
    }

    @Test
    fun givenTheFactoryReceivesNoExternalClientWillInstantiateOne() {
        CardConfigurationFactory.getRemoteCardConfiguration(card, baseURL)
    }

}
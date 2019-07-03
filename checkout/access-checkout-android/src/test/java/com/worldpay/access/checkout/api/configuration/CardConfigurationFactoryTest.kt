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
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito
import org.mockito.Captor
import org.mockito.Mockito.*
import java.lang.NullPointerException
import kotlin.test.*


class CardConfigurationFactoryTest {


    lateinit var card: Card
    lateinit var cardValidator: AccessCheckoutCardValidator
    lateinit var client: CardConfigurationClient
    lateinit var callbackObject: Callback<CardConfiguration>
    lateinit var cardConfiguration: CardConfiguration
    val baseURL = "http://localhost"

    @Before
    fun setup() {
        card = mock(Card::class.java)
        cardValidator = mock(AccessCheckoutCardValidator::class.java)
        client = mock(CardConfigurationClient::class.java)
        callbackObject = mock()
        cardConfiguration = mock(CardConfiguration::class.java)

    }

    @Captor private lateinit var captor: ArgumentCaptor<Callback<CardConfiguration>>
    @Captor private lateinit var captor2: ArgumentCaptor<CardValidator>

    @Test
    fun givenACardShouldFetchRemoteConfiguration() {

        captor = argumentCaptor<Callback<CardConfiguration>>()

        val cardConfigurationClient = mock<CardConfigurationClient>()

        CardConfigurationFactory.getRemoteCardValidatorConfiguration(card, baseURL, cardConfigurationClient)

        verify(cardConfigurationClient).getCardConfiguration(typeSafeEq(baseURL), capture(captor))

        val configuration = CardConfiguration(brands = listOf(CardBrand("test", "test", null, listOf())))
        captor.value.onResponse(null, configuration)

        captor2 = argumentCaptor()

        verify(card, atLeastOnce()).cardValidator = capture(captor2)

        val cardValidatorCaptured = captor2.value

        assertEquals(configuration, cardValidatorCaptured.cardConfiguration)

    }

    @Test
    fun givenAnApiErrorCardShouldNotBeSetWithValidator() {

        captor = argumentCaptor<Callback<CardConfiguration>>()

        val cardConfigurationClient = mock<CardConfigurationClient>()

        CardConfigurationFactory.getRemoteCardValidatorConfiguration(card, baseURL, cardConfigurationClient)

        verify(cardConfigurationClient).getCardConfiguration(typeSafeEq(baseURL), capture(captor))

        val error = Exception()
        captor.value.onResponse(error, null)

        captor2 = argumentCaptor()

        verify(card, never()).cardValidator = capture(captor2)

    }

    @Test
    fun givenTheFactoryReceivesNoExternalClientWillInstantiateOne() {
        CardConfigurationFactory.getRemoteCardValidatorConfiguration(card, baseURL)
    }

}
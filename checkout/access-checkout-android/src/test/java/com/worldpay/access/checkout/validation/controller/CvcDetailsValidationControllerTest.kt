package com.worldpay.access.checkout.validation.controller

import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_NO_BRAND
import com.worldpay.access.checkout.validation.decorators.CvcFieldDecorator
import org.junit.Before
import org.junit.Test

class CvcDetailsValidationControllerTest {

    private val cvcFieldDecorator = mock<CvcFieldDecorator>()

    private lateinit var callbackCaptor: KArgumentCaptor<Callback<CardConfiguration>>

    @Before
    fun setup() {
        callbackCaptor = argumentCaptor()
    }

    @Test
    fun `should add text changed listeners to each of the fields provided upon initialisation`() {
        createAccessCheckoutValidationController()

        verify(cvcFieldDecorator).decorate(CARD_CONFIG_NO_BRAND)
    }

    private fun createAccessCheckoutValidationController() {
        CvcDetailsValidationController(
            cvcFieldDecorator = cvcFieldDecorator
        )
    }

}

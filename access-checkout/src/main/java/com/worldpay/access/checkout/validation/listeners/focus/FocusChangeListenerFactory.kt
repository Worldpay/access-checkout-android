package com.worldpay.access.checkout.validation.listeners.focus

import com.worldpay.access.checkout.validation.result.handler.ResultHandlerFactory

internal class FocusChangeListenerFactory(private val resultHandlerFactory : ResultHandlerFactory) {

    fun createPanFocusChangeListener() : PanFocusChangeListener {
        return PanFocusChangeListener(resultHandlerFactory.getPanValidationResultHandler())
    }

    fun createCvcFocusChangeListener() : CvcFocusChangeListener {
        return CvcFocusChangeListener(resultHandlerFactory.getCvcValidationResultHandler())
    }

    fun createExpiryDateFocusChangeListener() : ExpiryDateFocusChangeListener {
        return ExpiryDateFocusChangeListener(resultHandlerFactory.getExpiryDateValidationResultHandler())
    }

}

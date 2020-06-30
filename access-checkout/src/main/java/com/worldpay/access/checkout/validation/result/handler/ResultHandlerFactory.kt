package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.validation.listener.*
import com.worldpay.access.checkout.validation.result.state.CvcFieldValidationStateManager
import com.worldpay.access.checkout.validation.result.state.ExpiryDateFieldValidationStateManager
import com.worldpay.access.checkout.validation.result.state.FieldValidationStateManager
import com.worldpay.access.checkout.validation.result.state.PanFieldValidationStateManager
import com.worldpay.access.checkout.validation.transformers.ToCardBrandTransformer

internal class ResultHandlerFactory(
    private val accessCheckoutValidationListener : AccessCheckoutValidationListener,
    private val fieldValidationStateManager : FieldValidationStateManager,
    private val lifecycleOwner : LifecycleOwner
) {

    private var cvcValidationResultHandler : CvcValidationResultHandler? = null
    private var panValidationResultHandler : PanValidationResultHandler? = null
    private var expiryDateValidationResultHandler : ExpiryDateValidationResultHandler? = null
    private var brandChangedHandler : BrandChangedHandler? = null

    fun getCvcValidationResultHandler() : CvcValidationResultHandler {
        if (cvcValidationResultHandler == null) {
            cvcValidationResultHandler = CvcValidationResultHandler(
                validationListener = accessCheckoutValidationListener as AccessCheckoutCvcValidationListener,
                validationStateManager = fieldValidationStateManager as CvcFieldValidationStateManager,
                lifecycleOwner = lifecycleOwner
            )
        }
        return cvcValidationResultHandler as CvcValidationResultHandler
    }

    fun getPanValidationResultHandler() : PanValidationResultHandler {
        if (panValidationResultHandler == null) {
            panValidationResultHandler = PanValidationResultHandler(
                validationListener = accessCheckoutValidationListener as AccessCheckoutPanValidationListener,
                validationStateManager = fieldValidationStateManager as PanFieldValidationStateManager,
                lifecycleOwner = lifecycleOwner
            )
        }

        return panValidationResultHandler as PanValidationResultHandler
    }

    fun getExpiryDateValidationResultHandler() : ExpiryDateValidationResultHandler {
        if (expiryDateValidationResultHandler == null) {
            expiryDateValidationResultHandler =
                ExpiryDateValidationResultHandler(
                    validationListener = accessCheckoutValidationListener as AccessCheckoutExpiryDateValidationListener,
                    validationStateManager = fieldValidationStateManager as ExpiryDateFieldValidationStateManager,
                    lifecycleOwner = lifecycleOwner
                )
        }
        return expiryDateValidationResultHandler as ExpiryDateValidationResultHandler
    }

    fun getBrandChangedHandler() : BrandChangedHandler {
        if (brandChangedHandler == null) {
            brandChangedHandler = BrandChangedHandler(
                validationListener = accessCheckoutValidationListener as AccessCheckoutBrandChangedListener,
                toCardBrandTransformer = ToCardBrandTransformer(),
                lifecycleOwner = lifecycleOwner
            )
        }
        return brandChangedHandler as BrandChangedHandler
    }

}

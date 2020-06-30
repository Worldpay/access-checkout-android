package com.worldpay.access.checkout.validation.result.handler

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutBrandChangedListener
import com.worldpay.access.checkout.validation.transformers.ToCardBrandTransformer

internal class BrandChangedHandler(
    private val validationListener: AccessCheckoutBrandChangedListener,
    private val toCardBrandTransformer: ToCardBrandTransformer = ToCardBrandTransformer(),
    lifecycleOwner : LifecycleOwner
) : LifecycleObserver {

    private var inLifecycleEvent = false

    private var deferredEvent: RemoteCardBrand? = null

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal fun onResume() {
        inLifecycleEvent = false
        if (deferredEvent != null) {
            handle(deferredEvent!!)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    internal fun onPause() {
        inLifecycleEvent = true
    }

    fun handle(remoteCardBrand : RemoteCardBrand?) {
        if (inLifecycleEvent) {
            deferredEvent = remoteCardBrand
            return
        }

        deferredEvent = null
        val cardBrand = toCardBrandTransformer.transform(remoteCardBrand)
        validationListener.onBrandChange(cardBrand)
    }

}

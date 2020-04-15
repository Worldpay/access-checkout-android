package com.worldpay.access.checkout.client.checkout

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.views.SessionResponseListener

class AccessCheckoutClientBuilder {

    private var baseUrl: String? = null
    private var merchantId: String? = null
    private var context: Context? = null
    private var sessionResponseListener: SessionResponseListener? = null
    private var lifecycleOwner: LifecycleOwner? = null

    fun baseUrl(baseURL: String): AccessCheckoutClientBuilder {
        this.baseUrl = baseURL
        return this
    }

    fun merchantId(merchantID: String): AccessCheckoutClientBuilder {
        this.merchantId = merchantID
        return this
    }

    fun context(context: Context): AccessCheckoutClientBuilder {
        this.context = context
        return this
    }

    fun sessionResponseListener(sessionResponseListener: SessionResponseListener): AccessCheckoutClientBuilder {
        this.sessionResponseListener = sessionResponseListener
        return this
    }

    fun lifecycleOwner(lifecycleOwner: LifecycleOwner): AccessCheckoutClientBuilder {
        this.lifecycleOwner = lifecycleOwner
        return this
    }

    fun build(): CheckoutClient {
        validateNotNull(baseUrl, "base url")
        validateNotNull(merchantId, "merchant ID")
        validateNotNull(context, "context")
        validateNotNull(sessionResponseListener, "session response listener")
        validateNotNull(lifecycleOwner, "lifecycle owner")

        return AccessCheckoutClient(
            baseUrl as String,
            merchantId as String,
            context as Context,
            sessionResponseListener as SessionResponseListener,
            lifecycleOwner as LifecycleOwner
        )
    }

    private fun validateNotNull(property: Any?, propertyKey: String) {
        if (property == null) {
            throw IllegalArgumentException("Expected $propertyKey to be provided but was not")
        }
    }

}
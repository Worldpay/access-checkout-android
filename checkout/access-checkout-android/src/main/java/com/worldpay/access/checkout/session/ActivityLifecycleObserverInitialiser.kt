package com.worldpay.access.checkout.session

import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.api.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.api.session.SessionReceiver

internal class ActivityLifecycleObserverInitialiser(
    tag: String,
    sessionReceiver: SessionReceiver,
    lifecycleOwner: LifecycleOwner,
    localBroadcastManagerFactory: LocalBroadcastManagerFactory
) {

    private val activityLifecycleObserver: ActivityLifecycleObserver =
        ActivityLifecycleObserver(
            tag,
            sessionReceiver,
            lifecycleOwner,
            localBroadcastManagerFactory
        )

    fun initialise(): ActivityLifecycleObserver {
        return activityLifecycleObserver
    }

}
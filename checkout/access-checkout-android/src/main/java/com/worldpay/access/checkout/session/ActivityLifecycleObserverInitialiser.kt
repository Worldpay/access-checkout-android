package com.worldpay.access.checkout.session

import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.session.broadcast.SessionBroadcastManagerFactory

internal class ActivityLifecycleObserverInitialiser(
    tag: String,
    lifecycleOwner: LifecycleOwner,
    sessionBroadcastManagerFactory: SessionBroadcastManagerFactory
) {

    private val activityLifecycleObserver: ActivityLifecycleObserver =
        ActivityLifecycleObserver(
            tag,
            lifecycleOwner,
            sessionBroadcastManagerFactory
        )

    fun initialise(): ActivityLifecycleObserver {
        return activityLifecycleObserver
    }

}
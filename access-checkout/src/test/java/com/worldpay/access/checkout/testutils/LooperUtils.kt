package com.worldpay.access.checkout.testutils

import android.os.Looper.getMainLooper
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowLooper

fun ShadowLooper.waitForQueueUntilIdle(seconds: Int = 1) {
    for (i in 1..(seconds * 10)) {
        if (shadowOf(getMainLooper()).isIdle) {
            Thread.sleep(100)
        } else {
            shadowOf(getMainLooper()).idle()
        }
    }
}

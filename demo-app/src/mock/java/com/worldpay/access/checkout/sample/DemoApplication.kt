package com.worldpay.access.checkout.sample

import android.app.Application

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MockServer.startWiremock(this)
        CardBinServiceMock.start(this)
    }

    override fun onTerminate() {
        MockServer.stopWiremock()
        CardBinServiceMock.stop()
        super.onTerminate()
    }
}

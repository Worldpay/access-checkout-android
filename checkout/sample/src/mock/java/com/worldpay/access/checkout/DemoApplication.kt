package com.worldpay.access.checkout

import android.app.Application

class DemoApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        MockServer.startWiremock(this)
    }

    override fun onTerminate() {
        MockServer.stopWiremock()
        super.onTerminate()
    }

}
package com.worldpay.access.checkout.sample

import android.app.Application
import com.worldpay.access.checkout.test.mocks.AccessWPServiceWiremock
import com.worldpay.access.checkout.test.mocks.CardBinServiceMock

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val server = AccessWPServiceWiremock.start(this, 8084)
        MockServer.defaultStubMappings(this, server)
        CardBinServiceMock.start(this, 3003)
    }

    override fun onTerminate() {
        AccessWPServiceWiremock.shutdown()
        CardBinServiceMock.shutdown()
        super.onTerminate()
    }
}

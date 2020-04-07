package com.worldpay.access.checkout.cvv.testutil

import com.worldpay.access.checkout.AbstractUITest
import com.worldpay.access.checkout.MockServer.defaultStubMappings
import org.junit.Before

abstract class AbstractCvvFlowUITest: AbstractUITest() {

    @Before
    fun setup() {
        defaultStubMappings(activityRule.activity)
    }

}
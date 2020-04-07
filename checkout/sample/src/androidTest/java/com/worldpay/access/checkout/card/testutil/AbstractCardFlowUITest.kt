package com.worldpay.access.checkout.card.testutil

import com.worldpay.access.checkout.AbstractUITest
import com.worldpay.access.checkout.MockServer.defaultStubMappings
import org.junit.Before

abstract class AbstractCardFlowUITest: AbstractUITest() {

    @Before
    fun setup() {
        defaultStubMappings(activityRule.activity)
    }

}
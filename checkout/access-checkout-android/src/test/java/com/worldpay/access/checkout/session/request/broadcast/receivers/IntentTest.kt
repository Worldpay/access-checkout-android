package com.worldpay.access.checkout.session.request.broadcast.receivers

import org.junit.Test
import kotlin.test.assertEquals

class IntentTest {

    @Test
    fun `should return expected intents`() {
        val packageName = "com.worldpay.access.checkout.intent.action"

        assertEquals("${packageName}.NUM_OF_SESSION_TYPES_REQUESTED", NUM_OF_SESSION_TYPES_REQUESTED)
        assertEquals("${packageName}.SESSION_TYPE_REQUEST_COMPLETE", SESSION_TYPE_REQUEST_COMPLETE)
        assertEquals("${packageName}.COMPLETED_SESSION_REQUEST", COMPLETED_SESSION_REQUEST)
    }

}
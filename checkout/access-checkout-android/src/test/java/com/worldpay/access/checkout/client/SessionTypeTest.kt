package com.worldpay.access.checkout.client

import com.worldpay.access.checkout.client.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
import org.junit.Test
import kotlin.test.assertEquals

class SessionTypeTest {

    @Test
    fun `should return expected value for each enum`() {
        assertEquals("payments-cvc-session", PAYMENTS_CVC_SESSION.value)
        assertEquals("verified-token-session", VERIFIED_TOKEN_SESSION.value)
    }

}
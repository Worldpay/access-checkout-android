package com.worldpay.access.checkout.api.serialization

import kotlin.test.Test
import kotlin.test.assertEquals

class PlainResponseDeserializerTest {

    @Test
    fun shouldReturnResponseAsIs() {
        val result = PlainResponseDeserializer.deserialize("some response")

        assertEquals("some response", result)
    }
}

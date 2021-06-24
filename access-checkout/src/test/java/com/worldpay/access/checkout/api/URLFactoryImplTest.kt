package com.worldpay.access.checkout.api

import java.net.URL
import org.junit.Assert.assertEquals
import org.junit.Test

class URLFactoryImplTest {

    @Test
    fun getUrlTest() {
        val urlString = "https://www.google.com"
        val expectedUrl = URL(urlString)
        val factory = URLFactoryImpl()

        val actual = factory.getURL(urlString)

        assertEquals(expectedUrl, actual)
    }
}

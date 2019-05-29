package com.worldpay.access.checkout.api

import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.URL

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
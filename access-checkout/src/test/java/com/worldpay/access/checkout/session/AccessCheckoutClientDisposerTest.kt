package com.worldpay.access.checkout.session

import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class AccessCheckoutClientDisposerTest {
    @Test
    fun `should call AccessCheckoutClient dispose() method when dispose is called()`() {
        val accessCheckoutClient = mock(AccessCheckoutClientImpl::class.java)
        val accessCheckoutClientDisposer = AccessCheckoutClientDisposer()

        accessCheckoutClientDisposer.dispose(accessCheckoutClient)

        verify(accessCheckoutClient).dispose()
    }
}

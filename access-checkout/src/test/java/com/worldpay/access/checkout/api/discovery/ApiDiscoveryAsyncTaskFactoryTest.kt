package com.worldpay.access.checkout.api.discovery

import com.nhaarman.mockitokotlin2.mock
import kotlin.test.assertNotNull
import org.junit.Test

class ApiDiscoveryAsyncTaskFactoryTest {

    @Test
    fun `should return api discovery async task when getAsyncTask function is called`() {
        val task = ApiDiscoveryAsyncTaskFactory().getAsyncTask(mock(), DiscoverLinks.verifiedTokens)
        assertNotNull(task)
    }
}

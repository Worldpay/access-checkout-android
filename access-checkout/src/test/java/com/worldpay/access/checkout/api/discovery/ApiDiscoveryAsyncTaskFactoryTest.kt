package com.worldpay.access.checkout.api.discovery

import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import kotlin.test.assertNotNull

class ApiDiscoveryAsyncTaskFactoryTest {

    @Test
    fun `should return api discovery async task when getAsyncTask function is called`() {
        val task = ApiDiscoveryAsyncTaskFactory().getAsyncTask(mock(), DiscoverLinks.verifiedTokens)
        assertNotNull(task)
    }

}
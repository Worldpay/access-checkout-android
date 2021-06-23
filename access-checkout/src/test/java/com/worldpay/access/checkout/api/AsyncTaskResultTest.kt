package com.worldpay.access.checkout.api

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AsyncTaskResultTest {

    @Test
    fun givenAResult_ThenShouldReturnResultAndNotError() {

        val asyncTaskResult = AsyncTaskResult("abc")

        assertEquals(asyncTaskResult.result, "abc")
        assertNull(asyncTaskResult.error)
    }

    @Test
    fun givenAnError_ThenShouldReturnErrorAndNotResult() {

        val asyncTaskResult = AsyncTaskResult<String>(Exception("Some exception"))

        assertNull(asyncTaskResult.result)
        assertTrue(asyncTaskResult.error is Exception)
        assertEquals(asyncTaskResult.error!!.message, "Some exception")
    }
}

package com.worldpay.access.checkout.api

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AsyncTaskUtilsTest {

    @Test
    fun shouldCallbackWithSuccessfulResponseFromAsyncTask() {
        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertEquals("response_string", response)
            }
        }

        AsyncTaskUtils.callbackOnTaskResult(callback, AsyncTaskResult("response_string"))
    }

    @Test
    fun shouldCallbackWithErrorResponseFromAsyncTask() {
        val callback = object : Callback<String> {
            override fun onResponse(error: Exception?, response: String?) {
                assertTrue(error is IllegalStateException)
                assertEquals("error_message", error?.message)
            }
        }

        AsyncTaskUtils.callbackOnTaskResult(callback, AsyncTaskResult(IllegalStateException("error_message")))
    }

    @Test
    fun canBeCalledWithoutCallbackFromAsyncTask() {
        AsyncTaskUtils.callbackOnTaskResult(null, AsyncTaskResult<String>(IllegalStateException("error_message")))
    }
}

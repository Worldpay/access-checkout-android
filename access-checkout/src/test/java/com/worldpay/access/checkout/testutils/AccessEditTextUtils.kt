package com.worldpay.access.checkout.testutils

import com.worldpay.access.checkout.ui.AccessEditText
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

fun createAccessEditTextMock(returnValue: String): AccessEditText {
    val accessEditTextMock = mock<AccessEditText>()
    whenever(accessEditTextMock.text).thenReturn(returnValue)

    return accessEditTextMock
}


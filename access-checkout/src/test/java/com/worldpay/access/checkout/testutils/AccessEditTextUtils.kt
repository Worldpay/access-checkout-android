package com.worldpay.access.checkout.testutils

import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

fun createAccessCheckoutEditTextMock(returnValue: String): AccessCheckoutEditText {
    val accessCheckoutEditTextMock = mock<AccessCheckoutEditText>()
    whenever(accessCheckoutEditTextMock.text).thenReturn(returnValue)

    return accessCheckoutEditTextMock
}

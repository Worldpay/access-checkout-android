package com.worldpay.access.checkout.ui

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.EditText
import com.worldpay.access.checkout.R
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import kotlin.test.assertEquals

class AttributeValuesTest {
    private lateinit var attributeValues: AttributeValues
    private val contextMock: Context = mock()
    private val attributeSetMock: AttributeSet = mock()
    private val editTextMock: EditText = mock()

    @Before
    fun setUp() {
        attributeValues = AttributeValues(contextMock, attributeSetMock, editTextMock)
    }

    @Test
    fun `should return a typedArray when called`() {
        val typedArrayMock: TypedArray = mock()

        given(
            contextMock.obtainStyledAttributes(
                attributeSetMock,
                R.styleable.AccessCheckoutEditText,
                0,
                0
            )
        ).willReturn(typedArrayMock)

        assertEquals(typedArrayMock, attributeValues.setAttributes())
    }
}

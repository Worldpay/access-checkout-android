package com.worldpay.access.checkout.ui

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.util.AttributeSet
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.given
import org.mockito.kotlin.mock

class AttributeValuesTest {
    private lateinit var attributeValues: AttributeValues
    private val contextMock: Context = mock()
    private val attributeSetMock: AttributeSet = mock()
    private val resourcesMock: Resources = mock()

    @Before
    fun setUp() {
        attributeValues = AttributeValues(contextMock, attributeSetMock)
    }

    @Test
    fun `should return string from attribute name`() {
        val resId = 123
        val attributeName = "some-name"
        val expectedString = "some-hint"

        given(attributeSetMock.getAttributeResourceValue(any(), eq(attributeName), any())).willReturn(resId)
        given(resourcesMock.getString(resId)).willReturn(expectedString)
        given(contextMock.resources).willReturn(resourcesMock)

        assertEquals(expectedString, attributeValues.stringOf(attributeName))
    }

    @Test
    fun `should return null when unable to find the resource`() {
        val resId = 123
        val attributeName = "some-name"

        given(attributeSetMock.getAttributeResourceValue(any(), eq(attributeName), any())).willReturn(resId)
        given(resourcesMock.getString(resId)).willThrow(NotFoundException())
        given(contextMock.resources).willReturn(resourcesMock)

        assertNull(attributeValues.stringOf(attributeName))
    }

    @Test
    fun `should return string from attribute name when resource id is 0`() {
        val attributeName = "some-name"
        val expectedString = "some-hint"

        given(attributeSetMock.getAttributeResourceValue(any(), eq(attributeName), any())).willReturn(0)
        given(attributeSetMock.getAttributeValue(any(), eq(attributeName))).willReturn(expectedString)
        given(contextMock.resources).willReturn(resourcesMock)

        assertEquals(expectedString, attributeValues.stringOf(attributeName))
    }
}

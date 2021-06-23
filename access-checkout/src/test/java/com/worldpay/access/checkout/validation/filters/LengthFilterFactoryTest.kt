package com.worldpay.access.checkout.validation.filters

import android.widget.EditText
import com.nhaarman.mockitokotlin2.mock
import kotlin.test.assertNotNull
import org.junit.Before
import org.junit.Test

class LengthFilterFactoryTest {

    private val panEditText = mock<EditText>()

    private lateinit var lengthFilterFactory: LengthFilterFactory

    @Before
    fun setup() {
        lengthFilterFactory = LengthFilterFactory()
    }

    @Test
    fun `should get pan length filter`() {
        val filter: PanLengthFilter = lengthFilterFactory.getPanLengthFilter(false)
        assertNotNull(filter)
    }

    @Test
    fun `should get expiry date length filter`() {
        val filter: ExpiryDateLengthFilter = lengthFilterFactory.getExpiryDateLengthFilter()
        assertNotNull(filter)
    }

    @Test
    fun `should get cvc length filter`() {
        val filter: CvcLengthFilter = lengthFilterFactory.getCvcLengthFilter(panEditText)
        assertNotNull(filter)
    }
}

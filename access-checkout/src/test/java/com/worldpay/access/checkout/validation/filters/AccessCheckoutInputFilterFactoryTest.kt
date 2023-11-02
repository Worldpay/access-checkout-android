package com.worldpay.access.checkout.validation.filters

import android.widget.EditText
import kotlin.test.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class AccessCheckoutInputFilterFactoryTest {

    private val panEditText = mock<EditText>()

    private lateinit var accessCheckoutInputFilterFactory: AccessCheckoutInputFilterFactory

    @Before
    fun setup() {
        accessCheckoutInputFilterFactory = AccessCheckoutInputFilterFactory()
    }

    @Test
    fun `should get pan length filter`() {
        val filter: PanNumericFilter = accessCheckoutInputFilterFactory.getPanNumericFilter()
        assertNotNull(filter)
    }

    @Test
    fun `should get expiry date length filter`() {
        val filter: ExpiryDateLengthFilter =
            accessCheckoutInputFilterFactory.getExpiryDateLengthFilter()
        assertNotNull(filter)
    }

    @Test
    fun `should get cvc length filter`() {
        val filter: CvcLengthFilter =
            accessCheckoutInputFilterFactory.getCvcLengthFilter(panEditText)
        assertNotNull(filter)
    }
}

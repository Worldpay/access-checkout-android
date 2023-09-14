package com.worldpay.access.checkout.validation.filters

import com.worldpay.access.checkout.testutils.CardConfigurationUtil.mockSuccessfulCardConfiguration
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import com.worldpay.access.checkout.ui.AccessEditText
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest as runAsBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class CvcLengthFilterTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val cvc = AccessEditText(context)
    private val pan = AccessEditText(context)

    @Before
    fun setup() = runAsBlockingTest {
        mockSuccessfulCardConfiguration()
        cvc.filters = arrayOf(CvcLengthFilter(pan))
    }

    @Test
    fun `should limit to max length`() {
        cvc.setText("123456")
        assertEquals("1234", cvc.text)
    }

    @Test
    fun `should allow text within limit`() {
        cvc.setText("1")
        assertEquals("1", cvc.text)

        cvc.setText("12")
        assertEquals("12", cvc.text)

        cvc.setText("123")
        assertEquals("123", cvc.text)

        cvc.setText("1234")
        assertEquals("1234", cvc.text)
    }

    @Test
    fun `should change the max length depending on the pan detected`() {
        pan.setText("")

        cvc.setText("123456")
        assertEquals("1234", cvc.text)

        pan.setText(visaPan())

        cvc.setText("123456")
        assertEquals("123", cvc.text)
    }

    @Test
    fun `should ignore pan if no pan edit text is passed to filter`() {
        cvc.filters = arrayOf(CvcLengthFilter(null))

        pan.setText("")

        cvc.setText("123456")
        assertEquals("1234", cvc.text)

        pan.setText(visaPan())

        cvc.setText("123456")
        assertEquals("1234", cvc.text)
    }
}

package com.worldpay.access.checkout.validation.filters

import android.widget.EditText
import com.worldpay.access.checkout.BaseCoroutineTest
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.mockSuccessfulCardConfiguration
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class CvcLengthFilterTest: BaseCoroutineTest() {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val cvc = EditText(context)
    private val pan = EditText(context)

    @Before
    fun setup() = runTest {
        mockSuccessfulCardConfiguration()
        advanceUntilIdle()
        cvc.filters = arrayOf(CvcLengthFilter(pan))
    }

    @Test
    fun `should limit to max length`() = runTest {
        cvc.setText("123456")
        assertEquals("1234", cvc.text.toString())
    }

    @Test
    fun `should allow text within limit`() = runTest {
        cvc.setText("1")
        assertEquals("1", cvc.text.toString())

        cvc.setText("12")
        assertEquals("12", cvc.text.toString())

        cvc.setText("123")
        assertEquals("123", cvc.text.toString())

        cvc.setText("1234")
        assertEquals("1234", cvc.text.toString())
    }

    @Test
    fun `should change the max length depending on the pan detected`() = runTest {
        pan.setText("")

        cvc.setText("123456")
        assertEquals("1234", cvc.text.toString())

        pan.setText(visaPan())

        cvc.setText("123456")
        assertEquals("123", cvc.text.toString())
    }

    @Test
    fun `should ignore pan if no pan edit text is passed to filter`() = runTest {
        cvc.filters = arrayOf(CvcLengthFilter(null))

        pan.setText("")

        cvc.setText("123456")
        assertEquals("1234", cvc.text.toString())

        pan.setText(visaPan())

        cvc.setText("123456")
        assertEquals("1234", cvc.text.toString())
    }
}

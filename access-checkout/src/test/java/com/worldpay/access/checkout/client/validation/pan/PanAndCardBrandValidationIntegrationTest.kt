package com.worldpay.access.checkout.client.validation.pan

import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.AMEX_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.DINERS_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.DISCOVER_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.JCB_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.MAESTRO_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.MASTERCARD_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.toCardBrandList
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.DINERS_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.DISCOVER_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.INVALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.JCB_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.MAESTRO_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.MASTERCARD_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.PARTIAL_VISA
import com.worldpay.access.checkout.testutils.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.visaPan
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PanAndCardBrandValidationIntegrationTest : AbstractValidationIntegrationTest() {

    @Test
    fun `should accept an unknown valid luhn pan when no accepted cards have been specified`() =
        runTest {
            initialiseValidation()
            setText(pan, VALID_UNKNOWN_LUHN)

            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener, never()).onBrandsChange(any())
        }

    @Test
    fun `should accept an unknown valid luhn pan when all cards are accepted`() = runTest {
        initialiseValidation(
            acceptedCardBrands = arrayOf(
                "AMEX",
                "DINERS",
                "DISCOVER",
                "JCB",
                "MAESTRO",
                "MASTERCARD",
                "VISA"
            )
        )

        setText(pan, VALID_UNKNOWN_LUHN)

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener, never()).onBrandsChange(any())
    }

    @Test
    fun `should not call listener at all when pan is complete but invalid and unrecognised`() =
        runTest {
            initialiseValidation()
            setText(pan, INVALID_UNKNOWN_LUHN)
            verifyNoInteractions(cardValidationListener)
        }

    @Test
    fun `should not call listener at all when pan is partial but invalid and unrecognised`() =
        runTest {
            initialiseValidation()
            setText(pan, "000")
            verifyNoInteractions(cardValidationListener)
        }

    @Test
    fun `should not validate pan for partial visa pan but should call brand changed with visa brand`() =
        runTest {
            initialiseValidation()
            setText(pan, PARTIAL_VISA)

            verify(cardValidationListener, never()).onPanValidated(any())
            verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))
        }

    @Test
    fun `should only notify pan validated on validation state change and notify brand change each time the brand changes - without accepted card brands`() =
        runTest {
            initialiseValidation()
            setText(pan, visaPan())

            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))

            reset(cardValidationListener)

            setText(pan, MASTERCARD_PAN)


            verify(cardValidationListener, never()).onPanValidated(any())
            verify(cardValidationListener).onBrandsChange(toCardBrandList(MASTERCARD_BRAND))

            reset(cardValidationListener)

            setText(pan, AMEX_PAN)

            verify(cardValidationListener, never()).onPanValidated(any())
            verify(cardValidationListener).onBrandsChange(toCardBrandList(AMEX_BRAND))

            reset(cardValidationListener)

            setText(pan, JCB_PAN)


            verify(cardValidationListener, never()).onPanValidated(any())
            verify(cardValidationListener).onBrandsChange(toCardBrandList(JCB_BRAND))

            reset(cardValidationListener)

            setText(pan, DISCOVER_PAN)


            verify(cardValidationListener, never()).onPanValidated(any())
            verify(cardValidationListener).onBrandsChange(toCardBrandList(DISCOVER_BRAND))

            reset(cardValidationListener)

            setText(pan, DINERS_PAN)


            verify(cardValidationListener, never()).onPanValidated(any())
            verify(cardValidationListener).onBrandsChange(toCardBrandList(DINERS_BRAND))

            reset(cardValidationListener)

            setText(pan, MAESTRO_PAN)

            verify(cardValidationListener, never()).onPanValidated(any())
            verify(cardValidationListener).onBrandsChange(toCardBrandList(MAESTRO_BRAND))

            reset(cardValidationListener)

            setText(pan, "")

            verify(cardValidationListener).onPanValidated(false)
            verify(cardValidationListener).onBrandsChange(emptyList())
        }

    @Test
    fun `should only notify pan validated on validation state change and notify brand change each time the brand changes - with empty array of accepted card brands`() =
        runTest {
            initialiseValidation(acceptedCardBrands = emptyArray())

            setText(pan, visaPan())


            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))

            reset(cardValidationListener)

            setText(pan, MASTERCARD_PAN)


            verify(cardValidationListener, never()).onPanValidated(any())
            verify(cardValidationListener).onBrandsChange(toCardBrandList(MASTERCARD_BRAND))

            reset(cardValidationListener)

            setText(pan, AMEX_PAN)


            verify(cardValidationListener, never()).onPanValidated(any())
            verify(cardValidationListener).onBrandsChange(toCardBrandList(AMEX_BRAND))

            reset(cardValidationListener)

            setText(pan, JCB_PAN)


            verify(cardValidationListener, never()).onPanValidated(any())
            verify(cardValidationListener).onBrandsChange(toCardBrandList(JCB_BRAND))

            reset(cardValidationListener)

            setText(pan, DISCOVER_PAN)


            verify(cardValidationListener, never()).onPanValidated(any())
            verify(cardValidationListener).onBrandsChange(toCardBrandList(DISCOVER_BRAND))

            reset(cardValidationListener)

            setText(pan, DINERS_PAN)


            verify(cardValidationListener, never()).onPanValidated(any())
            verify(cardValidationListener).onBrandsChange(toCardBrandList(DINERS_BRAND))

            reset(cardValidationListener)

            setText(pan, MAESTRO_PAN)


            verify(cardValidationListener, never()).onPanValidated(any())
            verify(cardValidationListener).onBrandsChange(toCardBrandList(MAESTRO_BRAND))

            reset(cardValidationListener)

            setText(pan, "")


            verify(cardValidationListener).onPanValidated(false)
            verify(cardValidationListener).onBrandsChange(emptyList())
        }

    @Test
    fun `should invalidate the cvc after the pan has been validated with a brand and the cvc is now incorrect`() =
        runTest {
            initialiseValidation()
            setText(cvc, "1234")
            verify(cardValidationListener).onCvcValidated(true)

            setText(pan, visaPan())

            verify(cardValidationListener).onPanValidated(true)
            verify(cardValidationListener).onBrandsChange(toCardBrandList(VISA_BRAND))
            verify(cardValidationListener).onCvcValidated(false)
        }

    @Test
    fun `should accept amex card when amex is the only accepted card brand`() = runTest {
        initialiseValidation(acceptedCardBrands = arrayOf("AMEX"))

        setText(pan, AMEX_PAN)

        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandsChange(toCardBrandList(AMEX_BRAND))
    }

    @Test
    fun `should not accept diners card when diners is not accepted`() = runTest {

        initialiseValidation(acceptedCardBrands = arrayOf("AMEX"))

        setText(pan, DINERS_PAN)


        verify(cardValidationListener).onPanValidated(false)
        verify(cardValidationListener).onBrandsChange(toCardBrandList(DINERS_BRAND))
    }
}

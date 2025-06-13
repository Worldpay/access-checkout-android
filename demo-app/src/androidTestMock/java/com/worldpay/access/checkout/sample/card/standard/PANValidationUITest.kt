import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.github.tomakehurst.wiremock.WireMockServer
import com.worldpay.access.checkout.sample.card.CardNumberUtil.INVALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.asPartial
import com.worldpay.access.checkout.sample.card.standard.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.AMEX
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.VISA
import com.worldpay.access.checkout.sample.card.standard.testutil.CardFragmentTestUtils.Input.CVC
import com.worldpay.access.checkout.sample.card.standard.testutil.CardFragmentTestUtils.Input.EXPIRY_DATE
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PANValidationUITest : AbstractCardFragmentTest() {

    private lateinit var cardBinServer: WireMockServer

    @Test
    fun shouldValidateValidGlobalBrandAsGreenTextWithGlobalBrandIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = VISA_PAN)
            .validationStateIs(pan = true)
            .hasBrand(VISA)
    }

    @Test
    fun shouldValidateInvalidPanAsRedTextWithGlobalBrandIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "4024001728904375123")
            .hasBrand(VISA)
            .focusOn(CVC)
            .validationStateIs(pan = false)
    }

    @Test
    fun shouldValidateValidUnknownLuhnAsGreenTextWithNoBrandIcon() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = VALID_UNKNOWN_LUHN)
            .validationStateIs(pan = true)
            .hasNoBrand()
    }

    @Test
    fun shouldLimitToMaxLengthWhenPastingLongString() {
        val pastedText = "123456789012345678901234567890"
        val pastedTextWith19DigitsAndSpaces = "1234 5678 9012 3456 789"

        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(pan = pastedText)
            .cardDetailsAre(pan = pastedTextWith19DigitsAndSpaces)
            .cursorPositionIs(23)
    }

    @Test
    fun shouldValidatePanWhenFocusIsLostAndDisplayGlobalBrandImage() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "343434343434341")
            .hasBrand(AMEX)
            .focusOn(CVC)
            .validationStateIs(pan = false)
    }

    @Test
    fun shouldDisplayGlobalBrandNameWhenPanIsValidated() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "4444333322221111")
            .hasBrand(VISA)
            .hasBrandName("visa")
    }

    @Test
    fun shouldDisplayOneGlobalBrandNameWhenOneDigitFromValidPanIsRemoved() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "444433332222")
            .hasBrand(VISA)
            .hasBrandName("visa")
            .setCursorPositionOnPan(11)
            .removeLastPanDigit()
            .hasBrandName("visa")
    }

    @Test
    fun shouldShowNoGlobalBrandsWhenValidPanIsCleared() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = "4111111111111111")
            .hasBrand(VISA)
            .hasBrandName("visa")
        clearPan()
        cardFragmentTestUtils
            .hasNoBrand()
            .hasBrandName("")
    }

    @Test
    fun shouldValidatePanWhenFocusIsLostAndDisplayGlobalBrandImage_unknownInvalidLuhn() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = INVALID_UNKNOWN_LUHN)
            .hasNoBrand()
            .focusOn(CVC)
            .validationStateIs(pan = false)
    }

    @Test
    fun shouldValidatePanAsFalseWhenPartialGlobalBrandEnteredAndFocusIsLost() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails(pan = asPartial(VISA_PAN))
            .hasBrand(VISA)
            .focusOn(EXPIRY_DATE)
            .validationStateIs(pan = false)
            .hasBrand(VISA)
    }

    @Test
    fun shouldValidateValidCobrandedPanWhenEntered() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails("4150580996517927")
            .hasBrandName("cartesBancaires, visa")
            .validationStateIs(pan = true)
    }

    @Test
    fun shouldDisplayOneGlobalBrandNameWhenOneDigitFromValidCoBrandedPanIsRemoved() {
        cardFragmentTestUtils
            .isInInitialState()
            .hasNoBrand()
            .enterCardDetails("415058099651")
            .hasBrandName("cartesBancaires, visa")
            .removeLastPanDigit()
            .hasBrandName("visa")
    }



}

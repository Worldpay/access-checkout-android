package com.worldpay.access.checkout.sample.card.restricted

import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.MockServer.defaultStubMappings
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.DINERS_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.DISCOVER_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.JCB_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.MAESTRO_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.MASTERCARD_PAN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.PARTIAL_MAESTRO
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.sample.card.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.sample.card.restricted.testutil.RestrictedCardFragmentTestUtils
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand.*
import com.worldpay.access.checkout.sample.testutil.UITestUtils.navigateTo
import com.worldpay.access.checkout.sample.testutil.UITestUtils.rotatePortrait
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RestrictedCardFragmentTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    private lateinit var restrictedCardFragmentTestUtils: RestrictedCardFragmentTestUtils

    @Before
    fun setup() {
        restrictedCardFragmentTestUtils = RestrictedCardFragmentTestUtils(activityRule)
        defaultStubMappings(activityRule.activity)
        navigateTo(R.id.nav_restricted_card_flow)
        closeSoftKeyboard()
        rotatePortrait(activityRule)
    }

    @Test
    fun shouldAcceptUnknownValidPan() {
        restrictedCardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(VALID_UNKNOWN_LUHN)
            .hasNoBrand()
            .validationStateIs(true)
    }

    @Test
    fun shouldAcceptVisaPan() {
        restrictedCardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(VISA_PAN)
            .hasBrand(VISA)
            .validationStateIs(true)
    }

    @Test
    fun shouldAcceptMastercardPan() {
        restrictedCardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(MASTERCARD_PAN)
            .hasBrand(MASTERCARD)
            .validationStateIs(true)
    }

    @Test
    fun shouldAcceptAmexPan() {
        restrictedCardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(AMEX_PAN)
            .hasBrand(AMEX)
            .validationStateIs(true)
    }

    @Test
    fun shouldNotAcceptDinersPan() {
        restrictedCardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(DINERS_PAN)
            .hasBrand(DINERS)
            .validationStateIs(false)
    }

    @Test
    fun shouldNotAcceptDiscoverPan() {
        restrictedCardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(DISCOVER_PAN)
            .hasBrand(DISCOVER)
            .validationStateIs(false)
    }

    @Test
    fun shouldNotAcceptJcbPan() {
        restrictedCardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(JCB_PAN)
            .hasBrand(JCB)
            .validationStateIs(false)
    }

    @Test
    fun shouldNotAcceptMaestroPan() {
        restrictedCardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(MAESTRO_PAN)
            .hasBrand(MAESTRO)
            .validationStateIs(false)
    }

    @Test
    fun shouldNotAcceptPartialMaestroPan() {
        restrictedCardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(PARTIAL_MAESTRO)
            .hasBrand(MAESTRO)
            .validationStateIs(false)
    }

}

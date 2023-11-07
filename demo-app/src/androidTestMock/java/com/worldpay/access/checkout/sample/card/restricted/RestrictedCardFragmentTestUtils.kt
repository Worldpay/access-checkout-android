package com.worldpay.access.checkout.sample.card.restricted

import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand
import com.worldpay.access.checkout.sample.testutil.AbstractFragmentTestUtils
import com.worldpay.access.checkout.sample.testutil.UITestUtils.retrieveEnteredText
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RestrictedCardFragmentTestUtils(activityRule: ActivityTestRule<MainActivity>) :
    AbstractFragmentTestUtils(activityRule) {

    private fun panInput() = findById<AccessCheckoutEditText>(R.id.restricted_card_flow_text_pan)
    private fun brandLogo() = findById<ImageView>(R.id.restricted_card_flow_brand_logo)

    fun isInInitialState(): RestrictedCardFragmentTestUtils {
        progressBarNotVisible()
        enabledStateIs(pan = true)
        cardDetailsAre(pan = "")
        hasNoBrand()
        return this
    }

    fun enabledStateIs(pan: Boolean? = null): RestrictedCardFragmentTestUtils {
        val visibleMsg = "visibility state"
        val enableMsg = "enabled state"

        if (pan != null) {
            wait { assertTrue("PAN Input - $visibleMsg") { panInput().isVisible } }
            wait { assertEquals(pan, panInput().isEnabled, "PAN Input - $enableMsg") }
        }

        return this
    }

    fun enterCardDetails(pan: String? = null): RestrictedCardFragmentTestUtils {
        if (pan != null) enterText(panInput(), pan)
        return this
    }

    fun cardDetailsAre(pan: String? = null): RestrictedCardFragmentTestUtils {
        if (pan != null) wait { assertEquals(pan, retrieveEnteredText(panInput())) }
        return this
    }

    fun validationStateIs(pan: Boolean? = null): RestrictedCardFragmentTestUtils {
        if (pan != null) checkValidationState(panInput(), pan, "pan")
        return this
    }

    fun hasNoBrand(): RestrictedCardFragmentTestUtils {
        val resourceEntryName = activity().resources.getResourceEntryName(R.drawable.card_unknown_logo)
        wait { assertEquals(resourceEntryName, brandLogo().getTag(R.integer.card_tag)) }
        return this
    }

    fun hasBrand(cardBrand: CardBrand): RestrictedCardFragmentTestUtils {
        wait { assertEquals(cardBrand.cardBrandName, brandLogo().getTag(R.integer.card_tag)) }
        return this
    }

    fun setSelection(start: Int, end: Int): RestrictedCardFragmentTestUtils {
        setCursorPosition(panInput(), start, end)
        return this
    }
}

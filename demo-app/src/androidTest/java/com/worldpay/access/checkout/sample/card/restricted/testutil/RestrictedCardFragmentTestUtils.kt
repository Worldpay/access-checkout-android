package com.worldpay.access.checkout.sample.card.restricted.testutil

import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.worldpay.access.checkout.sample.MainActivity
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.card.standard.testutil.CardBrand
import com.worldpay.access.checkout.sample.testutil.AbstractFragmentTestUtils
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RestrictedCardFragmentTestUtils(activityRule: ActivityTestRule<MainActivity>) : AbstractFragmentTestUtils(activityRule) {

    private fun panInput() = findById<EditText>(R.id.restricted_card_flow_text_pan)
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
        if (pan != null) wait { assertEquals(pan, panInput().text.toString()) }
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

    fun cursorPositionIs(position: Int): RestrictedCardFragmentTestUtils {
        wait { assertEquals(position, panInput().selectionEnd) }
        return this
    }

    fun selectionIs(start: Int, end: Int): RestrictedCardFragmentTestUtils {
        wait { assertEquals(start, panInput().selectionStart) }
        wait { assertEquals(end, panInput().selectionEnd) }
        return this
    }

    fun copy(): RestrictedCardFragmentTestUtils {
        copy(panInput())
        return this
    }

    fun paste(): RestrictedCardFragmentTestUtils {
        paste(panInput())
        return this
    }

    fun assertCopiedTextIs(text: String): RestrictedCardFragmentTestUtils {
        val clipboard = getInstrumentation().context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val actual = clipboard.primaryClip?.getItemAt(0)?.text.toString()
        assertEquals(text, actual)
        return this
    }
}

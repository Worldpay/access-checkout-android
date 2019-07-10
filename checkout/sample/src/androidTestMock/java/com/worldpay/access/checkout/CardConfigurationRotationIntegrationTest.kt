package com.worldpay.access.checkout

import android.support.test.rule.ActivityTestRule
import android.widget.EditText
import android.widget.ImageView
import com.worldpay.access.checkout.MockServer.stubCardConfiguration
import com.worldpay.access.checkout.UITestUtils.assertUiObjectExistsAndIsDisabled
import com.worldpay.access.checkout.UITestUtils.assertUiObjectExistsAndIsEnabled
import com.worldpay.access.checkout.UITestUtils.closeKeyboard
import com.worldpay.access.checkout.UITestUtils.getSuccessColor
import com.worldpay.access.checkout.UITestUtils.rotateToLandscapeAndWait
import com.worldpay.access.checkout.UITestUtils.rotateToPortraitAndWait
import com.worldpay.access.checkout.UITestUtils.uiObjectWithId
import com.worldpay.access.checkout.UITestUtils.updateCVVDetails
import com.worldpay.access.checkout.UITestUtils.updateMonthDetails
import com.worldpay.access.checkout.UITestUtils.updatePANDetails
import com.worldpay.access.checkout.matchers.BrandVectorImageMatcher
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardValidationRule
import com.worldpay.access.checkout.views.resIdByName
import org.junit.After
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CardConfigurationRotationIntegrationTest {

    private val timeoutInMillis = 3000L

    private val luhnValidMastercardCard = "5555555555554444"
    private val luhnInvalidMastercardCard = "55555555555111"
    private val unknownCvv = "12"
    private val month = "12"
    private val year = "99"

    private val mastercardCvvValidationRule: CardValidationRule = CardValidationRule("^\\d{0,3}$", null, null, 3)
    private val mastercardPANValidationRule: CardValidationRule = CardValidationRule("^5\\d{0,15}$", null, null, 16)
    private val brands = listOf(
        CardBrand(
            "mastercard",
            "card_mastercard_logo",
            mastercardCvvValidationRule,
            listOf(mastercardPANValidationRule)
        )
    )

    @get:Rule
    var cardConfigurationRule = ActivityTestRule(MainActivity::class.java)

    fun activity(): MainActivity = cardConfigurationRule.activity

    @After
    fun tearDown() {
        stubCardConfiguration(activity())
    }

    @Test
    fun givenCardConfigurationCallIsDelayed_AndValidKnownCardDataIsInsertedAndUserPressesSubmit_ThenSuccessfulResponseIsReceived() {

        val cardText = uiObjectWithId(R.id.card_number_edit_text)
        val cvvText = uiObjectWithId(R.id.cardCVVText)
        val monthText = uiObjectWithId(R.id.month_edit_text)
        val yearText = uiObjectWithId(R.id.year_edit_text)

        fun cardEditText(): EditText = activity().findViewById(R.id.card_number_edit_text)
        fun cvvEditText(): EditText = activity().findViewById(R.id.cardCVVText)
        fun monthEditText(): EditText = activity().findViewById(R.id.month_edit_text)
        fun yearEditText(): EditText = activity().findViewById(R.id.year_edit_text)

        assertExpectedLogo("card_unknown_logo")

        cardText.text = luhnInvalidMastercardCard
        cvvText.click()
        cvvText.text = unknownCvv
        monthText.click()
        monthText.text = "13"
        yearText.click()
        yearText.text = year

        val failColor = activity().getColor(R.color.FAIL)
        val successColor = activity().getColor(R.color.SUCCESS)

        assertViewState(failColor, cardEditText(), monthEditText(), yearEditText(), cvvEditText())
        assertUiObjectExistsAndIsDisabled(R.id.submit)

        //rotate and assert state is the same
        rotateToLandscapeAndWait(activity(), timeoutInMillis)

        assertViewState(failColor, cardEditText(), monthEditText(), yearEditText(), cvvEditText())
        assertUiObjectExistsAndIsDisabled(R.id.submit)

        // verify that the card is now mastercard
        assertExpectedLogo("card_mastercard_logo")

        // Re-enter a luhn valid, mastercard identified card and valid date
        updatePANDetails(luhnValidMastercardCard)
        updateMonthDetails(month)
        updateCVVDetails("123")
        assertExpectedLogo("card_mastercard_logo")


        assertViewState(successColor, cardEditText(), monthEditText(), yearEditText(), cvvEditText())

        rotateToPortraitAndWait(activity(), timeoutInMillis)

        assertExpectedLogo("card_mastercard_logo")

        // Verify that all the fields are now in a success state and can be submitted
        closeKeyboard()
        assertViewState(getSuccessColor(activity()), cardEditText(), monthEditText(), yearEditText(), cvvEditText())
        assertUiObjectExistsAndIsEnabled(R.id.submit)

        cvvText.text = "12"
        assertUiObjectExistsAndIsDisabled(R.id.submit)

        //rotate and assert state is the same
        rotateToLandscapeAndWait(activity(), timeoutInMillis)

        assertUiObjectExistsAndIsDisabled(R.id.submit)
    }

    private fun assertViewState(
        color: Int,
        cardEditText: EditText,
        monthEditText: EditText,
        yearEditText: EditText,
        cvvEditText: EditText
    ) {
        assertEquals(color, cardEditText.currentTextColor)
        assertEquals(color, monthEditText.currentTextColor)
        assertEquals(color, yearEditText.currentTextColor)
        assertEquals(color, cvvEditText.currentTextColor)
    }

    private fun assertExpectedLogo(logoResName: String) {
        val logoView = activity().findViewById<ImageView>(R.id.logo_view)
        val expectedLogoResourceId = activity().resIdByName(logoResName, "drawable")
        assertTrue { BrandVectorImageMatcher.equalsBrandVectorImage(logoView, expectedLogoResourceId) }
    }
}
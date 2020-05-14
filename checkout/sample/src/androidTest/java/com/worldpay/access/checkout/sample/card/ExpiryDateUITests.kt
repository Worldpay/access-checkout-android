package com.worldpay.access.checkout.sample.card

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.worldpay.access.checkout.sample.card.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.testutil.CardFragmentTestUtils.Input.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@LargeTest
@RunWith(AndroidJUnit4::class)
class ExpiryDateUITests: AbstractCardFragmentTest() {

    @Test
    fun shouldAcceptAllMonthValues() {
        cardFragmentTestUtils.isInInitialState()

        val months: Array<String> = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")

        for (month in months) {
            cardFragmentTestUtils
                .enterCardDetails(month = month)
                .focusOn(YEAR)
                .validationStateIs(month = true, year = true)
        }
    }

    @Test
    fun shouldNotAcceptInvalidMonthValue() {
        cardFragmentTestUtils.isInInitialState()

        val months: Array<String> = arrayOf("00", "13")

        for (month in months) {
            cardFragmentTestUtils
                .enterCardDetails(month = month)
                .focusOn(YEAR)
                .validationStateIs(month = false, year = false)
        }
    }

    @Test
    fun shouldNotAcceptIncompleteMonth() {
        // validation occurs when moved away from expiry month/year
        cardFragmentTestUtils.isInInitialState()
            .enterCardDetails(month = "1")
            .validationStateIs(month = true, year = true)
            .focusOn(YEAR)
            .validationStateIs(month = true, year = true)
            .focusOn(PAN)
            .validationStateIs(month = false, year = false)
    }

    @Test
    fun shouldNotAcceptInvalidYear() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(year = getCurrentYearWithOffset(-1))
            .validationStateIs(year = false)
    }

    @Test
    fun shouldAcceptValidYearUntilFocusMovedAwayFromExpiryDate_whenMonthIsEmpty() {
        cardFragmentTestUtils
            .isInInitialState()
            .cardDetailsAre(month = "")
            .enterCardDetails(year = getCurrentYearWithOffset(0))
            .validationStateIs(month = true, year = true)
            .focusOn(MONTH)
            .validationStateIs(month = true, year = true)
            .focusOn(PAN)
            .validationStateIs(month = false, year = false)
    }

    @Test
    fun shouldAcceptValidMonthUntilFocusMovedAwayFromExpiryDate_whenYearIsEmpty() {
        cardFragmentTestUtils
            .isInInitialState()
            .cardDetailsAre(year = "")
            .enterCardDetails(month = "01")
            .validationStateIs(month = true, year = true)
            .focusOn(YEAR)
            .validationStateIs(month = true, year = true)
            .focusOn(PAN)
            .validationStateIs(month = false, year = false)
    }

    @Test
    fun shouldNotAcceptIncompleteYear() {
        // validation occurs when moved away from expiry month/year
        cardFragmentTestUtils.isInInitialState()
            .enterCardDetails(year = "2")
            .validationStateIs(month = true, year = true)
            .focusOn(MONTH)
            .validationStateIs(month = true, year = true)
            .focusOn(PAN)
            .validationStateIs(month = false, year = false)
    }

    @Test
    fun shouldNotAcceptIncompleteExpiry_incompleteMonth() {
        // validation occurs when moved away from expiry month/year
        cardFragmentTestUtils.isInInitialState()
            .enterCardDetails(month = "1", year = getCurrentYearWithOffset(0))
            .validationStateIs(month = true, year = true)
            .focusOn(MONTH)
            .validationStateIs(month = true, year = true)
            .focusOn(PAN)
            .validationStateIs(month = false, year = false)
    }

    @Test
    fun shouldNotAcceptIncompleteExpiry_incompleteYear() {
        // validation occurs when moved away from expiry month/year
        cardFragmentTestUtils.isInInitialState()
            .enterCardDetails(month = "1", year = "2")
            .validationStateIs(month = true, year = true)
            .focusOn(MONTH)
            .validationStateIs(month = true, year = true)
            .focusOn(PAN)
            .validationStateIs(month = false, year = false)
    }

    @Test
    fun shouldInvalidateExpiryDateAsSoonAsYearIsInvalid_givenValidMonth() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(month = "01")
            .validationStateIs(month = true)
            .enterCardDetails(year = getCurrentYearWithOffset(-1))
            .validationStateIs(month = false, year = false)
    }

    @Test
    fun shouldInvalidateExpiryDateAsSoonAsMonthIsInvalid_givenValidYear() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(year = getCurrentYearWithOffset(1))
            .validationStateIs(year = true)
            .enterCardDetails(month = "13")
            .validationStateIs(month = false, year = false)
    }

    @Test
    fun shouldObserveMaxCharacterLengthForMonthAndYear() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(month = "012")
            .cardDetailsAre(month = "01")

            .enterCardDetails(year = "012")
            .cardDetailsAre(year = "01")
    }

    private fun getCurrentYearWithOffset(offset: Int): String {
        return (Calendar.getInstance().get(Calendar.YEAR)+offset).toString().substring(2)
    }

}
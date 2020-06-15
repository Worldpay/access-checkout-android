package com.worldpay.access.checkout.sample.card

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.worldpay.access.checkout.sample.card.testutil.AbstractCardFragmentTest
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.Year
import kotlin.math.abs

@LargeTest
@RunWith(AndroidJUnit4::class)
class ExpiryDateUITests: AbstractCardFragmentTest() {

    @Test
    fun shouldReformatAllMonthValues() {
        cardFragmentTestUtils.isInInitialState()

        val months: Array<String> = arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")

        for (month in months) {
            cardFragmentTestUtils
                .enterCardDetails(expiryDate = month)
                .cardDetailsAre(expiryDate = "${month}/")
                .validationStateIs(expiryDate = false)
        }
    }

    @Test
    fun shouldReformatAllSingleDigitMonthValues() {
        cardFragmentTestUtils.isInInitialState()

        cardFragmentTestUtils
            .enterCardDetails(expiryDate = "1")
            .cardDetailsAre(expiryDate = "1")
            .validationStateIs(expiryDate = false)

        val months: Array<String> = arrayOf("2", "3", "4", "5", "6", "7", "8", "9")

        for (month in months) {
            cardFragmentTestUtils
                .enterCardDetails(expiryDate = month)
                .cardDetailsAre(expiryDate = "0${month}/")
                .validationStateIs(expiryDate = false)
        }
    }

    @Test
    fun shouldNotAcceptInvalidMonthValue() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(expiryDate = "00")
            .cardDetailsAre(expiryDate = "00/")
            .validationStateIs(expiryDate = false)
    }

    @Test
    fun shouldReformatMonthOver12() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(expiryDate = "13")
            .cardDetailsAre(expiryDate = "01/3")
            .validationStateIs(expiryDate = false)
    }

//    @Test
//    fun shouldAcceptValidYearUntilFocusMovedAwayFromExpiryDate_whenMonthIsEmpty() {
//        cardFragmentTestUtils
//            .isInInitialState()
//            .cardDetailsAre(month = "")
//            .enterCardDetails(year = getCurrentYearWithOffset(0))
//            .validationStateIs(year = true)
//            .focusOn(MONTH)
//            .validationStateIs(year = true)
//            .focusOn(PAN)
//            .validationStateIs(year = false)
//    }

//    @Test
//    fun shouldAcceptValidMonthUntilFocusMovedAwayFromExpiryDate_whenYearIsEmpty() {
//        cardFragmentTestUtils
//            .isInInitialState()
//            .cardDetailsAre(year = "")
//            .enterCardDetails(month = "01")
//            .validationStateIs(year = true)
//            .focusOn(EXPIRY_DATE)
//            .validationStateIs(year = true)
//            .focusOn(PAN)
//            .validationStateIs(year = false)
//    }

    @Test
    fun shouldNotAcceptIncompleteYear() {
        cardFragmentTestUtils.isInInitialState()
            .enterCardDetails(expiryDate = "122")
            .cardDetailsAre(expiryDate = "12/2")
            .validationStateIs(expiryDate = false)
    }

    @Test
    fun shouldReturnInvalidStateWhenYearIsInPast() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(expiryDate = "${getMonth()}/19")
            .validationStateIs(expiryDate = false)
    }

    @Test
    fun shouldReturnInvalidStateWhenMonthIsInPast() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(expiryDate = "${getMonth(-1)}${getYear()}")
            .validationStateIs(expiryDate = false)
    }

    @Test
    fun shouldStripToMaxLength() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(expiryDate = "12244")
            .cardDetailsAre(expiryDate = "12/24")
    }

    private fun getMonth(offset: Int = 0): String {
        var month = LocalDate.now().month.value.toString()

        if (offset < 0) {
            month = LocalDate.now().minusMonths(abs(offset).toLong()).monthValue.toString()
        }

        if (offset > 0) {
            month = LocalDate.now().plusMonths(offset.toLong()).monthValue.toString()
        }

        if (month.length == 1) month = String.format("0%s", month)

        return month
    }

    private fun getYear(offset: Int = 0): String {
        val currentYear = Year.now().value
        return (currentYear + offset).toString().drop(2)
    }

}

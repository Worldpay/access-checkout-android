package com.worldpay.access.checkout.sample.card

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.worldpay.access.checkout.sample.card.testutil.AbstractCardFragmentTest
import com.worldpay.access.checkout.sample.card.testutil.CardFragmentTestUtils.Input.CVC
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

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
                .focusOn(CVC)
                .validationStateIs(expiryDate = false)
        }
    }

    @Test
    fun shouldReformatAllSingleDigitMonthValues() {
        cardFragmentTestUtils.isInInitialState()

        cardFragmentTestUtils
            .enterCardDetails(expiryDate = "1")
            .cardDetailsAre(expiryDate = "1")
            .focusOn(CVC)
            .validationStateIs(expiryDate = false)

        val months: Array<String> = arrayOf("2", "3", "4", "5", "6", "7", "8", "9")

        for (month in months) {
            cardFragmentTestUtils
                .enterCardDetails(expiryDate = month)
                .cardDetailsAre(expiryDate = "0${month}/")
                .focusOn(CVC)
                .validationStateIs(expiryDate = false)
        }
    }

    @Test
    fun shouldNotAcceptInvalidMonthValue() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(expiryDate = "00")
            .cardDetailsAre(expiryDate = "00/")
            .focusOn(CVC)
            .validationStateIs(expiryDate = false)
    }

    @Test
    fun shouldBeInvalidWhenFocusIsLost() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(expiryDate = "00")
            .cardDetailsAre(expiryDate = "00/")
            .focusOn(CVC)
            .validationStateIs(expiryDate = false)
    }

    @Test
    fun shouldReformatMonthOver12() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(expiryDate = "13")
            .cardDetailsAre(expiryDate = "01/3")
            .focusOn(CVC)
            .validationStateIs(expiryDate = false)
    }

    @Test
    fun shouldNotAcceptIncompleteYear() {
        cardFragmentTestUtils.isInInitialState()
            .enterCardDetails(expiryDate = "122")
            .cardDetailsAre(expiryDate = "12/2")
            .focusOn(CVC)
            .validationStateIs(expiryDate = false)
    }

    @Test
    fun shouldReturnInvalidStateWhenYearIsInPast() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(expiryDate = "${getMonth()}/19")
            .focusOn(CVC)
            .validationStateIs(expiryDate = false)
    }

    @Test
    fun shouldReturnInvalidStateWhenMonthIsInPast() {
        cardFragmentTestUtils
            .isInInitialState()
            .enterCardDetails(expiryDate = "${getMonth(-1)}${getYear()}")
            .focusOn(CVC)
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
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, offset)

        var month = calendar.get(Calendar.MONTH).toString()

        if (month.length == 1) month = String.format("0%s", month)

        return month
    }

    private fun getYear(offset: Int = 0): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, offset)

        val currentYear = calendar.get(Calendar.YEAR).toString()
        return (currentYear + offset).drop(2)
    }

}

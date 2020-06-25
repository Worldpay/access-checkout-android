package com.worldpay.access.checkout.validation.listeners.text

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ExpiryDateSanitiserTest {

    private lateinit var expiryDateSanitiser: ExpiryDateSanitiser

    @Before
    fun setup() {
        expiryDateSanitiser = ExpiryDateSanitiser()
    }

    @Test
    fun `should not do anything if input is empty`() {
        assertEquals("", expiryDateSanitiser.sanitise(""))
    }

    @Test
    fun `should not do anything if input has 1 digit only unless value is over 1`() {
        assertEquals("0", expiryDateSanitiser.sanitise("0"))
        assertEquals("1", expiryDateSanitiser.sanitise("1"))
        assertEquals("02/", expiryDateSanitiser.sanitise("2"))
        assertEquals("03/", expiryDateSanitiser.sanitise("3"))
        assertEquals("04/", expiryDateSanitiser.sanitise("4"))
        assertEquals("05/", expiryDateSanitiser.sanitise("5"))
        assertEquals("06/", expiryDateSanitiser.sanitise("6"))
        assertEquals("07/", expiryDateSanitiser.sanitise("7"))
        assertEquals("08/", expiryDateSanitiser.sanitise("8"))
        assertEquals("09/", expiryDateSanitiser.sanitise("9"))
    }

    @Test
    fun `should add separator where input has 2 digits with no separator`() {
        assertEquals("10/", expiryDateSanitiser.sanitise("10"))
        assertEquals("11/", expiryDateSanitiser.sanitise("11"))
        assertEquals("12/", expiryDateSanitiser.sanitise("12"))
    }

    @Test
    fun `should add separator where input has 3 digits with no separator`() {
        assertEquals("11/3", expiryDateSanitiser.sanitise("113"))
    }

    @Test
    fun `should add separator where input has 4 digits with no separator`() {
        assertEquals("11/30", expiryDateSanitiser.sanitise("1130"))
    }

    @Test
    fun `should do nothing where input has 2 digits with separator`() {
        assertEquals("11/", expiryDateSanitiser.sanitise("11/"))
    }

    @Test
    fun `should do nothing where input has 3 digits with separator`() {
        assertEquals("11/3", expiryDateSanitiser.sanitise("11/3"))
    }

    @Test
    fun `should do nothing where input has 4 digits with separator`() {
        assertEquals("11/30", expiryDateSanitiser.sanitise("11/30"))
    }

    @Test
    fun `should move separator where input has separator in wrong place`() {
        assertEquals("11/30", expiryDateSanitiser.sanitise("113/0"))
    }

    @Test
    fun `should remove separator where input has duplicate separators`() {
        assertEquals("11/30", expiryDateSanitiser.sanitise("11/3/0"))
    }

    @Test
    fun `should strip out non numeric except for separator`() {
        assertEquals("02/29", expiryDateSanitiser.sanitise("a2/29"))
        assertEquals("02/9", expiryDateSanitiser.sanitise("aa/29"))
        assertEquals("09/", expiryDateSanitiser.sanitise("aa/a9"))
        assertEquals("", expiryDateSanitiser.sanitise("aa/aa"))
        assertEquals("02/9", expiryDateSanitiser.sanitise("a2/a9"))
        assertEquals("02/", expiryDateSanitiser.sanitise("a/2a"))
    }

    @Test
    fun `should strip out special characters except for separator`() {
        val characters = "!\"#\$%&'()*+,-./:;<=>?@[\\]^_`{|}~"

        for (char in characters.split(".")) {
            assertEquals("02/29", expiryDateSanitiser.sanitise("${char}2/29"))
            assertEquals("02/9", expiryDateSanitiser.sanitise("0${char}/29"))
            assertEquals("02/9", expiryDateSanitiser.sanitise("02/${char}9"))
            assertEquals("02/2", expiryDateSanitiser.sanitise("02/2${char}"))

            assertEquals("02/", expiryDateSanitiser.sanitise("02/${char}${char}"))
            assertEquals("02/", expiryDateSanitiser.sanitise("0${char}/2${char}"))
            assertEquals("02/2", expiryDateSanitiser.sanitise("${char}2/2${char}"))
            assertEquals("02/9", expiryDateSanitiser.sanitise("${char}${char}/29"))
            assertEquals("02/9", expiryDateSanitiser.sanitise("${char}2/${char}9"))
            assertEquals("", expiryDateSanitiser.sanitise("${char}${char}/${char}${char}"))
            assertEquals("", expiryDateSanitiser.sanitise("${char}${char}${char}${char}"))
        }
    }

    @Test
    fun `should reformat input to expiry date format as expected`() {
        assertEquals("01/3", expiryDateSanitiser.sanitise("13"))
        assertEquals("10/0", expiryDateSanitiser.sanitise("100"))
        assertEquals("01/9", expiryDateSanitiser.sanitise("19"))
        assertEquals("02/23", expiryDateSanitiser.sanitise("223"))
        assertEquals("02/24", expiryDateSanitiser.sanitise("2240"))
    }

}

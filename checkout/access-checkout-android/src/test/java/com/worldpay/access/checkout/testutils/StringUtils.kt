package com.worldpay.access.checkout.testutils

fun removeWhitespace(string: String): String {
    return string.replace("\\s".toRegex(), "")
}
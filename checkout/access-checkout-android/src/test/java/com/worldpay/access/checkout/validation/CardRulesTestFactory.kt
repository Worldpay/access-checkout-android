package com.worldpay.access.checkout.validation

object CardRulesTestFactory {

    const val visaCard = "4136000000001"
    const val luhnValidUnknownCardSize19 = "6011462496366668012"
    const val luhnInvalidUnknownCardSize19 = "6022462496366668012"

    const val luhnValidVisaCardSize16 = "4026344341791618"
}
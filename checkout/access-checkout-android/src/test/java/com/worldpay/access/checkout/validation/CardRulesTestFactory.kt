package com.worldpay.access.checkout.validation

object CardRulesTestFactory {

    val visaCard = "4136000000001"
    val mastercardCard = "5555555555554444"
    val amexCard = "343434343434343"
    val unknownCard = "1111111111111111"
    val luhnValidUnknownCardSize19 = "6011462496366668012"
    val luhnInvalidUnknownCardSize19 = "6022462496366668012"

    val luhnValidVisaCardSize16 = "4026344341791618"
    val luhnValidVisaCardSize19 = "4024007128904375837"
    val luhnInvalidVisaCardSize19 = "4024001728904375837"
    val luhnInvalidVisaCardSize16 = "4024001728904375"

    val visaBrand = "visa"
    val amexBrand ="amex"
    val mastercardBrand = "mastercard"

    val validVisaStartRange = "413600"
}
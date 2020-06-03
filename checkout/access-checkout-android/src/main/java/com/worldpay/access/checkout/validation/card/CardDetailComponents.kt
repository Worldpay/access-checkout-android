package com.worldpay.access.checkout.validation.card

import android.widget.EditText

class CardDetailComponents(
    val pan: EditText,
    val expiryMonth: EditText,
    val expiryYear: EditText,
    val cvv: EditText
)
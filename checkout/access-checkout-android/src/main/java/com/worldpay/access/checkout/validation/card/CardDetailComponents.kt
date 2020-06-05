package com.worldpay.access.checkout.validation.card

import android.widget.EditText

class CardDetailComponents(
    val pan: EditText? = null,
    val expiryMonth: EditText? = null,
    val expiryYear: EditText? = null,
    val cvv: EditText? = null
)
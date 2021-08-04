package com.worldpay.access.checkout.validation.listeners.text

import android.text.TextWatcher

internal abstract class AbstractCardDetailTextWatcher : TextWatcher {

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
}

package com.worldpay.access.checkout.validation.decorators

import android.text.InputFilter
import android.widget.EditText
import com.worldpay.access.checkout.validation.filters.AccessCheckoutInputFilter

internal abstract class AbstractFieldDecorator {

    protected fun applyFilter(
        editText: EditText,
        accessCheckoutInputFilter: AccessCheckoutInputFilter
    ) {
        val filters = editText.filters
            .filter { it !is AccessCheckoutInputFilter && it !is InputFilter.LengthFilter }
            .toMutableList()

        filters.add(accessCheckoutInputFilter)
        editText.filters = filters.toTypedArray()
    }
}

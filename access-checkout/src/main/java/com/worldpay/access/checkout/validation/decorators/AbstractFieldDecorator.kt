package com.worldpay.access.checkout.validation.decorators

import android.text.InputFilter
import android.widget.EditText
import com.worldpay.access.checkout.validation.filters.AccessCheckoutInputFilter

internal abstract class AbstractFieldDecorator {

    protected fun applyFilter(
        editText: EditText,
        accessCheckoutInputFilter: AccessCheckoutInputFilter
    ) {
        val filters = mutableListOf<InputFilter>()
        for (filter in editText.filters) {
            if (filter !is AccessCheckoutInputFilter && filter !is InputFilter.LengthFilter) {
                filters.add(filter)
            }
        }

        filters.add(accessCheckoutInputFilter)
        editText.filters = filters.toTypedArray()
    }
}

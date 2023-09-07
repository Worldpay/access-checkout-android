package com.worldpay.access.checkout.validation.decorators

import android.text.InputFilter
import android.widget.EditText
import com.worldpay.access.checkout.ui.AccessEditText
import com.worldpay.access.checkout.validation.filters.AccessCheckoutInputFilter

internal abstract class AbstractFieldDecorator {

    protected fun applyFilter(
        accessEditText: AccessEditText,
        accessCheckoutInputFilter: AccessCheckoutInputFilter
    ) {
        val filters = mutableListOf<InputFilter>()
        for (filter in accessEditText.filters) {
            if (filter !is AccessCheckoutInputFilter && filter !is InputFilter.LengthFilter) {
                filters.add(filter)
            }
        }

        filters.add(accessCheckoutInputFilter)
        accessEditText.filters = filters.toTypedArray()
    }
}

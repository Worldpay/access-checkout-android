package com.worldpay.access.checkout.validation.decorators

import android.text.InputFilter
import android.widget.EditText
import com.worldpay.access.checkout.validation.filters.AbstractVariableLengthFilter

internal abstract class AbstractFieldDecorator {

    protected fun applyFilter(
        editText: EditText,
        abstractVariableLengthFilter: AbstractVariableLengthFilter
    ) {
        val filters = mutableListOf<InputFilter>()
        for (filter in editText.filters) {
            if (filter !is AbstractVariableLengthFilter && filter !is InputFilter.LengthFilter) {
                filters.add(filter)
            }
        }

        filters.add(abstractVariableLengthFilter)
        editText.filters = filters.toTypedArray()
    }
}

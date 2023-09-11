package com.worldpay.access.checkout.ui

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.worldpay.access.checkout.R

// class is final and public by default
class AccessEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {
    var mCustomHint: String? = null
    private lateinit var editText: EditText

    // with the internal access modifier, this property is internal to the Access Checkout SDK JAR file
    // (or access-checkout Gradle project when adding the dependency as a project dependency)
    // and cannot be accessed outside of that module, e.g. cannot be accessed by a merchant's app
    internal val text: String get() = editText.text.toString()

    val selectionEnd: Int get() = editText.selectionEnd

    val editableText: Editable get() = editText.editableText

    val isCursorVisible: Boolean get() = editText.isCursorVisible

    var filters: Array<InputFilter>
        get() {
            return editText.filters
        }
        set(filters) {
            editText.filters = filters
        }

    var inputType: Int
        get() {
            return editText.inputType
        }
        set(inputType) {
            editText.inputType = inputType
        }

    fun setText(text: CharSequence) = editText.setText(text)
    fun length(): Int = editText.length()
    fun removeTextChangedListener(watcher: TextWatcher?) = editText.removeTextChangedListener(watcher)
    fun addTextChangedListener(watcher: TextWatcher?) = editText.addTextChangedListener(watcher)
    fun setSelection(index: Int) = editText.setSelection(index)
    fun setTextColor(color: Int) = editText.setTextColor(color)
    fun clear() = editText.text.clear()

    private fun createEditText(): View {
        editText = EditText(context)
        editText.setHint(mCustomHint)
        return editText
    }

    init {
        orientation = VERTICAL
        context.withStyledAttributes(attrs, R.styleable.AccessEditText) {
            mCustomHint = getString(R.styleable.AccessEditText_customHint)
        }

        addView(createEditText())
    }
}

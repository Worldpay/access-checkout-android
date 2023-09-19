package com.worldpay.access.checkout.ui

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.KeyListener
import android.util.AttributeSet
import android.view.KeyEvent
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
    private var mCustomHint: String? = null
    private lateinit var editText: EditText

    // with the internal access modifier, this property is internal to the Access Checkout SDK JAR file
    // (or access-checkout Gradle project when adding the dependency as a project dependency)
    // and cannot be accessed outside of that module, e.g. cannot be accessed by a merchant's app
    internal val text: String get() = editText.text.toString()

    val selectionEnd: Int get() = editText.selectionEnd

    val selectionStart: Int get() = editText.selectionStart

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

    var keyListener: KeyListener
        get() {
            return editText.keyListener
        }
        set(input) {
            editText.keyListener = input
        }

    fun setText(text: CharSequence) = editText.setText(text)
    fun length(): Int = editText.length()
    fun removeTextChangedListener(watcher: TextWatcher?) = editText.removeTextChangedListener(watcher)
    fun addTextChangedListener(watcher: TextWatcher?) = editText.addTextChangedListener(watcher)
    fun setSelection(index: Int) = editText.setSelection(index)
    fun setSelection(start: Int, stop: Int) = editText.setSelection(start, stop)
    fun setTextColor(color: Int) = editText.setTextColor(color)
    fun clear() = editText.text.clear()
    fun insert(where: Int, text: CharSequence): Editable = editText.text.insert(where, text)
    fun replace(st: Int, en: Int, text: CharSequence): Editable = editText.text.replace(st, en, text)
    fun setHint(hint: CharSequence) = editText.setHint(hint)
    fun setHint(int: Int) = editText.setHint(int)
    fun append(text: CharSequence) = editText.append(text)
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return editText.dispatchKeyEvent(event)
    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        editText.onFocusChangeListener = l
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return editText.dispatchKeyEvent(event)
    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        editText.onFocusChangeListener = l
    }

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

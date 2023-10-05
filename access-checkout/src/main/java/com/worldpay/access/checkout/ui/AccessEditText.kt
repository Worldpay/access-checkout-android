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

// class is final and public by default
class AccessEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : LinearLayout(context, attrs, defStyle) {
    private var mCustomHint: String? = null
    private var editText: EditText? = null

    internal constructor(
        context: Context,
        editText: EditText,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
    ) : this(context, attrs, defStyle) {
        this.editText = editText
    }

    init {
        orientation = VERTICAL
//        context.withStyledAttributes(attrs, R.styleable.AccessEditText) {
//            mCustomHint = getString(R.styleable.AccessEditText_customHint)
//        }

        addView(createEditText())
    }

    // with the internal access modifier, this property is internal to the Access Checkout SDK JAR file
    // (or access-checkout Gradle project when adding the dependency as a project dependency)
    // and cannot be accessed outside of that module, e.g. cannot be accessed by a merchant's app
    internal val text: String get() = editText!!.text.toString()

    val selectionEnd: Int get() = editText!!.selectionEnd

    val selectionStart: Int get() = editText!!.selectionStart

    internal val editableText: Editable get() = editText!!.editableText

    val isCursorVisible: Boolean get() = editText!!.isCursorVisible
    val currentTextColor: Int get() = editText!!.currentTextColor
    internal fun setTextColor(color: Int) = editText!!.setTextColor(color)

    internal var filters: Array<InputFilter>
        get() {
            return editText!!.filters
        }
        set(filters) {
            editText!!.filters = filters
        }

    var inputType: Int
        get() {
            return editText!!.inputType
        }
        set(inputType) {
            editText!!.inputType = inputType
        }

    internal var keyListener: KeyListener
        get() {
            return editText!!.keyListener
        }
        set(input) {
            editText!!.keyListener = input
        }

    internal fun length(): Int = editText!!.length()

    internal fun setSelection(index: Int) = editText!!.setSelection(index)

    internal fun setSelection(start: Int, stop: Int) = editText!!.setSelection(start, stop)

    internal fun setText(text: CharSequence) = editText!!.setText(text)

    internal fun removeTextChangedListener(watcher: TextWatcher?) = editText!!.removeTextChangedListener(watcher)

    internal fun addTextChangedListener(watcher: TextWatcher?) = editText!!.addTextChangedListener(watcher)

    internal fun setHint(hint: CharSequence) = editText!!.setHint(hint)
    internal fun setHint(resId: Int) = editText!!.setHint(resId)
    internal fun getHint(): CharSequence = editText!!.hint

    internal fun clearText() = editText!!.text.clear()

    internal fun insertText(where: Int, text: CharSequence): Editable = editText!!.text.insert(where, text)

    internal fun replaceText(st: Int, en: Int, text: CharSequence): Editable = editText!!.text.replace(st, en, text)

    internal fun appendText(text: CharSequence) = editText!!.append(text)

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return editText!!.dispatchKeyEvent(event)
    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        editText!!.onFocusChangeListener = l
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return editText.dispatchKeyEvent(event)
    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        editText.onFocusChangeListener = l
    }

    private fun createEditText(): View {
        editText = EditText(context)
        editText!!.setHint(mCustomHint)
        return editText!!
    }
}

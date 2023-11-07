package com.worldpay.access.checkout.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.text.InputFilter
import android.text.method.KeyListener
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.widget.TextViewCompat
import com.worldpay.access.checkout.R
import kotlin.random.Random

class AccessCheckoutEditText internal constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int,
    internal val editText: EditText,
) : LinearLayout(context, attrs, defStyle) {
    internal companion object {
        internal val editTextPartialId = Random.nextInt(1000)
    }

    init {
        orientation = VERTICAL
        this.editText.id = this.id + editTextPartialId

        attrs?.let {
            val styledAttributes: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.AccessCheckoutEditText, 0, 0)

            val attributeValues = AttributeValues(styledAttributes)

            attributeValues.setAttributesOnEditText(this.editText)

            styledAttributes.recycle()
        }

        addView(this.editText)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
        this(context, attrs, defStyle, EditText(context))

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    /**
     * Core properties
     */
    internal val text: String get() = editText.text.toString()
    fun setText(text: String) = editText.setText(text)

    val selectionStart: Int get() = editText.selectionStart
    val selectionEnd: Int get() = editText.selectionEnd
    fun setSelection(start: Int, stop: Int) = editText.setSelection(start, stop)

    val currentTextColor: Int get() = editText.currentTextColor
    fun setTextColor(color: Int) = editText.setTextColor(color)

    val currentHintTextColor: Int @ColorInt get() = editText.currentHintTextColor

    val autoSizeTextType: Int get() = editText.autoSizeTextType

    var imeOptions: Int
        get() {
            return editText.imeOptions
        }
        set(imeOptions) {
            editText.imeOptions = imeOptions
        }

    var textScaleX: Float
        get() {
            return editText.textScaleX
        }
        set(size) {
            editText.textScaleX = size
        }

    var textSize: Float
        get() {
            return editText.textSize
        }
        set(size) {
            editText.textSize = size
        }

    var typeface: Typeface
        get() {
            return editText.typeface
        }
        set(tf) {
            editText.typeface = tf
        }

    var isCursorVisible: Boolean
        get() {
            return editText.isCursorVisible
        }
        set(visible) {
            editText.isCursorVisible = visible
        }

    internal var inputType: Int
        get() {
            return editText.inputType
        }
        set(inputType) {
            editText.inputType = inputType
        }

    internal var filters: Array<InputFilter>
        get() {
            return editText.filters
        }
        set(filters) {
            editText.filters = filters
        }

    internal var keyListener: KeyListener
        get() {
            return editText.keyListener
        }
        set(input) {
            editText.keyListener = input
        }

    /**
     * Methods
     */
    fun clear() = editText.text.clear()

    fun setEms(ems: Int) = editText.setEms(ems)

    fun setHintTextColor(@ColorInt color: Int) {
        editText.setHintTextColor(color)
    }

    fun setAutoSizeTextTypeWithDefaults(@TextViewCompat.AutoSizeTextType autoSizeTextType: Int) {
        editText.setAutoSizeTextTypeWithDefaults(autoSizeTextType)
    }

    fun setTextAppearance(@StyleRes resId: Int) {
        editText.setTextAppearance(resId)
    }

    internal fun length(): Int = editText.length()

    internal fun getHint(): CharSequence = editText.hint

    internal fun setHint(hint: CharSequence) = editText.setHint(hint)

    internal fun setHint(resId: Int) = editText.setHint(resId)

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return editText.dispatchKeyEvent(event)
    }

    override fun getOnFocusChangeListener(): OnFocusChangeListener {
        return editText.onFocusChangeListener
    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        editText.onFocusChangeListener = l
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val editTextState = editText.onSaveInstanceState()
        return Bundle().apply {
            putParcelable("superState", super.onSaveInstanceState())
            putParcelable("editTextState", editTextState)
        }
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val bundledState = (state as Bundle)

        super.onRestoreInstanceState(bundledState.getBundle("superState"))
        editText.onRestoreInstanceState(bundledState.getBundle("editTextState"))
    }
}

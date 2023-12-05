package com.worldpay.access.checkout.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.method.KeyListener
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.widget.TextViewCompat
import com.worldpay.access.checkout.R
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class AccessCheckoutEditText internal constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int,
    internal val editText: EditText?,
) : LinearLayout(context, attrs, defStyle) {
    internal companion object {
        private const val SUPER_STATE_KEY = "superState"
        private const val EDIT_TEXT_STATE_KEY = "editTextState"

        private val allEditTextIds = ConcurrentHashMap<Int, Int?>()

        fun editTextIdOf(accessCheckoutEditTextId: Int): Int {
            val newId = View.generateViewId()
            val editTextId = allEditTextIds.putIfAbsent(accessCheckoutEditTextId, newId)
            return Optional.ofNullable(editTextId).orElse(newId)
        }
    }

    init {
        orientation = VERTICAL
        super.setPadding(0, 0, 0, 0)

        this.editText?.let { editText ->
            editText.id = editTextIdOf(this.id)

            attrs?.let {
                val styledAttributes: TypedArray =
                    context.obtainStyledAttributes(attrs, R.styleable.AccessCheckoutEditText, 0, 0)

                val attributeValues = AttributeValues(styledAttributes)
                attributeValues.setAttributesOnEditText(editText)

                styledAttributes.recycle()
            }

            addView(this.editText)
        }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
        this(context, attrs, defStyle, EditText(context))

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    /**
     * Core properties
     */
    internal val text: String get() = editText!!.text.toString()
    fun setText(text: String) = editText!!.setText(text)

    val selectionStart: Int get() = editText!!.selectionStart
    val selectionEnd: Int get() = editText!!.selectionEnd
    fun setSelection(start: Int, stop: Int) = editText!!.setSelection(start, stop)

    val currentTextColor: Int get() = editText!!.currentTextColor
    fun setTextColor(color: Int) = editText!!.setTextColor(color)

    val currentHintTextColor: Int @ColorInt get() = editText!!.currentHintTextColor

    var imeOptions: Int
        get() {
            return editText!!.imeOptions
        }
        set(imeOptions) {
            editText!!.imeOptions = imeOptions
        }

    var textSize: Float
        get() {
            return editText!!.textSize
        }
        set(size) {
            editText!!.textSize = size
        }

    var typeface: Typeface
        get() {
            return editText!!.typeface
        }
        set(tf) {
            editText!!.typeface = tf
        }

    var isCursorVisible: Boolean
        get() {
            return editText!!.isCursorVisible
        }
        set(visible) {
            editText!!.isCursorVisible = visible
        }

    internal var inputType: Int
        get() {
            return editText!!.inputType
        }
        set(inputType) {
            editText!!.inputType = inputType
        }

    internal var filters: Array<InputFilter>
        get() {
            return editText!!.filters
        }
        set(filters) {
            editText!!.filters = filters
        }

    internal var keyListener: KeyListener
        get() {
            return editText!!.keyListener
        }
        set(input) {
            editText!!.keyListener = input
        }

    /**
     * Methods
     */
    fun clear() {
        editText!!.text = SpannableStringBuilder("", 0, 0)
    }

    fun setHintTextColor(@ColorInt color: Int) {
        editText!!.setHintTextColor(color)
    }

    fun setAutoSizeTextTypeWithDefaults(@TextViewCompat.AutoSizeTextType autoSizeTextType: Int) {
        editText!!.setAutoSizeTextTypeWithDefaults(autoSizeTextType)
    }

    fun setTextAppearance(@StyleRes resId: Int) {
        editText!!.setTextAppearance(resId)
    }

    internal fun length(): Int = editText!!.length()

    fun getHint(): CharSequence = editText!!.hint

    fun setHint(hint: CharSequence) = editText!!.setHint(hint)

    fun setHint(resId: Int) = editText!!.setHint(resId)

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        editText!!.setPadding(left, top, right, bottom)
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        editText!!.setPaddingRelative(start, top, end, bottom)
    }

    override fun getBackground(): Drawable? {
        return this.editText?.background
    }

    override fun setBackground(background: Drawable?) {
        this.editText?.background = background
    }

    override fun setBackgroundColor(color: Int) {
        this.editText!!.setBackgroundColor(color)
    }

    override fun setBackgroundResource(resId: Int) {
        this.editText!!.setBackgroundResource(resId)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return editText!!.dispatchKeyEvent(event)
    }

    override fun getOnFocusChangeListener(): OnFocusChangeListener {
        return editText!!.onFocusChangeListener
    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        editText!!.onFocusChangeListener = l
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val editTextState = editText!!.onSaveInstanceState()
        return Bundle().apply {
            putParcelable(SUPER_STATE_KEY, super.onSaveInstanceState())
            putParcelable(EDIT_TEXT_STATE_KEY, editTextState)
        }
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val bundledState = (state as Bundle)

        super.onRestoreInstanceState(bundledState.getParcelable(SUPER_STATE_KEY))
        editText!!.onRestoreInstanceState(bundledState.getParcelable(EDIT_TEXT_STATE_KEY))
    }
}

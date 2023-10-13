package com.worldpay.access.checkout.ui

import android.content.Context
import android.os.Parcelable
import android.text.InputFilter
import android.text.method.KeyListener
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText
import android.widget.LinearLayout
import kotlin.random.Random

class AccessEditText internal constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int,
    internal val editText: EditText,
    private val attributeValues: AttributeValues,
) : LinearLayout(context, attrs, defStyle) {
    companion object {
        internal val editTextPartialId = Random.nextInt()
    }

    init {
        orientation = VERTICAL
        this.editText.id = this.id + editTextPartialId

        addView(this.editText)
        this.attributeValues.stringOf("hint")?.let { setHint(it) }
        this.attributeValues.stringOfOther("id")?.let { this.editText.id = it }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
            this(context, attrs, defStyle, EditText(context), AttributeValues(context, attrs))

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    /**
     * Core properties
     */
    internal val text: String get() = editText.text.toString()
    fun setText(text: String) {
        editText.setText(text)
    }

    val selectionStart: Int get() = editText.selectionStart
    val selectionEnd: Int get() = editText.selectionEnd
    fun setSelection(start: Int, stop: Int) = editText.setSelection(start, stop)

    val isCursorVisible: Boolean get() = editText.isCursorVisible

    val currentTextColor: Int get() = editText.currentTextColor
    fun setTextColor(color: Int) = editText.setTextColor(color)

    internal var filters: Array<InputFilter>
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

    internal var keyListener: KeyListener
        get() {
            return editText.keyListener
        }
        set(input) {
            editText.keyListener = input
        }

    internal fun getHint(): CharSequence = editText.hint
    internal fun setHint(hint: CharSequence) {
        editText.hint = hint
    }

    internal fun setHint(resId: Int) = editText.setHint(resId)

    /**
     * Methods
     */
    internal fun length(): Int = editText.length()

    fun clear() = editText.text.clear()

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
        super.onSaveInstanceState()
        return editText.onSaveInstanceState()
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        editText.onRestoreInstanceState(state)
        super.onRestoreInstanceState(state)
    }
}

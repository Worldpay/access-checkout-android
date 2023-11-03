package com.worldpay.access.checkout.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.InputFilter
import android.text.method.KeyListener
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
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

        val attributes: TypedArray? = attrs?.let { setAttributes(context, attrs, defStyle) }

        addView(this.editText)

        attributes?.let { attributes.recycle() }
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

    val autoSizeTextType: Int @RequiresApi(Build.VERSION_CODES.O) get() = editText.autoSizeTextType

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun setAutoSizeTextTypeWithDefaults(@TextViewCompat.AutoSizeTextType autoSizeTextType: Int) {
        editText.setAutoSizeTextTypeWithDefaults(autoSizeTextType)
    }

    @RequiresApi(Build.VERSION_CODES.M)
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

    private fun setAttributes(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ): TypedArray {
        val styleAttributes: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.AccessCheckoutEditText, defStyle, 0)

        val textColor = getTextColorAttribute(styleAttributes)
        setTextColourAttribute(textColor)

        val hint = getHintAttribute(styleAttributes)
        setHintAttribute(hint)

        val ems = getEmsAttribute(styleAttributes)
        setEmsAttribute(ems)

        val hintTextColour = getHintTextColourAttribute(styleAttributes)
        setHintTextColourAttribute(hintTextColour)

        val imeOptions = getImeOptionsAttribute(styleAttributes)
        setImeOptionsAttribute(imeOptions)

        val cursorVisible = getCursorVisibleAttribute(styleAttributes)
        setCursorVisibleAttribute(cursorVisible)

        val textScaleX = getTextScaleXAttribute(styleAttributes)
        setTextScaleXAttribute(textScaleX)

        val textSize = getTextSizeAttribute(styleAttributes)
        setTextSizeAttribute(textSize)

        // set padding
        val padding = getPaddingAttribute(styleAttributes).toInt()
        val paddingLeft = getPaddingLeftAttribute(styleAttributes).toInt()
        val paddingTop = getPaddingTopAttribute(styleAttributes).toInt()
        val paddingRight = getPaddingRightAttribute(styleAttributes).toInt()
        val paddingBottom = getPaddingBottomAttribute(styleAttributes).toInt()

        setPaddingAttribute(paddingLeft, paddingTop, paddingRight, paddingBottom, padding)

        return styleAttributes
    }

    private fun setPaddingAttribute(
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int,
        padding: Int
    ) {
        var paddingLeft1 = paddingLeft
        var paddingTop1 = paddingTop
        var paddingRight1 = paddingRight
        var paddingBottom1 = paddingBottom

        paddingLeft1.takeIf { it == 0 }?.let { paddingLeft1 = editText.paddingLeft }
        paddingTop1.takeIf { it == 0 }?.let { paddingTop1 = editText.paddingTop }
        paddingRight1.takeIf { it == 0 }?.let { paddingRight1 = editText.paddingRight }
        paddingBottom1.takeIf { it == 0 }?.let { paddingBottom1 = editText.paddingBottom }

        if (padding != 0) {
            paddingLeft1 = padding
            paddingTop1 = padding
            paddingRight1 = padding
            paddingBottom1 = padding
        }

        editText.setPadding(paddingLeft1, paddingTop1, paddingRight1, paddingBottom1)
    }

    private fun setCursorVisibleAttribute(cursorVisible: Boolean) {
        this.isCursorVisible = cursorVisible
    }

    private fun setHintAttribute(hint: String?) {
        hint?.let {
            this.setHint(it as CharSequence)
        }
    }

    private fun setTextScaleXAttribute(textScaleX: Float) {
        textScaleX.takeIf { it != 0F }?.let { this.textScaleX = textScaleX }
    }

    private fun setTextSizeAttribute(textSize: Float) {
        textSize.takeIf { it != 0F }?.let { this.textSize = textSize }
    }

    private fun setImeOptionsAttribute(imeOptions: Int) {
        imeOptions.takeIf { it != 0 }?.let { this.imeOptions = imeOptions }
    }

    private fun setHintTextColourAttribute(hintTextColour: Int) {
        hintTextColour.takeIf { it != 0 }?.let { this.setHintTextColor(hintTextColour) }
    }

    private fun setEmsAttribute(ems: Int) {
        ems.takeIf { it != 0 }?.let { this.setEms(ems) }
    }

    private fun setTextColourAttribute(textColor: Int) {
        textColor.takeIf { it != 0 }?.let { this.setTextColor(textColor) }
    }

    private fun getPaddingBottomAttribute(styleAttributes: TypedArray) =
        styleAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_paddingBottom, 0.0F)

    private fun getPaddingRightAttribute(styleAttributes: TypedArray) =
        styleAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_paddingRight, 0.0F)

    private fun getPaddingTopAttribute(styleAttributes: TypedArray) =
        styleAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_paddingTop, 0.0F)

    private fun getPaddingLeftAttribute(styleAttributes: TypedArray) =
        styleAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_paddingLeft, 0.0F)

    private fun getPaddingAttribute(styleAttributes: TypedArray) =
        styleAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_padding, 0.0F)

    private fun getHintAttribute(styleAttributes: TypedArray) =
        styleAttributes.getString(R.styleable.AccessCheckoutEditText_android_hint)

    private fun getTextSizeAttribute(styleAttributes: TypedArray) =
        styleAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_textSize, 0F)

    private fun getTextScaleXAttribute(styleAttributes: TypedArray) =
        styleAttributes.getFloat(R.styleable.AccessCheckoutEditText_android_textScaleX, 0F)

    private fun getCursorVisibleAttribute(styleAttributes: TypedArray) =
        styleAttributes.getBoolean(R.styleable.AccessCheckoutEditText_android_cursorVisible, true)

    private fun getImeOptionsAttribute(styleAttributes: TypedArray) =
        styleAttributes.getInt(R.styleable.AccessCheckoutEditText_android_imeOptions, 0)

    private fun getHintTextColourAttribute(styleAttributes: TypedArray) =
        styleAttributes.getColor(R.styleable.AccessCheckoutEditText_android_textColorHint, 0)

    private fun getEmsAttribute(styleAttributes: TypedArray) =
        styleAttributes.getInt(R.styleable.AccessCheckoutEditText_android_ems, 0)

    private fun getTextColorAttribute(styleAttributes: TypedArray) =
        styleAttributes.getColor(R.styleable.AccessCheckoutEditText_android_textColor, 0)
}

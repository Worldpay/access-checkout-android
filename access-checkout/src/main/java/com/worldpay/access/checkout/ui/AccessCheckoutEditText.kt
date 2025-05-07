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
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.widget.TextViewCompat
import com.worldpay.access.checkout.R
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap

/**
 * A UI component to capture the pan, expiry date or cvc of a payment card without being exposed to the text entered by the shopper.
 * This design is to allow merchants to reach the lowest level of compliance (SAQ-A)
 */
class AccessCheckoutEditText internal constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int,
    internal val editText: EditText?,
) : FrameLayout(context, attrs, defStyle) {
    private var externalOnFocusChangeListener: OnFocusChangeListener? = null

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
     * Properties
     */
    internal val text: String get() = editText!!.text.toString()
    fun setText(text: String) = editText!!.setText(text)

    val selectionStart: Int get() = editText!!.selectionStart
    val selectionEnd: Int get() = editText!!.selectionEnd
    fun setSelection(start: Int, stop: Int) = editText!!.setSelection(start, stop)

    /**
     * Return the current color selected for normal text.
     *
     * @return Returns the current text color.
     */
    val currentTextColor: Int get() = editText!!.currentTextColor

    /**
     * Sets the text color for all the states (normal, selected,
     * focused) to be this color.
     *
     * @param color A color value in the form 0xAARRGGBB
     */
    fun setTextColor(color: Int) = editText!!.setTextColor(color)

    /**
     * Return the current color selected to paint the hint text.
     *
     * @return Returns the current hint text color.
     */
    val currentHintTextColor: Int @ColorInt get() = editText!!.currentHintTextColor

    /**
     * Sets the color selected to paint the hint text.
     *
     * @param color the color to paint the hint text
     */
    fun setHintTextColor(@ColorInt color: Int) {
        editText!!.setHintTextColor(color)
    }

    /**
     * The type of the Input Method Editor (IME).
     */
    var imeOptions: Int
        get() {
            return editText!!.imeOptions
        }
        set(imeOptions) {
            editText!!.imeOptions = imeOptions
        }

    /**
     * The size (in pixels) of the default text size in this component.
     */
    var textSize: Float
        get() {
            return editText!!.textSize
        }
        set(size) {
            editText!!.textSize = size
        }

    /**
     * The current Typeface that is used to style the text.
     */
    var typeface: Typeface
        get() {
            return editText!!.typeface
        }
        set(tf) {
            editText!!.typeface = tf
        }

    /**
     * @return whether or not the cursor is visible
     */
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
     * Returns whether this component is enabled
     *
     * @return whether this component is enabled
     */
    override fun isEnabled(): Boolean {
        return editText!!.isEnabled
    }

    /**
     * Enables/Disables this component
     *
     * @param enabled a boolean indicating whether to enable this component
     */
    override fun setEnabled(enabled: Boolean) {
        editText!!.setEnabled(enabled)
    }

    /**
     * Returns the hint that is displayed when the text is empty.
     *
     * @return the hint displayed when the text is empty
     */
    fun getHint(): CharSequence = editText!!.hint

    /**
     * Sets the hint that is displayed when the text is empty.
     *
     * @param hint to be displayed when the text is empty
     */
    fun setHint(hint: CharSequence) = editText!!.setHint(hint)

    /**
     * Sets the hint that is displayed when the text is empty.
     *
     * @param resId resource id of the hint to be displayed when the text is empty
     */
    fun setHint(resId: Int) = editText!!.setHint(resId)

    /**
     * Returns the background drawn in this component
     *
     * @return background drawn in this component
     */
    override fun getBackground(): Drawable? {
        return this.editText?.background
    }

    /**
     * Sets the background to be drawn in this component
     *
     * @param background to be drawn in this component
     */
    override fun setBackground(background: Drawable?) {
        this.editText?.background = background
    }

    /**
     * Sets the background to be drawn in this component using a resource id
     *
     * @param resId id of the resource to use to draw the background in this component
     */
    override fun setBackgroundResource(resId: Int) {
        this.editText!!.setBackgroundResource(resId)
    }

    /**
     * Sets the background color to be painted in this component
     *
     * @param color color to use to paint the background in this component
     */
    override fun setBackgroundColor(color: Int) {
        this.editText!!.setBackgroundColor(color)
    }

    /**
     * Methods
     */

    /**
     * Clears the text entered by the shopper
     */
    fun clear() {
        editText!!.text = SpannableStringBuilder("", 0, 0)
    }

    /**
     * Specify whether this widget should automatically scale the text to try to perfectly fit
     *
     * @param autoSizeTextType the type of auto-size
     */
    fun setAutoSizeTextTypeWithDefaults(@TextViewCompat.AutoSizeTextType autoSizeTextType: Int) {
        editText!!.setAutoSizeTextTypeWithDefaults(autoSizeTextType)
    }

    /**
     * Sets the text appearance from the specified style resource.
     *
     * @param resId the resource identifier of the style to apply
     */
    fun setTextAppearance(@StyleRes resId: Int) {
        editText!!.setTextAppearance(resId)
    }

    internal fun length(): Int = editText!!.length()

    /**
     * Sets the left, top, right and bottom padding
     *
     * @param left left padding
     * @param top top padding
     * @param right right padding
     * @param bottom bottom padding
     */
    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        editText!!.setPadding(left, top, right, bottom)
    }

    /**
     * Sets the start, top, end and bottom padding
     *
     * @param start start padding
     * @param top top padding
     * @param end end padding
     * @param bottom bottom padding
     */
    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        editText!!.setPaddingRelative(start, top, end, bottom)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return editText!!.dispatchKeyEvent(event)
    }

    override fun getOnFocusChangeListener(): OnFocusChangeListener? {
        return this.externalOnFocusChangeListener
    }

    override fun setOnFocusChangeListener(event: OnFocusChangeListener?) {
        this.externalOnFocusChangeListener = event
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

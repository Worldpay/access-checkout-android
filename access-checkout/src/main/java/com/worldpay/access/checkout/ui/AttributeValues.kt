package com.worldpay.access.checkout.ui

import android.content.res.TypedArray
import android.widget.EditText
import com.worldpay.access.checkout.R

internal class AttributeValues(
    private val styledAttributes: TypedArray
) {

    internal fun setAttributesOnEditText(editText: EditText) {
        setTextColourAttribute(editText)

        setHintAttribute(editText)

        setHintTextColourAttribute(editText)

        setImeOptionsAttribute(editText)

        setCursorVisibleAttribute(editText)

        setTextSizeAttribute(editText)

        setBackgroundAttribute(editText)

        setPaddingAttribute(editText)

        setFontAttribute(editText)

    }

    private fun setPaddingAttribute(editText: EditText) {
        val padding = getPaddingAttribute(defaultValue = 0)
        if (padding != 0) {
            editText.setPadding(padding, padding, padding, padding)
            return
        }

        val left = getPaddingLeftAttribute(defaultValue = editText.paddingLeft)
        val top = getPaddingTopAttribute(defaultValue = editText.paddingTop)
        val right = getPaddingRightAttribute(defaultValue = editText.paddingRight)
        val bottom = getPaddingBottomAttribute(defaultValue = editText.paddingBottom)
        val start = getPaddingStartAttribute(defaultValue = editText.paddingStart)
        val end = getPaddingEndAttribute(defaultValue = editText.paddingEnd)

        if (left > 0 || right > 0) {
            editText.setPadding(left, top, right, bottom)
        } else {
            editText.setPaddingRelative(start, top, end, bottom)
        }
    }

    private fun setCursorVisibleAttribute(editText: EditText) {
        val cursorVisible = getCursorVisibleAttribute()
        editText.isCursorVisible = cursorVisible
    }

    private fun setHintAttribute(editText: EditText) {
        val hint = getHintAttribute()
        hint?.let {
            editText.setHint(it as CharSequence)
        }
    }

    private fun setTextSizeAttribute(editText: EditText) {
        val textSize = getTextSizeAttribute()
        textSize.takeIf { it != 0F }?.let { editText.textSize = textSize }
    }

    private fun setImeOptionsAttribute(editText: EditText) {
        val imeOptions = getImeOptionsAttribute()
        imeOptions.takeIf { it != 0 }?.let { editText.imeOptions = imeOptions }
    }

    private fun setHintTextColourAttribute(editText: EditText) {
        val hintTextColour = getHintTextColourAttribute()
        hintTextColour.takeIf { it != 0 }?.let { editText.setHintTextColor(hintTextColour) }
    }

    private fun setTextColourAttribute(editText: EditText) {
        val textColor = getTextColorAttribute()
        textColor.takeIf { it != 0 }?.let { editText.setTextColor(textColor) }
    }

    private fun setFontAttribute(editText: EditText) {
        val font = getFontAttribute()
        font?.let { editText.typeface = font }
    }

    private fun setBackgroundAttribute(editText: EditText) {
        val background = getBackgroundAttribute()
        background?.let {
            editText.setPadding(0, 0, 0, 0)
            editText.background = background
        }
    }

    private fun getPaddingBottomAttribute(defaultValue: Int) =
        styledAttributes.getDimension(
            R.styleable.AccessCheckoutEditText_android_paddingBottom,
            defaultValue.toFloat()
        ).toInt()

    private fun getPaddingRightAttribute(defaultValue: Int) =
        styledAttributes.getDimension(
            R.styleable.AccessCheckoutEditText_android_paddingRight,
            defaultValue.toFloat()
        ).toInt()

    private fun getPaddingTopAttribute(defaultValue: Int) =
        styledAttributes.getDimension(
            R.styleable.AccessCheckoutEditText_android_paddingTop,
            defaultValue.toFloat()
        ).toInt()

    private fun getPaddingLeftAttribute(defaultValue: Int) =
        styledAttributes.getDimension(
            R.styleable.AccessCheckoutEditText_android_paddingLeft,
            defaultValue.toFloat()
        ).toInt()

    private fun getPaddingStartAttribute(defaultValue: Int) =
        styledAttributes.getDimension(
            R.styleable.AccessCheckoutEditText_android_paddingStart,
            defaultValue.toFloat()
        ).toInt()

    private fun getPaddingEndAttribute(defaultValue: Int) =
        styledAttributes.getDimension(
            R.styleable.AccessCheckoutEditText_android_paddingEnd,
            defaultValue.toFloat()
        ).toInt()

    private fun getPaddingAttribute(defaultValue: Int) =
        styledAttributes.getDimension(
            R.styleable.AccessCheckoutEditText_android_padding,
            defaultValue.toFloat()
        ).toInt()

    private fun getHintAttribute() =
        styledAttributes.getString(R.styleable.AccessCheckoutEditText_android_hint)

    private fun getTextSizeAttribute() =
        styledAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_textSize, 0F)

    private fun getCursorVisibleAttribute() =
        styledAttributes.getBoolean(R.styleable.AccessCheckoutEditText_android_cursorVisible, true)

    private fun getImeOptionsAttribute() =
        styledAttributes.getInt(R.styleable.AccessCheckoutEditText_android_imeOptions, 0)

    private fun getHintTextColourAttribute() =
        styledAttributes.getColor(R.styleable.AccessCheckoutEditText_android_textColorHint, 0)

    private fun getTextColorAttribute() =
        styledAttributes.getColor(R.styleable.AccessCheckoutEditText_android_textColor, 0)

    private fun getFontAttribute() =
        styledAttributes.getFont(R.styleable.AccessCheckoutEditText_android_font)

    private fun getBackgroundAttribute() =
        styledAttributes.getDrawable(R.styleable.AccessCheckoutEditText_android_background)
}

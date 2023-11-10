package com.worldpay.access.checkout.ui

import android.content.res.TypedArray
import android.widget.EditText
import com.worldpay.access.checkout.R

internal class AttributeValues(
    private val styledAttributes: TypedArray
) {

    internal fun setAttributesOnEditText(
        editText: EditText,
        accessCheckoutEditText: AccessCheckoutEditText
    ) {
        setTextColourAttribute(editText)

        setHintAttribute(editText)

        setHintTextColourAttribute(editText)

        setImeOptionsAttribute(editText)

        setCursorVisibleAttribute(editText)

        setTextSizeAttribute(editText)

        setPaddingAttribute(editText, accessCheckoutEditText)

        setFontAttribute(editText)

        setBackgroundAttribute(editText)
    }

    private fun setPaddingAttribute(
        editText: EditText,
        accessCheckoutEditText: AccessCheckoutEditText
    ) {
        val padding = getPaddingAttribute().toInt()
        var left = getPaddingLeftAttribute().toInt()
        var top = getPaddingTopAttribute().toInt()
        var right = getPaddingRightAttribute().toInt()
        var bottom = getPaddingBottomAttribute().toInt()
        var start = getPaddingStartAttribute().toInt()
        var end = getPaddingEndAttribute().toInt()

        left.takeIf { it == 0 }?.let { left = editText.paddingLeft }
        top.takeIf { it == 0 }?.let { top = editText.paddingTop }
        right.takeIf { it == 0 }?.let { right = editText.paddingRight }
        bottom.takeIf { it == 0 }?.let { bottom = editText.paddingBottom }
        start.takeIf { it == 0 }?.let { start = editText.paddingStart }
        end.takeIf { it == 0 }?.let { end = editText.paddingEnd }

        if (padding != 0) {
            left = padding
            top = padding
            right = padding
            bottom = padding
        }

        if (start == 0 && end == 0) {
            accessCheckoutEditText.setPadding(left, top, right, bottom)
        } else {
            accessCheckoutEditText.setPaddingRelative(start, top, end, bottom)
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
        editText?.background = background
    }

    private fun getPaddingBottomAttribute() =
        styledAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_paddingBottom, 0.0F)

    private fun getPaddingRightAttribute() =
        styledAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_paddingRight, 0.0F)

    private fun getPaddingTopAttribute() =
        styledAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_paddingTop, 0.0F)

    private fun getPaddingLeftAttribute() =
        styledAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_paddingLeft, 0.0F)

    private fun getPaddingStartAttribute() =
        styledAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_paddingStart, 0.0F)

    private fun getPaddingEndAttribute() =
        styledAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_paddingEnd, 0.0F)

    private fun getPaddingAttribute() =
        styledAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_padding, 0.0F)

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

    private fun getFontAttribute() = styledAttributes.getFont(R.styleable.AccessCheckoutEditText_android_font)

    private fun getBackgroundAttribute() = styledAttributes.getDrawable(R.styleable.AccessCheckoutEditText_android_background)
}

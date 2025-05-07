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

        setAutofillAttribute(editText)

        setHintTextColourAttribute(editText)

        setImeOptionsAttribute(editText)

        setCursorVisibleAttribute(editText)

        setTextSizeAttribute(editText)

        setBackgroundAttribute(editText)

        setPaddingAttribute(editText)

        setFontAttribute(editText)

        setEnabledAttribute(editText)
    }

    private fun setPaddingAttribute(editText: EditText) {
        val padding: Int? = getPaddingAttribute()
        if (padding != null) {
            editText.setPadding(padding, padding, padding, padding)
            return
        }

        val left = getPaddingLeftAttribute()
        val top = getPaddingTopAttribute()
        val right = getPaddingRightAttribute()
        val bottom = getPaddingBottomAttribute()
        val start = getPaddingStartAttribute()
        val end = getPaddingEndAttribute()

        if (start != null || end != null) {
            editText.setPaddingRelative(
                start ?: editText.paddingStart,
                top ?: editText.paddingTop,
                end ?: editText.paddingEnd,
                bottom ?: editText.paddingBottom
            )
        } else {
            editText.setPadding(
                left ?: editText.paddingLeft,
                top ?: editText.paddingTop,
                right ?: editText.paddingRight,
                bottom ?: editText.paddingBottom
            )
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

    private fun setAutofillAttribute(editText: EditText) {
        val autofill = getAutofillAttribute()
        autofill?.let {
            editText.setAutofillHints((it as CharSequence).toString())
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

    private fun setEnabledAttribute(editText: EditText) {
        val enabled = getEnabledAttribute()
        editText.isEnabled = enabled
    }

    private fun getPaddingBottomAttribute(): Int? =
        getDimensionAttribute(R.styleable.AccessCheckoutEditText_android_paddingBottom)

    private fun getPaddingRightAttribute(): Int? =
        getDimensionAttribute(R.styleable.AccessCheckoutEditText_android_paddingRight)

    private fun getPaddingTopAttribute(): Int? =
        getDimensionAttribute(R.styleable.AccessCheckoutEditText_android_paddingTop)

    private fun getPaddingLeftAttribute(): Int? =
        getDimensionAttribute(R.styleable.AccessCheckoutEditText_android_paddingLeft)

    private fun getPaddingStartAttribute(): Int? =
        getDimensionAttribute(R.styleable.AccessCheckoutEditText_android_paddingStart)

    private fun getPaddingEndAttribute(): Int? =
        getDimensionAttribute(R.styleable.AccessCheckoutEditText_android_paddingEnd)

    private fun getPaddingAttribute(): Int? =
        getDimensionAttribute(R.styleable.AccessCheckoutEditText_android_padding)

    private fun getDimensionAttribute(attributeId: Int): Int? {
        val attributeValue = styledAttributes.getDimension(attributeId, -1F)
        return if (attributeValue == -1F) {
            null
        } else attributeValue.toInt()
    }

    private fun getHintAttribute() =
        styledAttributes.getString(R.styleable.AccessCheckoutEditText_android_hint)

    private fun getAutofillAttribute() =
        styledAttributes.getString(R.styleable.AccessCheckoutEditText_android_autofillHints)

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

    private fun getEnabledAttribute() =
        styledAttributes.getBoolean(R.styleable.AccessCheckoutEditText_android_enabled, true)
}

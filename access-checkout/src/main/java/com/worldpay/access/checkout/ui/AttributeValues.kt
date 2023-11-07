package com.worldpay.access.checkout.ui

import android.content.res.TypedArray
import android.os.Build
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.worldpay.access.checkout.R

internal class AttributeValues(
    private val styledAttributes: TypedArray
) {

    @RequiresApi(Build.VERSION_CODES.O)
    internal fun setAttributesOnEditText(editText: EditText) {
        setTextColourAttribute(editText)

        setHintAttribute(editText)

        setEmsAttribute(editText)

        setHintTextColourAttribute(editText)

        setImeOptionsAttribute(editText)

        setCursorVisibleAttribute(editText)

        setTextScaleXAttribute(editText)

        setTextSizeAttribute(editText)

        setPaddingAttribute(editText)

        setFontAttribute(editText)
    }

    private fun setPaddingAttribute(
        editText: EditText
    ) {
        val padding = getPaddingAttribute().toInt()
        var paddingLeft = getPaddingLeftAttribute().toInt()
        var paddingTop = getPaddingTopAttribute().toInt()
        var paddingRight = getPaddingRightAttribute().toInt()
        var paddingBottom = getPaddingBottomAttribute().toInt()

        paddingLeft.takeIf { it == 0 }?.let { paddingLeft = editText.paddingLeft }
        paddingTop.takeIf { it == 0 }?.let { paddingTop = editText.paddingTop }
        paddingRight.takeIf { it == 0 }?.let { paddingRight = editText.paddingRight }
        paddingBottom.takeIf { it == 0 }?.let { paddingBottom = editText.paddingBottom }

        if (padding != 0) {
            paddingLeft = padding
            paddingTop = padding
            paddingRight = padding
            paddingBottom = padding
        }

        editText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
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

    private fun setTextScaleXAttribute(editText: EditText) {
        val textScaleX = getTextScaleXAttribute()
        textScaleX.takeIf { it != 0F }?.let { editText.textScaleX = textScaleX }
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

    private fun setEmsAttribute(editText: EditText) {
        val ems = getEmsAttribute()
        ems.takeIf { it != 0 }?.let { editText.setEms(ems) }
    }

    private fun setTextColourAttribute(editText: EditText) {
        val textColor = getTextColorAttribute()
        textColor.takeIf { it != 0 }?.let { editText.setTextColor(textColor) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setFontAttribute(editText: EditText) {
        val font = getFontAttribute()
        font?.let { editText.typeface = font }
    }

    private fun getPaddingBottomAttribute() =
        styledAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_paddingBottom, 0.0F)

    private fun getPaddingRightAttribute() =
        styledAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_paddingRight, 0.0F)

    private fun getPaddingTopAttribute() =
        styledAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_paddingTop, 0.0F)

    private fun getPaddingLeftAttribute() =
        styledAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_paddingLeft, 0.0F)

    private fun getPaddingAttribute() =
        styledAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_padding, 0.0F)

    private fun getHintAttribute() =
        styledAttributes.getString(R.styleable.AccessCheckoutEditText_android_hint)

    private fun getTextSizeAttribute() =
        styledAttributes.getDimension(R.styleable.AccessCheckoutEditText_android_textSize, 0F)

    private fun getTextScaleXAttribute() =
        styledAttributes.getFloat(R.styleable.AccessCheckoutEditText_android_textScaleX, 0F)

    private fun getCursorVisibleAttribute() =
        styledAttributes.getBoolean(R.styleable.AccessCheckoutEditText_android_cursorVisible, true)

    private fun getImeOptionsAttribute() =
        styledAttributes.getInt(R.styleable.AccessCheckoutEditText_android_imeOptions, 0)

    private fun getHintTextColourAttribute() =
        styledAttributes.getColor(R.styleable.AccessCheckoutEditText_android_textColorHint, 0)

    private fun getEmsAttribute() =
        styledAttributes.getInt(R.styleable.AccessCheckoutEditText_android_ems, 0)

    private fun getTextColorAttribute() =
        styledAttributes.getColor(R.styleable.AccessCheckoutEditText_android_textColor, 0)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getFontAttribute() = styledAttributes.getFont(R.styleable.AccessCheckoutEditText_android_font)
}

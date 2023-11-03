package com.worldpay.access.checkout.ui

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.EditText
import com.worldpay.access.checkout.R

internal class AttributeValues(
    private val context: Context,
    private val attrs: AttributeSet?,
    private val editText: EditText
) {

    internal fun setAttributes(): TypedArray {
        val styleAttributes: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.AccessCheckoutEditText, 0, 0)

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
        editText.isCursorVisible = cursorVisible
    }

    private fun setHintAttribute(hint: String?) {
        hint?.let {
            editText.setHint(it as CharSequence)
        }
    }

    private fun setTextScaleXAttribute(textScaleX: Float) {
        textScaleX.takeIf { it != 0F }?.let { editText.textScaleX = textScaleX }
    }

    private fun setTextSizeAttribute(textSize: Float) {
        textSize.takeIf { it != 0F }?.let { editText.textSize = textSize }
    }

    private fun setImeOptionsAttribute(imeOptions: Int) {
        imeOptions.takeIf { it != 0 }?.let { editText.imeOptions = imeOptions }
    }

    private fun setHintTextColourAttribute(hintTextColour: Int) {
        hintTextColour.takeIf { it != 0 }?.let { editText.setHintTextColor(hintTextColour) }
    }

    private fun setEmsAttribute(ems: Int) {
        ems.takeIf { it != 0 }?.let { editText.setEms(ems) }
    }

    private fun setTextColourAttribute(textColor: Int) {
        textColor.takeIf { it != 0 }?.let { editText.setTextColor(textColor) }
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

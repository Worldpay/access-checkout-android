package com.worldpay.access.checkout.views

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.res.ResourcesCompat.getColor
import android.text.InputFilter
import android.util.AttributeSet
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import com.worldpay.access.checkout.R

@SuppressLint("AppCompatCustomView")
open class CardCVVText : EditText, CardView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) :
            super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    init {
        this.onFocusChangeListener = onFocusChangeListener()
    }

    override var cardViewListener: CardViewListener? = null

    override fun isValid(valid: Boolean) {
        when (valid) {
            true -> setTextColor(getColor(context.resources, R.color.SUCCESS, context.theme))
            else -> setTextColor(getColor(context.resources, R.color.FAIL, context.theme))
        }
    }

    override fun applyLengthFilter(inputFilter: InputFilter) {
        this.filters += inputFilter
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        cardViewListener?.onUpdateCVV(text.toString())
    }

    override fun getInsertedText(): String = this.text.toString()

    internal fun onFocusChangeListener(): OnFocusChangeListener {
        return OnFocusChangeListener { _, focus ->
            if (!focus) {
                val cvv = getInsertedText()
                cardViewListener?.onEndUpdateCVV(cvv)
            }
        }
    }

}
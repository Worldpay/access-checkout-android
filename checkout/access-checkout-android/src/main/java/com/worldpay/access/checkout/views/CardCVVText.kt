package com.worldpay.access.checkout.views

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.res.ResourcesCompat.getColor
import android.text.InputFilter
import android.util.AttributeSet
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import com.worldpay.access.checkout.R

/**
 * Access Checkout's default implementation of a CVV field
 *
 * This class will handle the operations related to text changes and on focus changes, communicating those changes to the
 * required [CardViewListener], and receiving updates to change it's state through the [isValid] method
 */
@SuppressLint("AppCompatCustomView")
open class CardCVVText : EditText, CardTextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) :
            super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    init {
        this.onFocusChangeListener = onFocusChangeListener()
    }

    override var cardViewListener: CardViewListener? = null

    /**
     * Handles applying the state of the cvv text based on it's validity
     *
     * @param valid whether the cvv is currently valid
     */
    override fun isValid(valid: Boolean) {
        when (valid) {
            true -> setTextColor(getColor(context.resources, R.color.SUCCESS, context.theme))
            else -> setTextColor(getColor(context.resources, R.color.FAIL, context.theme))
        }
    }

    /**
     * Handles applying the length filter of the cvv text
     *
     * @param filter the length filter to apply to this text field
     */
    override fun applyLengthFilter(inputFilter: InputFilter) {
        this.filters += inputFilter
    }

    /**
     * Handles text changes from the cvv field. If a [CardViewListener] has been set, then it will notify that this
     * field has been updated
     *
     * @param text The text the [CardCVVText] is displaying
     * @param start The offset of the start of the range of the text that was
     * modified
     * @param lengthBefore The length of the former text that has been replaced
     * @param lengthAfter The length of the replacement modified text
     */
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
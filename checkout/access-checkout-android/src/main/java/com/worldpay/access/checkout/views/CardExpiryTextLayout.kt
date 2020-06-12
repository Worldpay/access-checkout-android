package com.worldpay.access.checkout.views

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import com.worldpay.access.checkout.R
import kotlinx.android.synthetic.main.date_view_layout.view.*

/**
 * Access Checkout's default implementation of a expiry date field
 *
 * This class will handle the operations related to text changes and on focus changes, communicating those changes to the
 * required [CardViewListener], and receiving updates to change it's state through the [isValid] method
 */
@Deprecated(message = "legacy")
open class CardExpiryTextLayout @JvmOverloads constructor(
    context: Context,
    attrSet: AttributeSet? = null,
    defStyles: Int = 0
) :
    LinearLayout(
        context,
        attrSet,
        defStyles
    ), CardDateView {


    override var cardViewListener: CardViewListener? = null

    /**
     * The month field [EditText] property
     */
    @JvmField
    val monthEditText: EditText

    /**
     * The year field [EditText] property
     */
    @JvmField
    val yearEditText: EditText

    init {
        orientation = HORIZONTAL
        val rootView = LayoutInflater.from(context).inflate(R.layout.date_view_layout, this, true)
        monthEditText = rootView.month_edit_text
        yearEditText = rootView.year_edit_text
        setContentListeners()
    }

    override fun getInsertedMonth() = monthEditText.text.toString()

    override fun getInsertedYear() = yearEditText.text.toString()

    override fun getMonth(): Int = Integer.parseInt(getInsertedMonth())

    override fun getYear(): Int = Integer.parseInt("20${getInsertedYear()}")

    override fun isValid(valid: Boolean) {
        setLayout(valid, monthEditText)
        setLayout(valid, yearEditText)
    }

    override fun applyLengthFilter(inputFilter: InputFilter) {
        monthEditText.filters += inputFilter
        yearEditText.filters += inputFilter
    }

    private fun setContentListeners() {
        monthEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cardViewListener?.onUpdateDate(s.toString(), null)
            }
        })

        monthEditText.onFocusChangeListener = monthEditTextOnFocusChange()

        yearEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cardViewListener?.onUpdateDate(null, s.toString())
            }
        })
        yearEditText.onFocusChangeListener = yearEditTextOnFocusChange()
    }

    internal fun monthEditTextOnFocusChange(): OnFocusChangeListener {
        return OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && !yearEditText.hasFocus()) {
                cardViewListener?.onEndUpdateDate(getInsertedMonth(), null)
            }
        }

    }

    internal fun yearEditTextOnFocusChange(): OnFocusChangeListener {
        return OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && !monthEditText.hasFocus()) {
                cardViewListener?.onEndUpdateDate(null, getInsertedYear())
            }
        }

    }

    private fun setLayout(valid: Boolean, targetView: EditText) {
        when (valid) {
            true -> targetView.setTextColor(
                ResourcesCompat.getColor(
                    this.context.resources,
                    R.color.SUCCESS,
                    this.context.theme
                )
            )
            else -> targetView.setTextColor(
                ResourcesCompat.getColor(
                    this.context.resources,
                    R.color.FAIL,
                    this.context.theme
                )
            )
        }
    }
}

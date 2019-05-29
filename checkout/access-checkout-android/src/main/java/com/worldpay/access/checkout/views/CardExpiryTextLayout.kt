package com.worldpay.access.checkout.views

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import com.worldpay.access.checkout.R
import kotlinx.android.synthetic.main.date_view_layout.view.*

/**
 * Access Checkout's default implementation of a expiry date field
 *
 * This class will handle the operations related to text changes and on focus changes, communicating those changes to the
 * required [CardViewListener], and receiving updates to change it's state through the [onValidationResult],
 * [onMonthValidationResult] and [onYearValidationResult] methods
 */
open class CardExpiryTextLayout @JvmOverloads constructor(
    context: Context,
    attrSet: AttributeSet? = null,
    defStyles: Int = 0
) :
    LinearLayout(
        context,
        attrSet,
        defStyles
    ), DateCardView {

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

    override fun getInsertedText(): String = getInsertedMonth() + "/" + getInsertedYear()

    override fun getInsertedMonth() = monthEditText.text.toString()

    override fun getInsertedYear() = yearEditText.text.toString()

    override fun getMonth(): Int = Integer.parseInt(getInsertedMonth())

    override fun getYear(): Int = Integer.parseInt("20${getInsertedYear()}")

    /**
     * Handles applying the length filter of the month field
     *
     * @param filter the length filter to apply to the month field
     */
    fun setMonthLengthFilter(filter: InputFilter?) {
        monthEditText.filters += filter
    }

    /**
     * Handles applying the length filter of the year field
     *
     * @param filter the length filter to apply to the year field
     */
    fun setYearLengthFilter(filter: InputFilter?) {
        yearEditText.filters += filter
    }

    /**
     * Handles applying the state of the expiry date fields based on it's overall validity
     *
     * @param valid whether the full date field is valid
     */
    fun onValidationResult(valid: Boolean) {
        onMonthValidationResult(valid)
        onYearValidationResult(valid)
    }

    /**
     * Handles applying the state of the expiry month field based on it's validity
     *
     * @param valid whether the month field is valid
     */
    fun onMonthValidationResult(valid: Boolean) = setLayout(valid, monthEditText)

    /**
     * Handles applying the state of the expiry year field based on it's validity
     *
     * @param valid whether the year field is valid
     */
    fun onYearValidationResult(valid: Boolean) = setLayout(valid, yearEditText)

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
            if (!hasFocus) {
                cardViewListener?.onEndUpdateDate(getInsertedMonth(), null)
            }
        }

    }

    internal fun yearEditTextOnFocusChange(): OnFocusChangeListener {
        return OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
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

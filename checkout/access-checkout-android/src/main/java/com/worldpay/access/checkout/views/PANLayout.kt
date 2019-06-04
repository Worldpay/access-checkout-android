package com.worldpay.access.checkout.views

import android.content.Context
import android.support.v4.content.res.ResourcesCompat.getColor
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.worldpay.access.checkout.R
import kotlinx.android.synthetic.main.card_number_view_layout.view.*


open class PANLayout @JvmOverloads constructor(
    context: Context,
    attrSet: AttributeSet? = null,
    defStyles: Int = 0
) :
    LinearLayout(
        context,
        attrSet,
        defStyles
    ),
    TextWatcher, CardTextView {

    @JvmField
    val mEditText: EditText

    internal var mImageView: ImageView; private set

    override var cardViewListener: CardViewListener? = null

    init {
        orientation = HORIZONTAL
        val rootView = LayoutInflater.from(context).inflate(R.layout.card_number_view_layout, this, true)
        mEditText = rootView.card_number_edit_text
        mImageView = rootView.logo_view
        mEditText.addTextChangedListener(this)
        mEditText.onFocusChangeListener = onFocusChangeListener()
    }

    override fun onFinishInflate() {
        mImageView.setTag(CARD_TAG, resources.getResourceEntryName(R.drawable.card_unknown))
        super.onFinishInflate()
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        cardViewListener?.onUpdatePAN(s.toString())
    }

    override fun getInsertedText(): String = mEditText.text.toString()

    override fun isValid(valid: Boolean) {
        when (valid) {
            true -> mEditText.setTextColor(getColor(this.context.resources, R.color.SUCCESS, this.context.theme))
            else -> mEditText.setTextColor(getColor(this.context.resources, R.color.FAIL, this.context.theme))
        }
    }

    override fun applyLengthFilter(inputFilter: InputFilter) {
        mEditText.filters += inputFilter
    }

    fun applyCardLogo(logoName: String) {
        val resID = context.resIdByName(logoName, "drawable")
        mImageView.setTag(CARD_TAG, logoName)
        mImageView.setImageResource(resID)
    }


    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    internal fun onFocusChangeListener(): OnFocusChangeListener {
        return OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val pan = getInsertedText()
                cardViewListener?.onEndUpdatePAN(pan)
            }
        }
    }

    companion object{
        @JvmStatic
        val CARD_TAG = R.integer.card_tag
    }
}


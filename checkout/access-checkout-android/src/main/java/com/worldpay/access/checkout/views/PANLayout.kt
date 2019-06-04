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

/**
 * Access Checkout's default implementation of a pan field
 *
 * This class will handle the operations related to text changes and on focus changes, communicating those changes to the
 * required [CardViewListener], and receiving updates to change it's state through the [onValidationResult] method
 */
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
    TextWatcher, CardView {

    /**
     * The pan field [EditText] property
     */
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

    /**
     * Handles text changes from the cvv field. If a [CardViewListener] has been set, then it will notify that this
     * field has been updated
     *
     * @param s The text the [PANLayout] is displaying
     * @param start The offset of the start of the range of the text that was
     * modified
     * @param before The length of the former text that has been replaced
     * @param count The length of the replacement modified text
     */
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        cardViewListener?.onUpdatePAN(s.toString())
    }

    override fun getInsertedText(): String = mEditText.text.toString()

    /**
     * Handles applying the length filter of the pan field
     *
     * @param filter the length filter to apply to the pan field
     */
    fun setLengthFilter(filter: InputFilter?) {
        mEditText.filters += filter
    }

    /**
     * Handles applying the state of the pan field based on it's validity, and a brand icon that is associated with the
     * card input
     *
     * @param valid whether the pan field is valid
     * @param logoResName the name of the resource icon to load to apply to the image view
     */
    fun onValidationResult(valid: Boolean, logoResName: String) {
        when (valid) {
            true -> mEditText.setTextColor(getColor(this.context.resources, R.color.SUCCESS, this.context.theme))
            else -> mEditText.setTextColor(getColor(this.context.resources, R.color.FAIL, this.context.theme))
        }

        val resID = context.resIdByName(logoResName, "drawable")
        mImageView.setTag(CARD_TAG, logoResName)
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


package com.worldpay.access.checkout.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.worldpay.access.checkout.R

// class is final and public by default
class AccessEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {
    var mCustomHint: String? = null
    private lateinit var editText:EditText

    // with the internal access modifier, this property is internal to the Access Checkout SDK JAR file
    // (or access-checkout Gradle project when adding the dependency as a project dependency)
    // and cannot be accessed outside of that module, e.g. cannot be accessed by a merchant's app
    internal val text: String get() = editText.text.toString()

    private fun createEditText(): View {
        editText = EditText(context)
        editText.setHint(mCustomHint)
        return editText
    }

    init {
        orientation = VERTICAL
        context.withStyledAttributes(attrs, R.styleable.AccessEditText) {
            mCustomHint = getString(R.styleable.AccessEditText_customHint)
        }

        addView(createEditText())
    }


}
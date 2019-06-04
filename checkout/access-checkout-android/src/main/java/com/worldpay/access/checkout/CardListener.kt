package com.worldpay.access.checkout

import android.text.InputFilter
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.validation.ValidationResult
import com.worldpay.access.checkout.views.CardView

interface CardListener {

  fun onUpdate(cardView: CardView, valid: Boolean)
  fun onUpdateLengthFilter(cardView: CardView, inputFilter: InputFilter)
  fun onUpdateCardBrand(cardBrand: CardBrand?)

}

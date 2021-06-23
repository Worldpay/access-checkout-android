package com.worldpay.access.checkout.sample.ui

import android.app.Activity
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import com.worldpay.access.checkout.sample.R

class SubmitButton(private val activity: Activity, private val id: Int) {

    fun enable() {
        if (ProgressBar(activity).isLoading()) {
            return
        }
        val submitBtn = activity.findViewById<Button>(id)
        val submitBtnColor = ResourcesCompat.getColor(activity.resources, R.color.colorPrimary, null)

        submitBtn.isEnabled = true
        submitBtn.setBackgroundColor(submitBtnColor)
    }

    fun disable() {
        val submitBtn = activity.findViewById<Button>(id)
        val submitBtnColor = ResourcesCompat.getColor(activity.resources, android.R.color.darker_gray, null)

        submitBtn.isEnabled = false
        submitBtn.setBackgroundColor(submitBtnColor)
    }

    fun get(): Button = activity.findViewById(id)
}

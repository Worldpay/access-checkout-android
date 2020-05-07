package com.worldpay.access.checkout.sample.ui

import android.app.Activity
import android.view.View
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.worldpay.access.checkout.sample.R

class ProgressBar(private val activity: Activity) {

    fun beginLoading() {
        toggleProgressBar(false)
    }

    fun stopLoading() {
        toggleProgressBar(true)
    }

    fun isLoading(): Boolean {
        return activity.findViewById<ProgressBar>(R.id.loading_bar).isVisible
    }

    private fun toggleProgressBar(enableFields: Boolean) {
        val progressBar = activity.findViewById<ProgressBar>(R.id.loading_bar)
        val rootLayout = activity.findViewById<ConstraintLayout>(R.id.root_layout)

        if (!enableFields) {
            rootLayout.alpha = 0.5f
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.INVISIBLE
            rootLayout.alpha = 1.0f
        }
    }

}
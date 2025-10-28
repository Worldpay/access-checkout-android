package com.worldpay.access.checkout.sample.ui

import android.app.Activity
import android.view.View
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.worldpay.access.checkout.sample.R

class ProgressBar(private val activity: Activity) {

    fun beginLoading() {
        toggleProgressBar(true)
    }

    fun stopLoading() {
        toggleProgressBar(false)
    }

    fun isLoading(): Boolean {
        return activity.findViewById<ProgressBar>(R.id.loading_bar).isVisible
    }

    private fun toggleProgressBar(showProgress: Boolean) {
        val progressBar = activity.findViewById<ProgressBar>(R.id.loading_bar)
        val rootLayout = activity.findViewById<ConstraintLayout>(R.id.root_layout)

        if (progressBar.isVisible != showProgress) {
            progressBar.visibility = if (showProgress) View.VISIBLE else View.INVISIBLE
            rootLayout.alpha = if (showProgress) 0.5f else 1.0f
        }
    }
}

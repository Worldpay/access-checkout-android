package com.worldpay.access.checkout.api.configuration

import android.os.AsyncTask
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.model.CardConfiguration

internal class CardConfigurationAsyncTask(private val callback: Callback<CardConfiguration>): AsyncTask<String, Void, CardConfiguration>() {

    override fun doInBackground(vararg params: String?): CardConfiguration {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
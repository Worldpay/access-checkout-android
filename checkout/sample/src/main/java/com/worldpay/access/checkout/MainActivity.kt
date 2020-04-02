package com.worldpay.access.checkout

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.worldpay.access.checkout.api.configuration.CardConfigurationFactory
import com.worldpay.access.checkout.card.CardListenerImpl
import com.worldpay.access.checkout.card.SessionResponseListenerImpl
import com.worldpay.access.checkout.validation.AccessCheckoutCardValidator
import com.worldpay.access.checkout.views.CardCVVText
import com.worldpay.access.checkout.views.CardExpiryTextLayout
import com.worldpay.access.checkout.views.PANLayout
import kotlinx.android.synthetic.main.fragment_card_flow.*

class MainActivity : AppCompatActivity() {

    private lateinit var card: Card
    private lateinit var panView: PANLayout
    private lateinit var cvvText: CardCVVText
    private lateinit var dateText: CardExpiryTextLayout

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        val navController = getNavController()
        val navView: NavigationView = findViewById(R.id.nav_view)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        setSupportActionBar(toolbar)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_card_flow, R.id.nav_cvv_flow), drawerLayout
        )

        toolbar.setupWithNavController(navController, appBarConfiguration)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()

        progressBar = ProgressBar(this)

        panView = findViewById(R.id.card_flow_text_pan)
        cvvText = findViewById(R.id.card_flow_text_cvv)
        dateText = findViewById(R.id.card_flow_text_exp)

        card = AccessCheckoutCard(panView, cvvText, dateText)
        card.cardListener = CardListenerImpl(this, card)
        card.cardValidator = AccessCheckoutCardValidator()

        CardConfigurationFactory.getRemoteCardConfiguration(card, getBaseUrl())

        panView.cardViewListener = card
        cvvText.cardViewListener = card
        dateText.cardViewListener = card

        val accessCheckoutClient = AccessCheckoutClient.init(
            getBaseUrl(),
            getMerchantID(),
            SessionResponseListenerImpl(this, progressBar),
            applicationContext,
            this
        )

        card_flow_btn_submit.setOnClickListener {
            val pan = panView.getInsertedText()
            val month = dateText.getMonth()
            val year = dateText.getYear()
            val cvv = cvvText.getInsertedText()
            accessCheckoutClient.generateSessionState(pan, month, year, cvv)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("loading", progressBar.isLoading())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        if (savedInstanceState.getBoolean("loading")) {
            progressBar.beginLoading()
        } else {
            progressBar.stopLoading()
        }

        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun getNavController(): NavController {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    private fun getMerchantID() = BuildConfig.MERCHANT_ID

    private fun getBaseUrl() = getString(R.string.endpoint)

}
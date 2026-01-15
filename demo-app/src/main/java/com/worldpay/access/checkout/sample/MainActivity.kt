package com.worldpay.access.checkout.sample

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.worldpay.access.checkout.sample.ui.ProgressBar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_main)
        val navController = getNavController()

        setSupportActionBar(toolbar)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_card_flow,
                R.id.nav_restricted_card_flow,
                R.id.nav_cvc_flow
            ),
        )

        toolbar.setupWithNavController(navController, appBarConfiguration)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val bottomNav = findViewById<BottomNavigationView>(R.id.nav_view)

        bottomNav.setupWithNavController(navController)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        return getNavController().navigateUp() || super.onSupportNavigateUp()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val loading = ProgressBar(this).isLoading()
        outState.putBoolean("loading", loading)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        if (savedInstanceState.getBoolean("loading")) {
            ProgressBar(this).beginLoading()
        } else {
            ProgressBar(this).stopLoading()
        }

        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun getNavController(): NavController {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }
}

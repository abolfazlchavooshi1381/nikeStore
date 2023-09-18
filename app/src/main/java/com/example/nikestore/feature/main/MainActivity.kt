package com.example.nikestore.feature.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.example.nikestore.R
import com.example.nikestore.common.NetworkUtils
import com.example.nikestore.common.NikeActivity
import com.example.nikestore.common.convertDpToPixel
import com.example.nikestore.common.setupWithNavController
import com.example.nikestore.data.CartItemCount
import com.example.nikestore.databinding.ActivityMainBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.MaterialColors
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : NikeActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentNavController: LiveData<NavController>? = null
    private val viewModel: MainViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            this.setupBottomNavigationBar()
        }
        NetworkUtils.registerNetworkChangeListener(this, this)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        val config = Configuration(newBase?.resources?.configuration)
        config.fontScale = 1f
        applyOverrideConfiguration(config)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationMain)

        val navGraphIds = listOf(R.navigation.home, R.navigation.cart, R.navigation.profile)

        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkUtils.unregisterNetworkChangeListener(this)
    }

    override fun onNetworkChanged(isConnected: Boolean) {
        this.loadingDialog.dismiss()
        this.viewModel.getCartItemsCount()
    }

    @SuppressLint("ResourceType")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCartItemsCountChangeEvent(cartItemCount: CartItemCount) {
        val badge = this.binding.bottomNavigationMain.getOrCreateBadge(R.id.cart)
        badge.badgeGravity = BadgeDrawable.BOTTOM_START
        badge.backgroundColor =
            MaterialColors.getColor(this.binding.bottomNavigationMain, org.koin.android.R.attr.colorPrimary)
        badge.number = cartItemCount.count
        badge.verticalOffset = convertDpToPixel(10f, this).toInt()
        badge.isVisible = cartItemCount.count > 0
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCartItemsCount()
    }
}
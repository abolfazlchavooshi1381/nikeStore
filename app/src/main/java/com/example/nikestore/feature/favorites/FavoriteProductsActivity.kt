package com.example.nikestore.feature.favorites

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nikestore.R
import com.example.nikestore.common.EXTRA_KEY_DATA
import com.example.nikestore.common.NikeActivity
import com.example.nikestore.data.Product
import com.example.nikestore.databinding.ActivityFavoriteProductsBinding
import com.example.nikestore.feature.product.ProductDetailActivity
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class FavoriteProductsActivity : NikeActivity(),
    FavoriteProductsAdapter.FavoriteProductEventListener {

    private lateinit var binding: ActivityFavoriteProductsBinding
    val viewModel: FavoriteProductsViewModel by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityFavoriteProductsBinding.inflate(this.layoutInflater)
        setContentView(binding.root)
        loadingDialog.isCancelable = false
        loadingDialog.show(supportFragmentManager, null)

        this.binding.helpBtn.setOnClickListener {
            showToast(this@FavoriteProductsActivity, getString(R.string.favorites_help_message), Toast.LENGTH_LONG)
        }

        viewModel.productsLiveData.observe(this) {
            if (it.isNotEmpty()) {
                this.binding.favoriteProductsRv.layoutManager =
                    LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                this.binding.favoriteProductsRv.adapter =
                    FavoriteProductsAdapter(it as MutableList<Product>, this, get())
                loadingDialog.dismiss()
            } else {
                setContentView(R.layout.view_default_empty_state);
                loadingDialog.dismiss()
            }
        }
        this.binding.favoriteProductsToolbar.onBackButtonClickListener = View.OnClickListener {
            finish()
        }
    }

    override fun onClick(product: Product) {
        startActivity(Intent(this, ProductDetailActivity::class.java).apply {
            putExtra(EXTRA_KEY_DATA, product)
        })
    }

    override fun onLongClick(product: Product) {
        viewModel.removeFromFavorites(product)
    }
}
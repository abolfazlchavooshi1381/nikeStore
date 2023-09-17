package com.example.nikestore.feature.common.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nikestore.feature.common.LoadingDialog
import com.example.nikestore.R
import com.example.nikestore.common.EXTRA_KEY_DATA
import com.example.nikestore.common.NikeActivity
import com.example.nikestore.common.convertPersianNumbersToEnglish
import com.example.nikestore.data.Product
import com.example.nikestore.databinding.ActivitySearchBinding
import com.example.nikestore.databinding.ActivityStartBinding
import com.example.nikestore.feature.list.ProductListAdapter
import com.example.nikestore.feature.list.VIEW_TYPE_SMALL
import com.example.nikestore.feature.product.ProductDetailActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class SearchActivity : NikeActivity(), ProductListAdapter.ProductEventListener {

    private lateinit var binding: ActivitySearchBinding
    val viewModel: ProductSearchViewModel by viewModel()
    val productListAdapter: ProductListAdapter by inject { parametersOf(VIEW_TYPE_SMALL) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivitySearchBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        loadingDialog.isCancelable = false
        loadingDialog.show(supportFragmentManager, null)

        val gridLayoutManager = GridLayoutManager(this, 2)
        this.binding.productsRv.layoutManager = gridLayoutManager
        this.binding.productsRv.adapter = productListAdapter
        productListAdapter.productEventListener = this
        loadingDialog.dismiss()

        viewModel.productsLiveData.observe(this) {
            Timber.i(it.toString())
            productListAdapter.products = it as ArrayList<Product>
        }

        viewModel.productsLiveData.observe(this) {
            Timber.i(it.toString())
            productListAdapter.main = it as ArrayList<Product>
        }

        this.binding.toolbarView.onBackButtonClickListener = View.OnClickListener {
            finish()
        }

        this.binding.searchBtn.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                productListAdapter.search(charSequence.toString().convertPersianNumbersToEnglish())
            }

            override fun afterTextChanged(editable: Editable) {}
        })

    }

    override fun onProductClick(product: Product) {
        startActivity(Intent(this, ProductDetailActivity::class.java).apply {
            putExtra(EXTRA_KEY_DATA, product)
        })
    }

    override fun onFavoriteBtnClick(product: Product) {
        viewModel.addProductToFavorites(product)
    }
}
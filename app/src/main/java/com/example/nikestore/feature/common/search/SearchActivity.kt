package com.example.nikestore.feature.common.search

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nikestore.common.EXTRA_KEY_DATA
import com.example.nikestore.common.NetworkUtils
import com.example.nikestore.common.NikeActivity
import com.example.nikestore.common.REQUEST_CODE_SPEECH_INPUT
import com.example.nikestore.common.convertPersianNumbersToEnglish
import com.example.nikestore.common.onSpeechButtonClicked
import com.example.nikestore.data.Product
import com.example.nikestore.databinding.ActivitySearchBinding
import com.example.nikestore.feature.list.ProductListAdapter
import com.example.nikestore.feature.list.VIEW_TYPE_SMALL
import com.example.nikestore.feature.product.ProductDetailActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.util.Objects

class SearchActivity : NikeActivity(), ProductListAdapter.ProductEventListener {

    private lateinit var binding: ActivitySearchBinding
    val viewModel: ProductSearchViewModel by viewModel()
    val productListAdapter: ProductListAdapter by inject { parametersOf(VIEW_TYPE_SMALL) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivitySearchBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        NetworkUtils.registerNetworkChangeListener(this, this)

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

        this.binding.toolbarView.onBackButtonClickListener = View.OnClickListener {
            finish()
        }

        this.binding.voiceSearchIv.setOnClickListener {
            try {
                startActivityForResult(onSpeechButtonClicked(), REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                Toast
                    .makeText(
                        this, " " + e.message,
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
        }

        this.binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                productListAdapter.search(charSequence.toString().convertPersianNumbersToEnglish().lowercase())
            }

            override fun afterTextChanged(editable: Editable) {}
        })

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                this.binding.searchEt.setText(
                    Objects.requireNonNull(result)?.get(0)
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkUtils.unregisterNetworkChangeListener(this)
    }

    override fun onNetworkChanged(isConnected: Boolean) {
        if (isConnected) {
            loadingDialog.dismiss()
            viewModel.getProducts()
        }
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
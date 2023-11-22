package com.example.nikestore.feature.list

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nikestore.R
import com.example.nikestore.common.EXTRA_KEY_DATA
import com.example.nikestore.common.NikeActivity
import com.example.nikestore.data.Product
import com.example.nikestore.databinding.ActivityProductListBinding
import com.example.nikestore.feature.cart.CartFragment
import com.example.nikestore.feature.main.MainViewModel
import com.example.nikestore.feature.product.ProductDetailActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.util.Locale

class ProductListActivity : NikeActivity(), ProductListAdapter.ProductEventListener {

    private lateinit var binding: ActivityProductListBinding
    val viewModel: ProductListViewModel by viewModel {
        parametersOf(
            intent.extras!!.getInt(
                EXTRA_KEY_DATA
            )
        )
    }
    private val mainViewModel: MainViewModel by viewModel()
    private val productListAdapter: ProductListAdapter by inject { parametersOf(VIEW_TYPE_SMALL) }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityProductListBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        loadingDialog.isCancelable = false
        loadingDialog.show(supportFragmentManager, null)

        val gridLayoutManager = GridLayoutManager(this, 2)
        this.binding.productsRv.layoutManager = gridLayoutManager
        this.binding.productsRv.adapter = productListAdapter
        loadingDialog.dismiss()
        productListAdapter.productEventListener = this
        this.binding.viewTypeChangerBtn.setOnClickListener {
            if (productListAdapter.viewType == VIEW_TYPE_SMALL) {
                this.binding.viewTypeChangerBtn.setImageResource(R.drawable.ic_view_type_large)
                productListAdapter.viewType = VIEW_TYPE_LARGE
                gridLayoutManager.spanCount = 1
                productListAdapter.notifyDataSetChanged()

            } else {
                this.binding.viewTypeChangerBtn.setImageResource(R.drawable.ic_grid)
                productListAdapter.viewType = VIEW_TYPE_SMALL
                gridLayoutManager.spanCount = 2
                productListAdapter.notifyDataSetChanged()
            }
        }

        viewModel.selectedSortTitleLiveData.observe(this) {
            this.binding.selectedSortTitleTv.text = getString(it)
        }

        viewModel.productsLiveData.observe(this) {
            Timber.i(it.toString())
            productListAdapter.products = it as ArrayList<Product>
        }

        this.binding.toolbarView.onBackButtonClickListener = View.OnClickListener {
            finish()
        }



        this.binding.sortBtn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(this, R.style.AlertDialogCustom)
                .setSingleChoiceItems(
                    R.array.sortTitlesArray,
                    viewModel.sort
                ) { dialog, selectedSortIndex ->
                    dialog.dismiss()
                    viewModel.onSelectedSortChangedByUser(selectedSortIndex)
                }.setTitle(getString(R.string.sort))
            dialog.show().window?.setBackgroundDrawableResource(R.drawable.background_dialog_fragment)
        }

        this.binding.cartBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content, CartFragment()).commit()
        }

        this.mainViewModel.cartItemsCountLiveData.observe(this) {
            val count = it.count
            if (count > 0) {

                this.binding.badgeTv.text = String.format(Locale.ENGLISH, count.toString())
                this.binding.badgeTv.visibility = View.VISIBLE
            } else {
                this.binding.badgeTv.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.getCartItemsCount()
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
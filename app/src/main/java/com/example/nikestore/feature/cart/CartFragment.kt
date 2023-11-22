package com.example.nikestore.feature.cart

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nikestore.R
import com.example.nikestore.common.EXTRA_KEY_DATA
import com.example.nikestore.common.NikeCompletableObserver
import com.example.nikestore.common.NikeFragment
import com.example.nikestore.data.CartItem
import com.example.nikestore.databinding.FragmentCartBinding
import com.example.nikestore.feature.auth.AuthActivity
import com.example.nikestore.feature.product.ProductDetailActivity
import com.example.nikestore.feature.shipping.ShippingActivity
import com.example.nikestore.services.ImageLoadingService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_cart_empty_state.view.emptyStateCtaBtn
import kotlinx.android.synthetic.main.view_cart_empty_state.view.emptyStateMessageTv
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class CartFragment: NikeFragment(), CartItemAdapter.CartItemViewCallbacks {

    private lateinit var binding: FragmentCartBinding
    private val viewModel: CartViewModel by viewModel()
    private var cartItemAdapter: CartItemAdapter? = null
    private val imageLoadingService: ImageLoadingService by inject()
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        loadingDialog.isCancelable = false
        fragmentManager?.let { loadingDialog.show(it, null) }

        this.binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_cart, container, false
        )
        this.binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.cartItemsLiveData.observe(viewLifecycleOwner) {
            Timber.i(it.toString())
            this.binding.cartItemsRv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            this.cartItemAdapter = CartItemAdapter(it as MutableList<CartItem>, imageLoadingService, this)
            this.binding.cartItemsRv.adapter = cartItemAdapter
            loadingDialog.dismiss()
        }

        viewModel.purchaseDetailLiveData.observe(viewLifecycleOwner) {
            Timber.i(it.toString())
            cartItemAdapter?.let { adapter ->
                adapter.purchaseDetail = it
                adapter.notifyItemChanged(adapter.cartItems.size)
                loadingDialog.dismiss()
            }
        }

        viewModel.emptyStateLiveData.observe(viewLifecycleOwner) {
            if (it.mustShow) {

                val emptyState = showEmptyState(R.layout.view_cart_empty_state)

                emptyState?.let { view ->
                    loadingDialog.dismiss()
                    view.emptyStateMessageTv.text = getString(it.messageResId)
                    view.emptyStateCtaBtn.visibility =
                        if (it.mustShowCallToActionButton) View.VISIBLE else View.GONE
                    view.emptyStateCtaBtn.setOnClickListener {
                        startActivity(Intent(requireContext(), AuthActivity::class.java))
                    }
                }
            }
        }

        this.binding.payBtn.setOnClickListener {
            startActivity(Intent(requireContext(), ShippingActivity::class.java).apply {
                putExtra(EXTRA_KEY_DATA, viewModel.purchaseDetailLiveData.value)
            })
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }

    override fun onRemoveCartItemButtonClick(cartItem: CartItem) {
        viewModel.removeItemFromCart(cartItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : NikeCompletableObserver(compositeDisposable) {
                override fun onComplete() {
                    cartItemAdapter?.removeCartItem(cartItem)
                }
            })
    }

    override fun onIncreaseCartItemButtonClick(cartItem: CartItem) {
        viewModel.increaseCartItemCount(cartItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : NikeCompletableObserver(compositeDisposable) {
                override fun onComplete() {
                    cartItemAdapter?.increaseCount(cartItem)
                }
            })
    }

    override fun onDecreaseCartItemButtonClick(cartItem: CartItem) {
        viewModel.decreaseCartItemCount(cartItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : NikeCompletableObserver(compositeDisposable) {
                override fun onComplete() {
                    cartItemAdapter?.decreaseCount(cartItem)
                }
            })
    }

    override fun onProductImageClick(cartItem: CartItem) {
        startActivity(Intent(requireContext(), ProductDetailActivity::class.java).apply {
            putExtra(EXTRA_KEY_DATA, cartItem.product)
        })
    }

}
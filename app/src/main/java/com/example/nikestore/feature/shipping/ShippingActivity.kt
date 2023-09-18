package com.example.nikestore.feature.shipping

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.nikestore.R
import com.example.nikestore.common.EXTRA_KEY_DATA
import com.example.nikestore.common.EXTRA_KEY_ID
import com.example.nikestore.common.NetworkUtils
import com.example.nikestore.common.NikeActivity
import com.example.nikestore.common.NikeCompletableObserver
import com.example.nikestore.common.NikeSingleObserver
import com.example.nikestore.common.openUrlInCustomTab
import com.example.nikestore.data.CartItem
import com.example.nikestore.data.PurchaseDetail
import com.example.nikestore.data.SubmitOrderResult
import com.example.nikestore.databinding.ActivityShippingBinding
import com.example.nikestore.feature.cart.CartItemAdapter
import com.example.nikestore.feature.cart.CartViewModel
import com.example.nikestore.feature.checkout.CheckOutActivity
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ShippingActivity : NikeActivity(), IPurchaseRegistered {

    private lateinit var binding: ActivityShippingBinding
    private val shippingViewModel: ShippingViewModel by viewModel()
    private val cartViewModel: CartViewModel by viewModel()
    private var cartItemAdapter: CartItemAdapter? = null
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityShippingBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        NetworkUtils.registerNetworkChangeListener(this, this)

        loadingDialog.isCancelable = false
        loadingDialog.show(supportFragmentManager, null)

        val purchaseDetail = intent.getParcelableExtra<PurchaseDetail>(EXTRA_KEY_DATA)
            ?: throw IllegalStateException("purchase detail cannot be null")

        val viewHolder = CartItemAdapter.PurchaseDetailViewHolder(this.binding.purchaseDetailView)
        viewHolder.bind(
            purchaseDetail.totalPrice,
            purchaseDetail.shipping_cost,
            purchaseDetail.payable_price
        )

        loadingDialog.dismiss()

        this.binding.onlinePaymentBtn.setOnClickListener {
            if (this.checkInputValidation()) {
                shippingViewModel.submitOrder(
                    this.binding.firstNameEt.text.toString(),
                    this.binding.lastNameEt.text.toString(),
                    this.binding.postalCodeEt.text.toString(),
                    this.binding.phoneNumberEt.text.toString(),
                    this.binding.addressEt.text.toString(),
                    PAYMENT_METHOD_ONLINE
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : NikeSingleObserver<SubmitOrderResult>(compositeDisposable) {
                        override fun onSuccess(t: SubmitOrderResult) {
                            this@ShippingActivity.emptyShoppingCart()
                            openUrlInCustomTab(this@ShippingActivity, t.bank_gateway_url)
                            finish()
                        }
                    })
            }
        }
        this.binding.codBtn.setOnClickListener {
            if (this.checkInputValidation()) {
                shippingViewModel.submitOrder(
                    this.binding.firstNameEt.text.toString(),
                    this.binding.lastNameEt.text.toString(),
                    this.binding.postalCodeEt.text.toString(),
                    this.binding.phoneNumberEt.text.toString(),
                    this.binding.addressEt.text.toString(),
                    PAYMENT_METHOD_COD
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : NikeSingleObserver<SubmitOrderResult>(compositeDisposable) {
                        override fun onSuccess(t: SubmitOrderResult) {
                            this@ShippingActivity.emptyShoppingCart()
                            startActivity(
                                Intent(
                                    this@ShippingActivity,
                                    CheckOutActivity::class.java
                                ).apply {
                                    putExtra(EXTRA_KEY_ID, t.order_id)
                                }
                            )
                            finish()
                        }
                    })
            }
        }

        this.binding.shippingToolbar.onBackButtonClickListener = View.OnClickListener {
            finish()
        }
    }

    private fun checkInputValidation(): Boolean {
        val firstNameFieldLiveStatus = checkFieldStatus(this.binding.firstNameEt, getString(R.string.please_select_first_name))
        val lastNameFieldLiveStatus = checkFieldStatus(this.binding.lastNameEt, getString(R.string.please_enter_last_name))
        val postalCodeFieldLiveStatus = checkFieldStatus(this.binding.postalCodeEt,  getString(R.string.please_enter_postal_code))
        val phoneNumberFieldLiveStatus = checkFieldStatus(this.binding.phoneNumberEt, getString(R.string.please_enter_valid_phone_number))
        val addressFieldLiveStatus = checkFieldStatus(this.binding.addressEt, getString(R.string.please_enter_address))

        return !(!this.firstNameFieldLiveStatus() || !this.lastNameFieldLiveStatus() || !this.postalCodeFieldLiveStatus() || !this.phoneNumberFieldLiveStatus() || !this.addressFieldLiveStatus() ||
                !firstNameFieldLiveStatus || !lastNameFieldLiveStatus || !postalCodeFieldLiveStatus || !phoneNumberFieldLiveStatus || ! addressFieldLiveStatus)
    }

    private fun firstNameFieldLiveStatus():Boolean {
        if (this.binding.firstNameEt.text.toString().isEmpty()) {
            this.binding.firstNameEt.error =
                getString(R.string.please_select_first_name)
            return false
        }
        return true
    }
    private fun lastNameFieldLiveStatus():Boolean {
        if (this.binding.lastNameEt.text.toString().isEmpty()) {
            this.binding.lastNameEt.error = getString(R.string.please_enter_last_name)
            return false
        }
        return true
    }

    private fun postalCodeFieldLiveStatus():Boolean {
        if (this.binding.postalCodeEt.text.toString().isEmpty() ||
            this.binding.postalCodeEt.text.toString().length != 10) {
            this.binding.postalCodeEt.error = getString(R.string.please_enter_postal_code)
            return false
        }
        return true
    }

    private fun phoneNumberFieldLiveStatus():Boolean {
        if (this.binding.phoneNumberEt.text.toString().isEmpty() ||
            this.binding.phoneNumberEt.text.toString().length != 11 ||
            this.binding.phoneNumberEt.text.toString().startsWith("۰۹")) {
            this.binding.phoneNumberEt.error = getString(R.string.please_enter_valid_phone_number)
            return false
        }
        return true
    }

    private fun addressFieldLiveStatus():Boolean {
        if (this.binding.addressEt.text.toString().isEmpty()) {
            this.binding.addressEt.error = getString(R.string.please_enter_address)
            return false
        }
        return true
    }



    private fun checkFieldStatus(textInputLayout: TextInputEditText, errorMessage: String): Boolean {

        var returnValue = true

        textInputLayout.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not used in this example
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Not used in this example
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty() ) {
                    textInputLayout.error = errorMessage
                    returnValue = false
                } else {
                    returnValue = true
                }
            }
        })
        return returnValue
    }

    private fun removeItemFromCart(cartItem: CartItem) {
        cartViewModel.removeItemFromCart(cartItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : NikeCompletableObserver(compositeDisposable) {
                override fun onComplete() {
                    cartItemAdapter?.removeCartItem(cartItem)
                }
            })
    }

    override fun emptyShoppingCart() {
        this.cartViewModel.refresh()
    }

    override fun onResume() {
        super.onResume()
        cartViewModel.cartItemsLiveData.observe(this) {
            Timber.i(it.toString())
            loadingDialog.dismiss()
            it.forEach { catItem ->
                this.removeItemFromCart(catItem)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkUtils.unregisterNetworkChangeListener(this)
    }

    override fun onNetworkChanged(isConnected: Boolean) {
        if (isConnected) {
           this.loadingDialog.dismiss()
        }
    }
}
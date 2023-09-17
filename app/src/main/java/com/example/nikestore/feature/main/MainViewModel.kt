package com.example.nikestore.feature.main

import androidx.lifecycle.MutableLiveData
import com.example.nikestore.common.NikeSingleObserver
import com.example.nikestore.common.NikeViewModel
import com.example.nikestore.data.CartItemCount
import com.example.nikestore.data.PurchaseDetail
import com.example.nikestore.data.TokenContainer
import com.example.nikestore.data.repository.CartRepository
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus

class MainViewModel(private val cartRepository: CartRepository) : NikeViewModel() {

    val cartItemsCountLiveData = MutableLiveData<CartItemCount>()

    fun getCartItemsCount() {
        if (!TokenContainer.token.isNullOrEmpty()) {
            cartRepository.getCartItemsCount()
                .subscribeOn(Schedulers.io())
                .subscribe(object : NikeSingleObserver<CartItemCount>(compositeDisposable) {
                    override fun onSuccess(t: CartItemCount) {
                        EventBus.getDefault().postSticky(t)
                        cartItemsCountLiveData.postValue(t)
                    }
                })
        }
    }
}
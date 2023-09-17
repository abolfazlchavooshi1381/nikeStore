package com.example.nikestore.feature.home

import androidx.lifecycle.MutableLiveData
import com.example.nikestore.common.NikeCompletableObserver
import com.example.nikestore.common.NikeSingleObserver
import com.example.nikestore.common.NikeViewModel
import com.example.nikestore.data.*
import com.example.nikestore.data.repository.BannerRepository
import com.example.nikestore.data.repository.ProductRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val bannerRepository: BannerRepository
) : NikeViewModel() {

    val productsLatestLiveData = MutableLiveData<List<Product>>()
    val productsPopularLiveData = MutableLiveData<List<Product>>()
    val productsExpensiveLiveData = MutableLiveData<List<Product>>()
    val productsCheapestLiveData = MutableLiveData<List<Product>>()
    val bannersLiveData = MutableLiveData<List<Banner>>()

    init {
        this.getBanners()
        this.getProducts()
    }

    fun getLatest() {
        progressBarLiveData.postValue(true)
        productRepository.getProducts(SORT_LATEST)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progressBarLiveData.postValue(false) }
            .subscribe(object : NikeSingleObserver<List<Product>>(compositeDisposable) {
                override fun onSuccess(t: List<Product>) {
                    productsLatestLiveData.postValue(t)
                }
            })
    }

    fun getPopular() {
        progressBarLiveData.postValue(true)
        productRepository.getProducts(SORT_POPULAR)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progressBarLiveData.postValue(false) }
            .subscribe(object : NikeSingleObserver<List<Product>>(compositeDisposable) {
                override fun onSuccess(t: List<Product>) {
                    productsPopularLiveData.postValue(t)
                }
            })
    }

    fun getExpensive() {
        progressBarLiveData.postValue(true)
        productRepository.getProducts(SORT_PRICE_DESC)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progressBarLiveData.postValue(false) }
            .subscribe(object : NikeSingleObserver<List<Product>>(compositeDisposable) {
                override fun onSuccess(t: List<Product>) {
                    productsExpensiveLiveData.postValue(t)
                }
            })
    }

    fun getCheapest() {
        progressBarLiveData.postValue(true)
        productRepository.getProducts(SORT_PRICE_ASC)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { progressBarLiveData.postValue(false) }
            .subscribe(object : NikeSingleObserver<List<Product>>(compositeDisposable) {
                override fun onSuccess(t: List<Product>) {
                    productsCheapestLiveData.postValue(t)
                }
            })
    }

    fun getBanners() {
        bannerRepository.getBanners()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : NikeSingleObserver<List<Banner>>(compositeDisposable) {
                override fun onSuccess(t: List<Banner>) {
                    bannersLiveData.postValue(t)
                }
            })
    }

    fun addProductToFavorites(product: Product) {
        if (product.isFavorite)
            productRepository.deleteFromFavorites(product)
                .subscribeOn(Schedulers.io())
                .subscribe(object : NikeCompletableObserver(compositeDisposable) {
                    override fun onComplete() {
                        product.isFavorite = false
                        this@HomeViewModel.getProducts()
                    }
                })
        else
            productRepository.addToFavorites(product)
                .subscribeOn(Schedulers.io())
                .subscribe(object : NikeCompletableObserver(compositeDisposable) {
                    override fun onComplete() {
                        product.isFavorite = true
                        this@HomeViewModel.getProducts()
                    }
                })
    }

    private fun getProducts() {
        getLatest()
        getPopular()
        getExpensive()
        getCheapest()
    }
}
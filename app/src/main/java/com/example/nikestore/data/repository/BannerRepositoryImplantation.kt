package com.example.nikestore.data.repository

import com.example.nikestore.data.Banner
import com.example.nikestore.data.repository.source.BannerDataSource
import io.reactivex.Single

class BannerRepositoryImplantation(val bannerRemoteDataSource: BannerDataSource) :
    BannerRepository {
    override fun getBanners(): Single<List<Banner>> = bannerRemoteDataSource.getBanner()
}
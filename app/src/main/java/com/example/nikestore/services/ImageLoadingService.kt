package com.example.nikestore.services

import com.example.nikestore.view.NikeImageView
import java.net.URL

interface ImageLoadingService {

    fun load(imageView: NikeImageView, imageUrl: String)
}
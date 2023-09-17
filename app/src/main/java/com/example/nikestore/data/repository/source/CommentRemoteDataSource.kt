package com.example.nikestore.data.repository.source

import com.example.nikestore.data.Comment
import com.example.nikestore.services.http.ApiService
import io.reactivex.Single

class CommentRemoteDataSource(val apiService: ApiService) : CommentDataSource {
    override fun getAll(productId: Int): Single<List<Comment>> = apiService.getComments(productId)

    override fun insert(productId: Int, title: String, content: String): Single<Comment> = apiService.addComments(productId, title, content)
}
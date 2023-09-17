package com.example.nikestore.data.repository

import com.example.nikestore.data.Comment
import com.example.nikestore.data.repository.source.CommentDataSource
import io.reactivex.Single

class CommentRepositoryImplantation(val commentDataSource: CommentDataSource) : CommentRepository {
    override fun getAll(productId: Int): Single<List<Comment>> = commentDataSource.getAll(productId)

    override fun insert(productId: Int, title: String, content: String): Single<Comment> = commentDataSource.insert(productId, title, content)
}
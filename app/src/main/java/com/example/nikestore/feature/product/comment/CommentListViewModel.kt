package com.example.nikestore.feature.product.comment

import androidx.lifecycle.MutableLiveData
import com.example.nikestore.common.NikeSingleObserver
import com.example.nikestore.common.NikeViewModel
import com.example.nikestore.common.asyncNetworkRequest
import com.example.nikestore.data.Comment
import com.example.nikestore.data.repository.CommentRepository

class CommentListViewModel(productId: Int, commentRepository: CommentRepository) : NikeViewModel() {
    val commentsLiveData = MutableLiveData<List<Comment>>()
    val addCommentsLiveData = MutableLiveData<Comment>()
    private var commentRepository: CommentRepository? = null
    var productId: Int? = null
    init {
        this.commentRepository = commentRepository
        this.productId = productId
    }

    fun getAll() {
        progressBarLiveData.value = true
        commentRepository!!.getAll(productId!!)
            .asyncNetworkRequest()
            .doFinally { progressBarLiveData.value = false }
            .subscribe(object : NikeSingleObserver<List<Comment>>(compositeDisposable) {
                override fun onSuccess(t: List<Comment>) {
                    commentsLiveData.value = t
                }
            })
    }
    fun addComment(title: String, content: String) {
        commentRepository!!.insert(productId!!, title, content)
            .asyncNetworkRequest()
            .doFinally { progressBarLiveData.value = false }
            .subscribe(object : NikeSingleObserver<Comment>(compositeDisposable) {
                override fun onSuccess(t: Comment) {
                    addCommentsLiveData.value = t
                }
            })
    }
}
package com.example.nikestore.feature.product.comment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nikestore.feature.common.LoadingDialog
import com.example.nikestore.R
import com.example.nikestore.common.EXTRA_KEY_ID
import com.example.nikestore.data.Comment
import com.example.nikestore.common.NikeActivity
import com.example.nikestore.databinding.ActivityCommentListBinding
import com.example.nikestore.databinding.ActivityStartBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CommentListActivity : NikeActivity() {

    private lateinit var binding: ActivityCommentListBinding
    val viewModel: CommentListViewModel by viewModel {
        parametersOf(
            intent.extras!!.getInt(
                EXTRA_KEY_ID
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityCommentListBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        loadingDialog.isCancelable = false
        loadingDialog.show(supportFragmentManager, null)

        viewModel.commentsLiveData.observe(this) {
            val adapter = CommentAdapter(true)
            this.binding.commentsRv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            adapter.comments = it as ArrayList<Comment>
            this.binding.commentsRv.adapter = adapter
            loadingDialog.dismiss()
        }

        this.binding.commentListToolbar.onBackButtonClickListener = View.OnClickListener {
            finish()
        }
    }
}
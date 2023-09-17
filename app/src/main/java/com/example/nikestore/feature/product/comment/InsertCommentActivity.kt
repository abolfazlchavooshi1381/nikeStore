package com.example.nikestore.feature.product.comment

import android.os.Bundle
import android.view.View
import com.example.nikestore.R
import com.example.nikestore.common.EXTRA_KEY_ID
import com.example.nikestore.common.NikeActivity
import com.example.nikestore.common.closeKeyboard
import com.example.nikestore.databinding.ActivityInsertCommentBinding
import kotlinx.android.synthetic.main.activity_comment_list.commentListToolbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class InsertCommentActivity : NikeActivity() {

    private lateinit var binding: ActivityInsertCommentBinding
    val viewModel: CommentListViewModel by viewModel {
        parametersOf(
            intent.extras!!.getInt(
                EXTRA_KEY_ID
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityInsertCommentBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        this.binding.sendCommentBtn.setOnClickListener {
            this.closeKeyboard(this.binding.contentTIET)
            loadingDialog.isCancelable = false
            loadingDialog.show(supportFragmentManager, null)

            if (!this.binding.titleTIET.text.isNullOrEmpty() && !this.binding.contentTIET.text.toString().isNullOrEmpty()) {
                viewModel.addComment(this.binding.titleTIET.text.toString(), this.binding.contentTIET.text.toString())
                showToast(this@InsertCommentActivity, getString(R.string.send_comment))
                loadingDialog.dismiss()
            }
        }

        commentListToolbar.onBackButtonClickListener = View.OnClickListener {
            finish()
        }
    }
}
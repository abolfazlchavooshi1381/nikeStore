package com.example.nikestore.feature.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import com.example.nikestore.R
import com.example.nikestore.common.NikeDialogFragment
import com.example.nikestore.databinding.FragmentAskQuestionFromUserBinding
import com.example.nikestore.data.helperInterface.IFragmentAskQuestionClickListener

class AskQuestionFromUserFragment(
    private val listener: IFragmentAskQuestionClickListener,
    private val headerQuestionText: String,
    private val bodyQuestionText: String,
    private val animationResource: Int,
    private val positiveButtonText: Int = R.string.yes,
    private val negativeButtonText: Int = R.string.no,
    private val cancelable: Boolean = true,
    override val rootView: CoordinatorLayout?,
    override val viewContext: Context?
) : NikeDialogFragment() {
    private lateinit var binding: FragmentAskQuestionFromUserBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(this.requireContext())
        this.binding = DataBindingUtil.inflate(
            LayoutInflater.from(this.requireContext()),
            R.layout.fragment_ask_question_from_user,
            null,
            false
        )
        this.binding.lifecycleOwner = this
        dialogBuilder.setView(this.binding.root)
        this.isCancelable = cancelable
        return dialogBuilder.create()
    }

    override fun onResume() {
        super.onResume()
        this.initializeViewsContent()
        this.setListeners()
    }

    private fun initializeViewsContent() {
        this.binding.fragmentAskQuestionFromUserLottieAnimation.setAnimation(this.animationResource)
        this.binding.fragmentAskQuestionFromUserHeaderQuestionTextTv.text = this.headerQuestionText
        this.binding.fragmentAskQuestionFromUserBodyQuestionTextTv.text = this.bodyQuestionText
        this.binding.fragmentAskQuestionFromUserPositiveBtn.text =
            getString(this.positiveButtonText)
        this.binding.fragmentAskQuestionFromUserNegativeBtn.text =
            getString(this.negativeButtonText)
    }

    private fun setListeners() {
        this.binding.fragmentAskQuestionFromUserPositiveBtn.setOnClickListener {
            this.dismiss()
            listener.onPositiveButtonCLicked()
        }

        this.binding.fragmentAskQuestionFromUserNegativeBtn.setOnClickListener {
            listener.onNegativeButtonClicked(this)
        }
    }
}
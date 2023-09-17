package com.example.nikestore.feature.aboutUs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import com.example.nikestore.R
import com.example.nikestore.common.NikeDialogFragment
import com.example.nikestore.databinding.FragmentShowErrorLogsBinding


class ShowErrorLogsDialogFragment(private val errorLogsText: String, override val rootView: CoordinatorLayout?, override val viewContext: Context?
) : NikeDialogFragment() {
    private lateinit var binding: FragmentShowErrorLogsBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(this.requireContext())
        this.binding = DataBindingUtil.inflate(
            LayoutInflater.from(this.requireContext()),
            R.layout.fragment_show_error_logs,
            null,
            false
        )
        this.binding.lifecycleOwner = this
        isCancelable = true
        dialogBuilder.setView(this.binding.root)
        return dialogBuilder.create()
    }

    override fun onResume() {
        super.onResume()
        this.setListeners()
        this.binding.fragmentShowErrorLogsErrorTextTv.text = errorLogsText
    }

    private fun setListeners() {
        this.binding.fragmentShowErrorLogsBackButtonIv.setOnClickListener {
            this.backButtonClicked()
        }
        this.binding.fragmentShowErrorLogsCloseBtn.setOnClickListener {
            this.backButtonClicked()
        }

        this.binding.fragmentShowErrorLogsScrollDownAnimation.setOnClickListener {
            this.binding.fragmentShowErrorLogsNestedScrollView.post {
                this.binding.fragmentShowErrorLogsNestedScrollView.fullScroll(
                    View.FOCUS_DOWN
                )
            }
        }
    }
    private fun backButtonClicked(){
        this.dismiss()
    }
}
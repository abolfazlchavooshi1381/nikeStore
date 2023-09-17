package com.example.nikestore.feature.common

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.nikestore.R

class LoadingDialog : DialogFragment() {

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = context?.let { AlertDialog.Builder(it) }
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.view_loading, null, false)
        builder?.setView(view)
        if (builder != null) {
            return builder.create()
        } else TODO()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog)
        dialog?.window?.setGravity(Gravity.CENTER)
    }

    override fun onResume() {
        super.onResume()
        val window: Window = dialog?.window ?: return
        val params = window.attributes
        params.width = 220
        params.height = 220
        window.attributes = params
    }
}

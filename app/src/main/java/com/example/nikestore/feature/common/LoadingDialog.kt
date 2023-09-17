package com.example.nikestore.feature.common

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.nikestore.R

class LoadingDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = getContext()?.let { AlertDialog.Builder(it) }
        val view: View =
            LayoutInflater.from(getContext()).inflate(R.layout.view_loading, null, false)
        builder?.setView(view)
        if (builder != null) {
            return builder.create()
        } else TODO()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getDialog()?.getWindow()?.setBackgroundDrawableResource(R.drawable.rounded_dialog)
        getDialog()?.getWindow()?.setGravity(Gravity.CENTER)
    }

    override fun onResume() {
        super.onResume()
        val window: Window = getDialog()?.getWindow() ?: return
        val params = window.attributes
        params.width = 200
        params.height = 200
        window.attributes = params
    }
}

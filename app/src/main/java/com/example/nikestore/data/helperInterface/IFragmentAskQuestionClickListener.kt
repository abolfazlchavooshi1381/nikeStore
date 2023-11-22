package com.example.nikestore.data.helperInterface

import com.example.nikestore.common.NikeDialogFragment


interface IFragmentAskQuestionClickListener {
    fun onPositiveButtonCLicked()

    fun onNegativeButtonClicked(dialog: NikeDialogFragment)
}
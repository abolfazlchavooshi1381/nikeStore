package com.example.nikestore.data

import androidx.annotation.StringRes

class EmptyState(
    val mustShow: Boolean,
    @StringRes val messageResId: Int = 0,
    val mustShowCallToActionButton: Boolean = false
)
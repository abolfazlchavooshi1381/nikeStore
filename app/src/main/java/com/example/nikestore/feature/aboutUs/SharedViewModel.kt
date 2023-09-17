package com.example.nikestore.feature.aboutUs

import com.example.nikestore.common.NikeViewModel
import com.example.nikestore.common.SingleLiveEvent
import com.example.nikestore.data.InsertingNotDeliveredItemResult

class SharedViewModel : NikeViewModel() {
    val insertNotDeliveredItemsStatusLiveData = SingleLiveEvent<InsertingNotDeliveredItemResult>()
    val distributionOperationStatusLiveData = SingleLiveEvent<Boolean>()
    val registerUnSuccessfulVisitLiveData = SingleLiveEvent<String>()
    val anyCatalogItemsAdded = SingleLiveEvent<Boolean>()

    fun setDistributionStatusResult(operationStatus: Boolean) {
        this.distributionOperationStatusLiveData.value = operationStatus
    }

    fun setInsertNotDeliveredItemsStatusResult(operationStatus: InsertingNotDeliveredItemResult) {
        this.insertNotDeliveredItemsStatusLiveData.value = operationStatus
    }

    fun setRegisterUnSuccessfulVisitStatusResult(operationStatusInformation: String) {
        this.registerUnSuccessfulVisitLiveData.value = operationStatusInformation
    }

    fun setAnyCatalogItemsAdded(anyItemsAdded: Boolean) {
        this.anyCatalogItemsAdded.value = anyItemsAdded
    }
}
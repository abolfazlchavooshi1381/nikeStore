package com.example.nikestore.data.repository.source

import com.example.nikestore.data.Checkout
import com.example.nikestore.data.OrderHistoryItem
import com.example.nikestore.data.SubmitOrderResult
import io.reactivex.Single

interface OrderDataSource {

    fun submit(
        firstName: String,
        lastName: String,
        postalCode: String,
        phoneNumber: String,
        address: String,
        paymentMethod: String
    ): Single<SubmitOrderResult>

    fun checkout(orderId: Int): Single<Checkout>

    fun list(): Single<List<OrderHistoryItem>>
}
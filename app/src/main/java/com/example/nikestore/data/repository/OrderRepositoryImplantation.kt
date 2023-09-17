package com.example.nikestore.data.repository

import com.example.nikestore.data.Checkout
import com.example.nikestore.data.OrderHistoryItem
import com.example.nikestore.data.SubmitOrderResult
import com.example.nikestore.data.repository.source.OrderDataSource
import io.reactivex.Single

class OrderRepositoryImplantation(val orderDataSource: OrderDataSource) : OrderRepository {
    override fun submit(
        firstName: String,
        lastName: String,
        postalCode: String,
        phoneNumber: String,
        address: String,
        paymentMethod: String
    ): Single<SubmitOrderResult> {
        return orderDataSource.submit(
            firstName,
            lastName,
            postalCode,
            phoneNumber,
            address,
            paymentMethod
        )
    }

    override fun checkout(orderId: Int): Single<Checkout> {
        return orderDataSource.checkout(orderId)
    }

    override fun list(): Single<List<OrderHistoryItem>> = orderDataSource.list()
}
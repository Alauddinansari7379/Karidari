package com.example.tlismimoti.order.model.ModelPendingOrder

data class OrderItem(
    val amount: Int,
    val id: Int,
    val info: String,
    val medias: List<Any>,
    val order_id: Int,
    val qty: Int,
    val term: Term,
    val term_id: Int
)
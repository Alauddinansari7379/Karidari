package com.example.tlismimoti .order.model.modelOrderDet

data class OrderItemWithFile(
    val amount: Int,
    val `file`: List<Any>,
    val id: Int,
    val info: String,
    val media: Any,
    val medias: List<Any>,
    val order_id: Int,
    val qty: Int,
    val term: Term,
    val term_id: Int
)
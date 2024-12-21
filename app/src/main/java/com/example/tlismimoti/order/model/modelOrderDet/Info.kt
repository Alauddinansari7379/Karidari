package com.example.tlismimoti .order.model.modelOrderDet

data class Info(
    val affid: Any,
    val category_id: Int,
    val created_at: String,
    val customer_id: Int,
    val id: Int,
    val is_pay: Int,
    val order_content: OrderContent,
    val order_item_with_file: ArrayList<OrderItemWithFile>,
    val order_no: String,
    val order_type: Int,
    val payment_method: Any,
    val payment_status: Int,
    val s_date: Any,
    val shipping: Int,
    val shipping_info: Any,
    val status: String,
    val tax: Int,
    val total: Int,
    val trans_id: String,
    val transaction_id: Any,
    val updated_at: String,
    val user_id: Int
)
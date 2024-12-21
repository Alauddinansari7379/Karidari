package com.example.tlismimoti.order.model.ModelPendingOrder

data class Customer(
    val created_at: String,
    val created_by: Int,
    val deactivate: Int,
    val device_id: String,
    val device_type: String,
    val domain_id: Int,
    val email: String,
    val id: Int,
    val mobile: String,
    val name: String,
    val updated_at: String
)
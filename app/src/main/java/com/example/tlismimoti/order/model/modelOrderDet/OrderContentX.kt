package com.example.tlismimoti .order.model.modelOrderDet

data class OrderContentX(
    val address: String,
    val comment: Any,
    val coupon_discount: String,
    val email: String,
    val name: String,
    val phone: Any,
    val sub_total: Int,
    val sub_totalfdf: Int,
    val zip_code: String
)
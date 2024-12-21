package com.amtech.mehfeel.category.model.ModelCatWithId

data class Price(
    val ending_date: Any,
    val id: Int,
    val price: Int,
    val price_type: Int,
    val regular_price: String?,
    val special_price: String?,
    val starting_date: Any,
    val term_id: Int
)
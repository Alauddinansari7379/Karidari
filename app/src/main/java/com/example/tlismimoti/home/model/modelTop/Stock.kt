package com.amtech.mehfeel.home.model.modelTop

data class Stock(
    val id: Int,
    val sku: Any,
    val stock_manage: Int,
    val stock_qty: Int,
    val stock_status: Int,
    val term_id: Int
)
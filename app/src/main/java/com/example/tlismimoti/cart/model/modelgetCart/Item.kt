package com.example.tlismimoti.cart.model.modelgetCart

data class Item(
    val attributes: String,
    val final_total: Int,
    val id: Int,
    val options: String,
    val preview: String,
    val price: String,
    val qty: String,
    val subtotal: Int,
    val tax: Tax,
    val term_id: String,
    val term_title: String
)
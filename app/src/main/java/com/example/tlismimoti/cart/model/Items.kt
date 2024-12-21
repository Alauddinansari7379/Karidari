package com.example.tlismimoti.cart.model

import com.amtech.mehfeel.category.model.Option
import com.amtech.tlismiherbs.category.model.Attribute
import com.example.tlismimoti.cart.model.modelgetCart.Tax

data class Items(
     val term_title:String,
    val price:String,
    val qty:String,
    val preview:String,
    val term_id:String,
    val tax:TaxX,
    val id:String?,
    val subtotal:String,
    val attributes: List<Attribute>,
    val options: List<Option>,
    val final_total:String?,
)
package com.amtech.mehfeel.home.model.modelTop

data class Media(
    val created_at: String,
    val id: Int,
    val name: String,
    val pivot: Pivot,
    val updated_at: String,
    val url: String,
    val user_id: Int
)
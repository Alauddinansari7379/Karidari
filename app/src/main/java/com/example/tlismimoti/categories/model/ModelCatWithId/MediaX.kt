package com.amtech.mehfeel.category.model.ModelCatWithId

data class MediaX(
    val created_at: String,
    val id: Int,
    val name: String,
    val pivot: PivotX,
    val updated_at: String,
    val url: String,
    val user_id: Int
)
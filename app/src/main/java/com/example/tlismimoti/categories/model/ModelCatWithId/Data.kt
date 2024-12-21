package com.amtech.mehfeel.category.model.ModelCatWithId

data class Data(
    val created_at: String,
    val featured: Int,
    val id: Int,
    val is_admin: Int,
    val menu_status: Int,
    val name: String,
    val p_id: Any,
    val s_id: Any,
    val slug: String,
    val take_prod: ArrayList<TakeProd>,
    val type: String,
    val updated_at: String,
    val user_id: Int
)
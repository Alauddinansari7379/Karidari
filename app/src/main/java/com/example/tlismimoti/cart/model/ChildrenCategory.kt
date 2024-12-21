package com.amtech.mehfeel.category.model

data class ChildrenCategory(
    val amount: Int,
    val amount_type: Int,
    val categories: List<Any>,
    val id: Int,
    val is_required: Int,
    val name: String,
    val p_id: Int,
    val rprice: String,
    val select_type: Int,
    val term_id: Int,
    val type: Int,
    val user_id: Int
)
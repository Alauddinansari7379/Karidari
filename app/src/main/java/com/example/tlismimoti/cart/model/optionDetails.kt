package com.amtech.tlismiherbs.category.model

import com.amtech.mehfeel.category.model.ChildrenCategory

data class optionDetails(
    val amount: Any,
    val amount_type: Int,
    val children_categories: ArrayList<ChildrenCategory>,
    val id: Int,
    val is_required: Int,
    val name: String,
    val p_id: Any,
    val rprice: Any,
    val select_type: Int,
    val term_id: Int,
    val type: Int,
    val user_id: Int
)
package com.amtech.tlismiherbs.category.model


data class Attribute(
    val attribute: AttributeX,
    val category_id: Int,
    val id: Int,
    val term_id: Int,
    val variation: Variation,
    val variation_id: Int
)
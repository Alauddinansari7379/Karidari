package com.example.tlismimoti.listing.model

import com.amtech.tlismiherbs.category.model.Attribute
import com.amtech.tlismiherbs.category.model.optionDetails

data class Info(
    val affiliate: Any,
    val answer1: Any,
    val answer2: Any,
    val answer3: Any,
    val answer4: Any,
    val answer5: Any,
    val attributes: List<Attribute>,
    val brands: List<Any>,
    val categories: ArrayList<Category>,
    val content: ContentX,
    val created_at: String,
    val featured: Int,
    val id: Int,
    val is_admin: Int,
    val medias: ArrayList<Media>,
    val options: ArrayList<optionDetails>,
    val price: Price,
    val question1: Any,
    val question2: Any,
    val question3: Any,
    val question4: Any,
    val question5: Any,
    val seo: Seo,
    val service_location: Any,
    val service_type: Any,
    val slug: String,
    val status: Int,
    val stock: Stock,
    val title: String,
    val type: String,
    val updated_at: String,
    val user_id: Int,
    val youtube_link: Any
)
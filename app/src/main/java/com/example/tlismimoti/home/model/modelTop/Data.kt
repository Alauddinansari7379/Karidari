package com.amtech.mehfeel.home.model.modelTop

data class Data(
    val get_latest_products: ArrayList<GetTrendingProduct>,
    val get_offerable_products: ArrayList<GetTrendingProduct>,
    val get_trending_products: ArrayList<GetTrendingProduct>,
)
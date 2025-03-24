package com.app.demo_MVVM_Hilt

data class FestivalResponse(
    val status: String,
    val message: String,
    val festivals: List<Festival>
)
data class Festival(
    val festival_id: String,
    val festival_name: String,
    val festival_image: String,
    val festival_date: String
)
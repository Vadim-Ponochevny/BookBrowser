package com.example.bookbrowser.data.model

// UI model shared by search results and work details.
data class BookItem(
    val id: String,
    val title: String,
    val authors: List<String> = emptyList(),
    val description: String? = null,
    val coverUrl: String? = null,
    val averageRating: Double? = null
)

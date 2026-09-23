package com.example.bookbrowser.data.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class BookResponse(val docs: List<SearchBook>? = null)

data class SearchBook(
    val key: String? = null,
    val title: String? = null,
    @SerializedName("author_name") val authors: List<String>? = null,
    @SerializedName("cover_i") val coverId: Long? = null,
    @SerializedName("ratings_average") val averageRating: Double? = null
) {
    fun toBookItem(): BookItem? {
        val id = key?.substringAfterLast('/')?.takeIf { it.matches(Regex("OL[0-9]+W")) }
            ?: return null
        return BookItem(id,
            title ?: "Untitled", authors.orEmpty(), null, coverImage(coverId), averageRating
        )
    }
}

data class WorkResponse(
    val title: String? = null,
    val description: JsonElement? = null,
    val covers: List<Long>? = null
) {
    fun toBookItem(id: String, summary: BookItem?): BookItem {
        val text = description?.let {
            when {
                it.isJsonPrimitive && it.asJsonPrimitive.isString -> it.asString
                it.isJsonObject -> it.asJsonObject.get("value")
                    ?.takeIf { value -> value.isJsonPrimitive && value.asJsonPrimitive.isString }
                    ?.asString
                else -> null
            }
        }?.takeIf { it.isNotBlank() }
        return BookItem(id,
            title ?: summary?.title ?: "Untitled",
            summary?.authors.orEmpty(),
            text,
            coverImage(covers?.firstOrNull { it > 0 }) ?: summary?.coverUrl,
            summary?.averageRating
        )
    }
}

private fun coverImage(id: Long?): String? =
    id?.takeIf { it > 0 }?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" }

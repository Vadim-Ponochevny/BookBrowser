package com.example.bookbrowser

import com.example.bookbrowser.data.model.BookResponse
import com.example.bookbrowser.data.model.WorkResponse
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class OpenLibraryTest {
    private val gson = Gson()

    @Test fun searchMapsNamesCoversAndNavigationSafeId() {
        val response = gson.fromJson("""
            {"docs":[{"key":"/works/OL123W","title":"Test","author_name":["Author"],
            "cover_i":42,"ratings_average":4.5}]}
        """, BookResponse::class.java)
        val book = response.docs!!.single().toBookItem()!!
        assertEquals("OL123W", book.id)
        assertEquals(listOf("Author"), book.authors)
        assertEquals("https://covers.openlibrary.org/b/id/42-L.jpg", book.coverUrl)
        assertEquals(4.5, book.averageRating!!, 0.0)
    }

    @Test fun absentFieldsAndInvalidKeysAreSafe() {
        assertTrue(gson.fromJson("{}", BookResponse::class.java).docs.orEmpty().isEmpty())
        val response = gson.fromJson("""
            {"docs":[{"key":"OL123W"},{"key":"/books/OL123M"},{}]}
        """, BookResponse::class.java)
        val books = response.docs!!.mapNotNull { it.toBookItem() }
        assertEquals(1, books.size)
        assertNull(books.single().coverUrl)
    }

    @Test fun workSupportsBothDescriptionFormatsAndMissingDescription() {
        for (description in listOf("\"Plain text\"", """{"type":"/type/text","value":"Plain text"}""")) {
            val work = gson.fromJson("""{"description":$description}""", WorkResponse::class.java)
            assertEquals("Plain text", work.toBookItem("OL1W", null).description)
        }
        assertNull(gson.fromJson("{}", WorkResponse::class.java)
            .toBookItem("OL1W", null).description)
    }

    @Test fun workPreservesSummaryMetadataAndSkipsInvalidCovers() {
        val summary = gson.fromJson("""
            {"docs":[{"key":"OL1W","title":"Title","author_name":["Author"],"cover_i":42}]}
        """, BookResponse::class.java).docs!!.single().toBookItem()!!
        val work = gson.fromJson("""{"covers":[-1,99]}""", WorkResponse::class.java)
        val book = work.toBookItem("OL1W", summary)
        assertEquals("Title", book.title)
        assertEquals(listOf("Author"), book.authors)
        assertEquals("https://covers.openlibrary.org/b/id/99-L.jpg", book.coverUrl)
        assertEquals(summary.coverUrl,
            gson.fromJson("{}", WorkResponse::class.java).toBookItem("OL1W", summary).coverUrl)
    }
}

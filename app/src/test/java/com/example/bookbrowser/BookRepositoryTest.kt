package com.example.bookbrowser

import com.example.bookbrowser.data.model.*
import com.example.bookbrowser.data.network.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class BookRepositoryTest {
    private class FakeApi : BookApi {
        var searchCalls = 0
        var detailCalls = 0
        var lastQuery = ""
        var failSearch = false
        override suspend fun getBooks(query: String, fields: String, limit: Int): BookResponse {
            searchCalls++
            lastQuery = query
            if (failSearch) throw java.io.IOException("Offline")
            return BookResponse(listOf(SearchBook("OL1W", "Title", listOf("Author"))))
        }
        override suspend fun getBookDetails(bookId: String): WorkResponse {
            detailCalls++
            assertEquals("OL1W", bookId)
            return WorkResponse(title = "Title")
        }
    }

    @Test fun listMetadataIsReusedAndDetailsAreCached() = runBlocking {
        val api = FakeApi()
        val repository = BookRepository(api)
        val id = repository.getBooks().single().id
        val book = repository.getBookDetails(id)
        assertEquals(listOf("Author"), book.authors)
        assertEquals(book, repository.getBookDetails(id))
        assertEquals(1, api.searchCalls)
        assertEquals(1, api.detailCalls)
    }

    @Test fun detailsRestoreMetadataWithoutAnEarlierListRequest() = runBlocking {
        val api = FakeApi()
        val book = BookRepository(api).getBookDetails("OL1W")
        assertEquals("key:/works/OL1W", api.lastQuery)
        assertEquals(listOf("Author"), book.authors)
    }

    @Test fun optionalMetadataFailureStillShowsTheWork() = runBlocking {
        val api = FakeApi().apply { failSearch = true }
        assertEquals("Title", BookRepository(api).getBookDetails("OL1W").title)
    }
}

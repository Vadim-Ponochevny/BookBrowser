package com.example.bookbrowser.data.network

import com.example.bookbrowser.data.model.BookItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class BookRepository(private val api: BookApi) {
    private val summaries = mutableMapOf<String, BookItem>()
    private val details = mutableMapOf<String, BookItem>()
    private val requestMutex = Mutex()
    private var lastRequestNanos: Long? = null

    // Open Library allows one request per second without contact identification.
    private suspend fun <T> request(block: suspend () -> T): T = requestMutex.withLock {
        lastRequestNanos?.let {
            val remaining = 1_000L - (System.nanoTime() - it) / 1_000_000
            if (remaining > 0) delay(remaining)
        }
        lastRequestNanos = System.nanoTime()
        block()
    }

    suspend fun getBooks(): List<BookItem> {
        val books = request { api.getBooks() }.docs.orEmpty().mapNotNull { it.toBookItem() }
        books.forEach { summaries[it.id] = it }
        return books
    }

    suspend fun getBookDetails(id: String): BookItem {
        require(id.matches(Regex("OL[0-9]+W"))) { "Invalid Open Library work ID" }
        details[id]?.let { return it }
        val work = request { api.getBookDetails(id) }
        // Restore author names and rating even after Android recreates the process.
        val summary = summaries[id] ?: try {
            request { api.getBooks(query = "key:/works/$id", limit = 1) }
                .docs.orEmpty().mapNotNull { it.toBookItem() }.firstOrNull { it.id == id }
                ?.also { summaries[id] = it }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null // Optional metadata must not hide an otherwise readable work.
        }
        return work.toBookItem(id, summary).also { details[id] = it }
    }
}

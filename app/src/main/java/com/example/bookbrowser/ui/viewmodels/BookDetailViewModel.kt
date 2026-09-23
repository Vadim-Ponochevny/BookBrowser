package com.example.bookbrowser.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookbrowser.data.model.BookItem
import com.example.bookbrowser.data.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job

class BookDetailViewModel : ViewModel() {
    private val _book = MutableStateFlow<BookItem?>(null)
    val book = _book.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    private var loadJob: Job? = null

    fun loadBookDetails(bookId: String) {
        if (_book.value?.id == bookId) return

        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _book.value = null
                val result = NetworkModule.repository.getBookDetails(bookId)
                _book.value = result
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "Could not load this book. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }
}
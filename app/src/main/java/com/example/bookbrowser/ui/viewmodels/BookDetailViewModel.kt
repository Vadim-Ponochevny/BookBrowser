package com.example.bookbrowser.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookbrowser.data.model.BookItem
import com.example.bookbrowser.data.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookDetailViewModel : ViewModel() {
    private val _book = MutableStateFlow<BookItem?>(null)
    val book = _book.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadBookDetails(bookId: String) {
        if (_book.value?.id == bookId) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = NetworkModule.api.getBookDetails(bookId)
                _book.value = result
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }
}
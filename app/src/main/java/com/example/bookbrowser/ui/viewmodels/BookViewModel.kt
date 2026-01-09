package com.example.bookbrowser.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookbrowser.data.model.BookItem
import com.example.bookbrowser.data.network.NetworkModule
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {
    private val _books = mutableStateOf<List<BookItem>>(emptyList())
    val books: State<List<BookItem>> = _books

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = NetworkModule.api.getBooks()
                _books.value = response.items
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }
}
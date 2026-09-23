package com.example.bookbrowser.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookbrowser.data.model.BookItem
import com.example.bookbrowser.data.network.NetworkModule
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

class BookViewModel : ViewModel() {
    private val _books = mutableStateOf<List<BookItem>>(emptyList())
    val books: State<List<BookItem>> = _books

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _books.value = NetworkModule.repository.getBooks()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "Could not load books. Check your connection and try again."
            } finally {
                _isLoading.value = false
            }
        }
    }
}
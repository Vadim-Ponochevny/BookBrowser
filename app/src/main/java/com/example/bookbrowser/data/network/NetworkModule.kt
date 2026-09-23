package com.example.bookbrowser.data.network

import com.example.bookbrowser.data.model.BookResponse
import com.example.bookbrowser.data.model.WorkResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApi {
    @GET("search.json")
    suspend fun getBooks(
        @Query("q") query: String = "subject:fiction",
        @Query("fields") fields: String = "key,title,author_name,cover_i,ratings_average",
        @Query("limit") limit: Int = 20
    ): BookResponse

    @GET("works/{bookId}.json")
    suspend fun getBookDetails(@Path("bookId") bookId: String): WorkResponse
}

object NetworkModule {
    private const val BASE_URL = "https://openlibrary.org/"

    val api: BookApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder()
                    .header("User-Agent", "BookBrowser/1.0 (Android)")
                    .build())
            }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BookApi::class.java)
    }

    val repository: BookRepository by lazy { BookRepository(api) }
}

package com.example.bookbrowser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.bookbrowser.ui.navigation.AppNavigation
import com.example.bookbrowser.ui.theme.BookBrowserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookBrowserTheme {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController
                )
            }
        }
    }
}
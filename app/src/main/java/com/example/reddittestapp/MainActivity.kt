package com.example.reddittestapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.reddittestapp.ui.theme.RedditTestAppTheme
import com.example.reddittestapp.ui.theme.VerticalScreen
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RedditTestAppTheme {
                Surface(
                    Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: RedditViewModel = viewModel()
                    VerticalScreen(viewModel = viewModel)
                }
            }
        }
    }
}

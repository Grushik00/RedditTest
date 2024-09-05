package com.example.reddittestapp.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reddittestapp.RedditViewModel

@Composable
fun VerticalScreen(viewModel: RedditViewModel) {
    //val postsData = RedditViewModel()
    val scrollState = rememberScrollState()
    //postsData.fetchData()
    var posts by remember { mutableStateOf(viewModel.posts.value) }

    LaunchedEffect(Unit){
        viewModel.posts.collect { newPosts ->
            //posts.clear()
            posts = newPosts
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        posts.forEach() {
            Text(text = "${it.author}, \n${it.title}, \n${it.subreddit}, \n${it.created_utc}, \n${it.ups}, \n${it.url}, \n${it.thumbnail}")

        }

    }
}


package com.example.reddittestapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RedditViewModel : ViewModel() {
    private var _posts = MutableStateFlow<List<RedditPost>>(emptyList())
    var posts: StateFlow<List<RedditPost>> = _posts
    init {
        fetchData()
    }
    fun fetchData() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getTopPosts(5)
                _posts.value = response.data.children.map {
                    RedditPost(
                        author = it.data.author,
                        created_utc = it.data.created_utc,
                        subreddit = it.data.subreddit,
                        title = it.data.title,
                        ups = it.data.ups,
                        url = it.data.url,
                        thumbnail = it.data.thumbnail
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace() // Обработка ошибок
            }
        }
    }
}


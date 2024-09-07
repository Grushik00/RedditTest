package com.example.reddittestapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RedditViewModel : ViewModel() {
    var posts = MutableStateFlow<List<RedditPost>>(emptyList())
    var after = ""
    private val limit = 15
    var isLoading = false

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.apiService.getTopPosts(limit)
                after = response.data.after
                posts.value = response.data.children.map {
                    RedditPost(
                        author = it.data.author,
                        created_utc = it.data.created_utc,
                        title = it.data.title,
                        thumbnail = it.data.thumbnail,
                        num_comments = it.data.num_comments
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun loadNextPage(after: String) {
        if (isLoading) return
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.apiService.getNextPage(limit, after)
                this@RedditViewModel.after = response.data.after
                posts.value += response.data.children.map {
                    RedditPost(
                        author = it.data.author,
                        created_utc = it.data.created_utc,
                        title = it.data.title,
                        thumbnail = it.data.thumbnail,
                        num_comments = it.data.num_comments
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}


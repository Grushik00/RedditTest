package com.example.reddittestapp


data class RedditResponse(
    val data: RedditData
)

data class RedditData(
    val children: List<RedditPostWrapper>,
    val after: String
)

data class RedditPostWrapper(
    val data: RedditPost
)

data class RedditPost(
    val author: String,
    val created_utc: Long,
    val title: String,
    val subreddit: String,
    val ups: Int,
    val url: String,
    val thumbnail: String?,
    val author_fullname: String
)

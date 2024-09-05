package com.example.reddittestapp

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface RedditApiService {
    @GET("r/all/top.json")
    suspend fun getTopPosts(
        @Query("limit") limit: Int
    ): RedditResponse
}

object RetrofitClient {
    val apiService: RedditApiService by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://www.reddit.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RedditApiService::class.java)
    }

}

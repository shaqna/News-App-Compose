package com.ngedev.newsapplicationcompose.data.source.remote.response

data class Response<T>(
    val status: String,
    val totalResults: Long,
    val articles: List<T>
)

package com.example.healthmanager.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/** No HTTP body logger is installed, to avoid leaking sensitive health details. */
interface LlmApiService {
    @POST("chat/completions")
    suspend fun chatCompletions(
        @Header("Authorization") authorization: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

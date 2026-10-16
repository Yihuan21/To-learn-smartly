package com.example.healthmate.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class ChatMessage(val role: String, val content: String)
data class JsonResponseFormat(val type: String = "json_object")
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.2,
    @SerializedName("response_format") val responseFormat: JsonResponseFormat = JsonResponseFormat()
)
data class ChatCompletionResponse(val choices: List<Choice> = emptyList()) {
    data class Choice(val message: ChatMessage)
}
interface LlmApiService {
    @POST("chat/completions")
    suspend fun chatCompletions(
        @Header("Authorization") authorization: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

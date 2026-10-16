package com.example.healthmanager.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessage(val role: String, val content: String)

@JsonClass(generateAdapter = true)
data class ResponseFormat(val type: String = "json_object")

@JsonClass(generateAdapter = true)
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val response_format: ResponseFormat = ResponseFormat(),
    val temperature: Double = 0.2
)

@JsonClass(generateAdapter = true)
data class ChatCompletionResponse(val choices: List<ChatChoice> = emptyList())

@JsonClass(generateAdapter = true)
data class ChatChoice(val message: ChatMessage)

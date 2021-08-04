package com.check24.codingchallenge.data.domain.models

data class Question(
    val answers: Map<String, Any>,
    val correctAnswer: String,
    val question: String,
    val questionImageUrl: String? = null,
    val score: Int
)
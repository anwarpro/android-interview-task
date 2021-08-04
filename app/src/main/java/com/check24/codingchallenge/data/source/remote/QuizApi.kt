package com.check24.codingchallenge.data.source.remote

import com.check24.codingchallenge.data.domain.models.QuizResponse
import retrofit2.http.GET

interface QuizApi {
    @GET("vg2-quiz/quiz.json")
    suspend fun getQuestions(): QuizResponse
}
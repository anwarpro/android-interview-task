package com.check24.codingchallenge.data

import com.check24.codingchallenge.data.domain.models.QuizResponse
import com.check24.codingchallenge.data.source.remote.RemoteQuestionSource
import com.check24.codingchallenge.utils.Resource
import javax.inject.Inject

class QuizRepository @Inject constructor(private val remoteQuestionSource: RemoteQuestionSource) {
    /**
     *getting questions from remote source if internet inactive data will return from network cache
     */
    suspend fun getQuizQuestions(): Resource<QuizResponse> =
        remoteQuestionSource.getQuizQuestions()
}
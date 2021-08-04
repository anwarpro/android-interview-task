package com.check24.codingchallenge.di

import android.content.Context
import com.check24.codingchallenge.data.QuizRepository
import com.check24.codingchallenge.data.source.remote.RemoteQuestionSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object QuizRepoModule {
    @Provides
    fun provideQuizRepository(remoteQuestionSource: RemoteQuestionSource): QuizRepository {
        return QuizRepository(remoteQuestionSource)
    }

    @Provides
    fun provideRemoteQuizSource(@ApplicationContext appContext: Context): RemoteQuestionSource {
        return RemoteQuestionSource(appContext)
    }
}
package com.check24.codingchallenge.ui.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.check24.codingchallenge.data.QuizRepository
import com.check24.codingchallenge.data.domain.models.QuizResponse
import com.check24.codingchallenge.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizFragmentViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _quizQuestions = MutableLiveData<Resource<QuizResponse>>()
    val quizQuestions: LiveData<Resource<QuizResponse>>
        get() = _quizQuestions

    init {
        getQuizQuestions()
    }

    private fun getQuizQuestions() = viewModelScope.launch {
        _quizQuestions.value = Resource.Loading
        _quizQuestions.value = quizRepository.getQuizQuestions()
    }
}
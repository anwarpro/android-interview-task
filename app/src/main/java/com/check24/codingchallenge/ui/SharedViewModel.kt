package com.check24.codingchallenge.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    val highScore = MutableLiveData(0)

    fun updateScore(score: Int) {
        _score.value = _score.value?.plus(score)?.also {
            if (highScore.value ?: 0 < it) {
                highScore.value = it
            }
        }
    }
}
package com.check24.codingchallenge.data.domain.models

data class QuestionOption(
    var key: String,
    var option: Any?,
    var selected: String? = null,
    var correct: String? = null
) {
    companion object {
        fun Map.Entry<String, Any?>.toQuestionOption(
            selected: String? = null,
            correct: String? = null
        ): QuestionOption {
            return QuestionOption(this.key, this.value, selected, correct)
        }
    }
}
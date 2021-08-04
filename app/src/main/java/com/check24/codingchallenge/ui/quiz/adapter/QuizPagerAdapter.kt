package com.check24.codingchallenge.ui.quiz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.check24.codingchallenge.data.domain.models.Question
import com.check24.codingchallenge.data.domain.models.QuestionOption
import com.check24.codingchallenge.data.domain.models.QuestionOption.Companion.toQuestionOption
import com.check24.codingchallenge.databinding.ItemQuizBinding

class QuizPagerAdapter(
    private val onQuestionSelect: (Int) -> Unit
) : ListAdapter<Question, QuizPagerAdapter.QuestionPageView>(QuestionDiffUtil) {
    inner class QuestionPageView(
        private val itemBinding: ItemQuizBinding,
        private val onQuestionSelect: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private var currentQuestion: Question? = null
        private var adapter: QuestionOptionAdapter? = null

        init {
            adapter = QuestionOptionAdapter { _option ->
                currentQuestion?.let {
                    //check selected answer
                    val newOptionList = if (it.correctAnswer == _option.key) {
                        //answer correct
                        onQuestionSelect(it.score)
                        getMapListFromMap(it.answers, _option.key, it.correctAnswer)
                    } else {
                        //wrong answer
                        onQuestionSelect(0)
                        getMapListFromMap(it.answers, _option.key, it.correctAnswer)
                    }

                    adapter?.submitList(newOptionList)
                }
            }

            itemBinding.recyclerViewOptions.adapter = adapter
        }

        fun bind(question: Question) {
            currentQuestion = question

            itemBinding.textViewScore.text = "${question.score} Punkte"
            itemBinding.imageViewQuestionPhoto.apply {
                isVisible = question.questionImageUrl != null && question.questionImageUrl != ""
                load(question.questionImageUrl)
            }
            itemBinding.textViewQuestion.text =
                HtmlCompat.fromHtml(question.question, HtmlCompat.FROM_HTML_MODE_COMPACT)

            //update question options
            adapter?.submitList(getMapListFromMap(question.answers).toMutableList())

        }

        private fun getMapListFromMap(
            answers: Map<String, Any>,
            selected: String? = null,
            correct: String? = null
        ): List<QuestionOption> {
            return answers.map {
                it.toQuestionOption(selected, correct)
            }.toList()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionPageView {
        val item = ItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionPageView(item, onQuestionSelect)
    }

    override fun onBindViewHolder(holder: QuestionPageView, position: Int) {
        holder.bind(getItem(position))
    }
}

object QuestionDiffUtil : DiffUtil.ItemCallback<Question>() {
    override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean = false

    override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean = false
}
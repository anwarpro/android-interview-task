package com.check24.codingchallenge.ui.quiz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.check24.codingchallenge.R
import com.check24.codingchallenge.data.domain.models.QuestionOption
import com.check24.codingchallenge.databinding.ItemQuizOptionBinding

class QuestionOptionAdapter(
    private val onOptionSelected: (QuestionOption) -> Unit
) : ListAdapter<QuestionOption, QuestionOptionAdapter.QuestionOptionView>(
    QuestionOptionDiffUtil
) {
    inner class QuestionOptionView(
        private val itemBinding: ItemQuizOptionBinding,
        private val onOptionSelected: (QuestionOption) -> Unit
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private var currentOption: QuestionOption? = null

        init {
            itemBinding.root.setOnClickListener {
                currentOption?.let {
                    if (it.selected == null) {
                        onOptionSelected(it)
                    }
                }
            }
        }

        fun bind(option: QuestionOption) {
            currentOption = option
            itemBinding.textViewOption.text = "${option.option}"

            if (option.selected != null) {
                if (option.key == option.correct) {
                    itemBinding.root.background =
                        ContextCompat.getDrawable(
                            itemBinding.root.context,
                            R.drawable.question_option_bg_correct
                        )
                } else if (option.selected == option.key && option.selected != option.correct) {
                    itemBinding.root.background = ContextCompat.getDrawable(
                        itemBinding.root.context,
                        R.drawable.question_option_bg_wrong
                    )
                } else {
                    itemBinding.root.background = ContextCompat.getDrawable(
                        itemBinding.root.context,
                        R.drawable.question_option_bg
                    )
                }
            } else {
                itemBinding.root.background = ContextCompat.getDrawable(
                    itemBinding.root.context,
                    R.drawable.question_option_bg
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionOptionView {
        val item = ItemQuizOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionOptionView(item, onOptionSelected)
    }

    override fun onBindViewHolder(holder: QuestionOptionView, position: Int) {
        holder.bind(getItem(position))
    }
}

object QuestionOptionDiffUtil : DiffUtil.ItemCallback<QuestionOption>() {
    override fun areItemsTheSame(
        oldItem: QuestionOption,
        newItem: QuestionOption
    ): Boolean {
        return false
    }

    override fun areContentsTheSame(
        oldItem: QuestionOption,
        newItem: QuestionOption
    ): Boolean {
        return false
    }

}
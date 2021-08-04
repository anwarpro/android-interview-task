package com.check24.codingchallenge.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.check24.codingchallenge.databinding.FragmentQuizBinding
import com.check24.codingchallenge.ui.SharedViewModel
import com.check24.codingchallenge.ui.quiz.adapter.QuizPagerAdapter
import com.check24.codingchallenge.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass.
 * We use navigation controller to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class QuizFragment : Fragment() {
    private lateinit var adapter: QuizPagerAdapter
    private lateinit var binding: FragmentQuizBinding

    private val viewModel: QuizFragmentViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = QuizPagerAdapter {
            sharedViewModel.updateScore(it)

            //change page after few seconds
            viewLifecycleOwner.lifecycleScope.launch {
                delay(2000L)

                withContext(Dispatchers.Main) {
                    if (binding.viewPagerQuiz.currentItem + 1 < adapter.currentList.size) {
                        println("next page")
                        binding.viewPagerQuiz.currentItem = binding.viewPagerQuiz.currentItem + 1
                    } else {
                        println("back to main screen")
                        //all done back to main screen
                        findNavController().popBackStack()
                    }
                }
            }

        }

        binding.viewPagerQuiz.adapter = adapter
        binding.viewPagerQuiz.isUserInputEnabled = false

        sharedViewModel.score.observe(viewLifecycleOwner, {
            //update score
            binding.textViewQuizScore.text =
                "Frage ${binding.viewPagerQuiz.currentItem + 1}/${adapter.currentList.size} - Aktulee Punktzahi: ${it}"
        })

        viewModel.quizQuestions.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    binding.textViewQuizScore.isVisible = true
                }
                Resource.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.textViewQuizScore.isVisible = false
                }
                is Resource.Success -> {
                    val questions = it.data.questions
                    adapter.submitList(questions.toMutableList())

                    binding.progressBar.isVisible = false
                    binding.textViewQuizScore.isVisible = true
                }
            }
        })

    }
}
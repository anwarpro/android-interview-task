package com.check24.codingchallenge.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.check24.codingchallenge.R
import com.check24.codingchallenge.databinding.FragmentHomeBinding
import com.check24.codingchallenge.ui.SharedViewModel

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewHighScore.text = "Highscore \n${sharedViewModel.highScore.value} Punkte"

        binding.buttonStart.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_quizFragment)
        }
    }
}
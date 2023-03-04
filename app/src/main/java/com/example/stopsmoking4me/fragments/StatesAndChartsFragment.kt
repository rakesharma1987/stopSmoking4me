package com.example.stopsmoking4me.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.stopsmoking4me.R
import com.example.stopsmoking4me.databinding.FragmentStatesAndChartsBinding

// https://www.codingdemos.com/draw-android-line-chart/
class StatesAndChartsFragment : Fragment() {
    private lateinit var binding: FragmentStatesAndChartsBinding
    private var xAxisData = arrayOf("1/3/2023", "2/3/2023", "3/3/2023", "4/3/2023", "5/3/2023", "6/3/2023", "7/3/2023")
    private var yAxisData = arrayOf(1, 2, 3, 4, 5, 6, 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatesAndChartsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}
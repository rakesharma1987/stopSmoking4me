package com.example.stopsmoking4me.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.stopsmoking4me.R
import com.example.stopsmoking4me.databinding.FragmentStatesAndChartsBinding
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.*

class StatesAndChartsFragment : Fragment() {
    private lateinit var binding: FragmentStatesAndChartsBinding
    lateinit var lineGraphView: GraphView

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val series: LineGraphSeries<DataPoint> = LineGraphSeries(
            arrayOf(
                // on below line we are adding
                // each point on our x and y axis.
                DataPoint(Date("6/3/23"), 1.0),
                DataPoint(Date("7/3/23"), 2.0),
                DataPoint(Date("8/3/23"), 4.0),
                DataPoint(Date("9/3/23"), 5.0),
                DataPoint(Date("10/3/23"), 7.0),
                DataPoint(Date("11/3/23"), 8.0),
                DataPoint(Date("12/3/23"), 10.0),
            )
        )

        // on below line adding animation
        lineGraphView.animate()

        // on below line we are setting scrollable
        // for point graph view
        lineGraphView.viewport.isScrollable = true

        // on below line we are setting scalable.
        lineGraphView.viewport.isScalable = true

        // on below line we are setting scalable y
        lineGraphView.viewport.setScalableY(true)

        // on below line we are setting scrollable y
        lineGraphView.viewport.setScrollableY(true)

        // on below line we are setting color for series.
        series.color = R.color.purple_200

        // on below line we are adding
        // data series to our graph view.
        lineGraphView.addSeries(series)
    }
}
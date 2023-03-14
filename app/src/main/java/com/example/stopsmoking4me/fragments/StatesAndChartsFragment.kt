package com.example.stopsmoking4me.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.stopsmoking4me.databinding.FragmentStatesAndChartsBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class StatesAndChartsFragment : Fragment() {
    private lateinit var binding: FragmentStatesAndChartsBinding

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
        var xAxis: XAxis = binding.activityMainLinechart.xAxis
//        xAxis.valueFormatter

//        setLineChartData()

        }
    private fun setLineChartData(yesLine: ArrayList<Entry>, noLine: ArrayList<Entry>){
        var dataSet: ArrayList<LineDataSet> = arrayListOf()

        val yesLineDataSet = LineDataSet(yesLine, "Yes")
        yesLineDataSet.setDrawCircleHole(true)
        yesLineDataSet.setCircleRadius(4f)
        yesLineDataSet.setDrawValues(false)
        yesLineDataSet.lineWidth = 3f
        yesLineDataSet.setColor(Color.GREEN)
        yesLineDataSet.setCircleColor(Color.GREEN)
        dataSet.add(yesLineDataSet)

        val noLineDataSet = LineDataSet(noLine, "No")
        noLineDataSet.setDrawCircleHole(true)
        noLineDataSet.setCircleRadius(4f)
        noLineDataSet.setDrawValues(false)
        noLineDataSet.lineWidth = 3f
        noLineDataSet.setColor(Color.RED)
        noLineDataSet.setCircleColor(Color.RED)
        dataSet.add(noLineDataSet)


        val lineData = LineData(dataSet as List<ILineDataSet>?)
        binding.activityMainLinechart.data = lineData
        binding.activityMainLinechart.invalidate()


    }
}
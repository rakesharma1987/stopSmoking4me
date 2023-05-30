package com.example.stopsmoking4me.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.room.paging.util.queryItemCount
import com.example.stopsmoking4me.MainActivity
import com.example.stopsmoking4me.R
import com.example.stopsmoking4me.databinding.FragmentStatesAndChartsBinding
import com.example.stopsmoking4me.model.Reason
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.google.gson.internal.bind.ArrayTypeAdapter
import java.text.SimpleDateFormat

class StatesAndChartsFragment : Fragment() {
    private lateinit var binding: FragmentStatesAndChartsBinding
    private var dataPoints = ArrayList<Reason>()
    private lateinit var chart: LineChart
    private lateinit var dropDownAdapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataPoints = (requireActivity() as MainActivity).reasonList

        dropDownAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, resources.getStringArray(R.array.drop_down))
        Log.d("START_DATE", "startDate: $dataPoints")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatesAndChartsBinding.inflate(layoutInflater, container, false)
        chart = binding.chart
        binding.spiner.adapter = dropDownAdapter

//        reasonList = (requireActivity() as MainActivity).reasonList
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLineChartDataForYesNo()
//        setLineChartDataForReason()

        }
    private fun setLineChartDataForYesNo(){
        if (dataPoints.isEmpty()) return

        // Create entries for the chart
        val entries = mutableListOf<Entry>()
        val entriesNo = mutableListOf<Entry>()
        val dates = mutableListOf<String>()

        for (i in dataPoints.indices) {
            val dataPoint = dataPoints[i]
            val date = dataPoint.dateString
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val dateNew = formatter.parse(date)
            val desiredFormat = SimpleDateFormat("MMM-dd").format(dateNew)
            val countYes = countOccurrences(dataPoints, date, true)
            val countNo = countOccurrences(dataPoints, date, false)
            entries.add(Entry(i.toFloat(), countYes.toFloat()))
            entriesNo.add(Entry(i.toFloat(), countNo.toFloat()))
            dates.add(desiredFormat)
        }

        // Sort entries by X-axis value
        entries.sortWith(EntryXComparator())

        // Create a LineDataSet for yes counts
        val yesDataSet = LineDataSet(entries, "Yes")
        yesDataSet.color = Color.GREEN

        // Create a LineDataSet for no counts
        val noDataSet = LineDataSet(entriesNo, "No")
        noDataSet.color = Color.RED

        // Combine the datasets
        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(yesDataSet)
        dataSets.add(noDataSet)

        // Create a LineData object with the datasets
        val lineData = LineData(dataSets)

        // Customize the X-axis
        val xAxis: XAxis = chart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(dates)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f

        // Set the data to the chart and refresh
        chart.data = lineData
        chart.animateXY(300, 300)
        chart.invalidate()

    }

    // Function to count occurrences of yes or no for a specific date
    private fun countOccurrences(dataPoints: List<Reason>, date: String, isYes: Boolean): Int {
        var count = 0
        for (dataPoint in dataPoints) {
            if (dataPoint.dateString == date && dataPoint.yesOrNo == isYes) {
                count++
            }
        }
        return count
    }
}
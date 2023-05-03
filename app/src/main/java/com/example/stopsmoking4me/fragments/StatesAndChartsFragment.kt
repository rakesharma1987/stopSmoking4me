package com.example.stopsmoking4me.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class StatesAndChartsFragment : Fragment() {
    private lateinit var binding: FragmentStatesAndChartsBinding
    private var reasonList = ArrayList<Reason>()
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatesAndChartsBinding.inflate(layoutInflater, container, false)

        reasonList = (requireActivity() as MainActivity).reasonList
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLineChartDataForYesNo()
        setLineChartDataForReason()

        }
    var uniqueDateToList = ArrayList<String>()
    var trueCountList = ArrayList<String>()
    var falseCountList = ArrayList<String>()
    private fun setLineChartDataForYesNo(){

        val uniqueDate = reasonList.map {
            it.date
        }.toSet()


        uniqueDateToList = uniqueDate.toList() as ArrayList<String>

        trueCountList = uniqueDate.map { date ->
            reasonList.filter { it.date == date && it.yesOrNo }.count().toString()
        } as ArrayList<String>

        falseCountList = uniqueDate.map { date ->
            reasonList.filter { it.date == date && !it.yesOrNo }.count().toString()
        } as ArrayList<String>


        var xValue = ArrayList<String>()
//        xValue.add("01/03/2023")
//        xValue.add("02/03/2023")
//        xValue.add("03/03/2023")
//        xValue.add("04/03/2023")
//        xValue.add("05/03/2023")
//        xValue.add("06/03/2023")
//        xValue.add("07/03/2023")
        xValue = uniqueDateToList

        val lineEntryYes = ArrayList<Entry>()
//        lineEntryYes.add(Entry(10f, 0))
//        lineEntryYes.add(Entry(20f, 1))
//        lineEntryYes.add(Entry(30f, 3))
//        lineEntryYes.add(Entry(40f, 4))
//        lineEntryYes.add(Entry(50f, 5))
        var count = 0
        for (dataYes in trueCountList){
            lineEntryYes.add(Entry(dataYes.toFloat(), count))
            count++
        }

        val lineEntryNo = ArrayList<Entry>()
//        lineEntryNo.add(Entry(13f, 0))
//        lineEntryNo.add(Entry(22f, 1))
//        lineEntryNo.add(Entry(33f, 3))
//        lineEntryNo.add(Entry(44f, 4))
//        lineEntryNo.add(Entry(53f, 5))
        var countNo = 0
        for (dataYes in falseCountList){
            lineEntryYes.add(Entry(dataYes.toFloat(), countNo))
            countNo++
        }

//        for (item in reasonList){
//                var yes = 0
//                var no = 0
//                var countYes = 0
//                var countNo = 0
//
//                if (item.yesOrNo){
//                    countYes++
//                    yes = countYes
//                }else{
//                    countNo++
//                    no = countNo
//                }
//
//            if (!xValue.contains(item.date)){
//                xValue.add(item.date)
//                lineEntryYes.add(Entry(yes.toFloat(), count))
//                lineEntryNo.add(Entry(no.toFloat(), count))
//                yes = 0
//                no = 0
//                countYes = 0
//                countNo = 0
//                count++
//            }
//
//        }

        val lineDataSet = LineDataSet(lineEntryYes, "Yes")
        lineDataSet.color = resources.getColor(R.color.purple_500)

        val lineDataSet1 = LineDataSet(lineEntryNo, "No")
        lineDataSet1.color = resources.getColor(R.color.teal_200)

        val finalDataSet = ArrayList<LineDataSet>()
        finalDataSet.add(lineDataSet)
        finalDataSet.add(lineDataSet1)

        val data = LineData(xValue, finalDataSet as List<ILineDataSet>?)
        binding.activityMainLinechart.data = data
        binding.activityMainLinechart.setBackgroundColor(resources.getColor(R.color.white))
        binding.activityMainLinechart.animateXY(3000, 3000)

    }
    private fun setLineChartDataForReason(){
        val xValue = ArrayList<String>()
        xValue.add("01/03/2023")
        xValue.add("02/03/2023")
        xValue.add("03/03/2023")
        xValue.add("04/03/2023")
        xValue.add("05/03/2023")
        xValue.add("06/03/2023")
        xValue.add("07/03/2023")

        val lineEntryYes = ArrayList<Entry>()
        lineEntryYes.add(Entry(10f, 0))
        lineEntryYes.add(Entry(20f, 1))
        lineEntryYes.add(Entry(30f, 3))
        lineEntryYes.add(Entry(40f, 4))
        lineEntryYes.add(Entry(50f, 5))

        val lineEntryNo = ArrayList<Entry>()
        lineEntryNo.add(Entry(13f, 0))
        lineEntryNo.add(Entry(22f, 1))
        lineEntryNo.add(Entry(33f, 3))
        lineEntryNo.add(Entry(44f, 4))
        lineEntryNo.add(Entry(53f, 5))

        val lineDataSet = LineDataSet(lineEntryYes, "Yes")
        lineDataSet.color = resources.getColor(R.color.purple_500)

        val lineDataSet1 = LineDataSet(lineEntryNo, "No")
        lineDataSet1.color = resources.getColor(R.color.teal_200)

        val finalDataSet = ArrayList<LineDataSet>()
        finalDataSet.add(lineDataSet)
        finalDataSet.add(lineDataSet1)

        val data = LineData(xValue, finalDataSet as List<ILineDataSet>?)
        binding.activityMainLinechart1.data = data
        binding.activityMainLinechart1.setBackgroundColor(resources.getColor(R.color.white))
        binding.activityMainLinechart1.animateXY(3000, 3000)

    }
}
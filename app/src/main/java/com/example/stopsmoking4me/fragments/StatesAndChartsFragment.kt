package com.example.stopsmoking4me.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.paging.util.queryItemCount
import com.example.stopsmoking4me.MainActivity
import com.example.stopsmoking4me.R
import com.example.stopsmoking4me.adapter.StatesRecyclerViewAdapter
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

class StatesAndChartsFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentStatesAndChartsBinding
    private var dataPoints = ArrayList<Reason>()
    private lateinit var chart: LineChart
    private lateinit var dropDownAdapter: ArrayAdapter<String>
    private lateinit var reasonList: ArrayList<Reason>
    private lateinit var adapterData: StatesRecyclerViewAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dropDownAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, resources.getStringArray(R.array.drop_down))
//        adapterData = StatesRecyclerViewAdapter(requireContext(), (requireActivity() as MainActivity).dbAdapter.getOneDayAnalytics())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatesAndChartsBinding.inflate(layoutInflater, container, false)
        binding.spiner.adapter = dropDownAdapter
        binding.spiner.onItemSelectedListener = this

        var layoutManager = LinearLayoutManager(context)
        binding.rvData.layoutManager = layoutManager
        adapterData = StatesRecyclerViewAdapter(requireContext(), (requireActivity() as MainActivity).dbAdapter.getOneDayAnalytics())
        binding.rvData.adapter = adapterData

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        }

    // Function to count occurrences of yes or no for a specific date
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var value = parent?.getItemAtPosition(position).toString()
        if (value == "One day"){
            var list = (requireActivity() as MainActivity).dbAdapter.getOneDayAnalytics()
            adapterData = StatesRecyclerViewAdapter(requireContext(), list)
            binding.rvData.adapter = adapterData
            Log.d("XYZ", "onItemSelected: $list")
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}
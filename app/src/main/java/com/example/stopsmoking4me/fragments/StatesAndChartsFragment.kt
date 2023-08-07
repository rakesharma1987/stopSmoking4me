package com.example.stopsmoking4me.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stopsmoking4me.MainActivity
import com.example.stopsmoking4me.adapter.DayWiseSmokeCounterAdapter
import com.example.stopsmoking4me.adapter.SmokCountPercentageAdapter
import com.example.stopsmoking4me.adapter.StatesRecyclerViewAdapter
import com.stopsmokingforfamily.aityl.R
import com.stopsmokingforfamily.aityl.databinding.FragmentStatesAndChartsBinding

class StatesAndChartsFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentStatesAndChartsBinding
    private lateinit var dropDownAdapter: ArrayAdapter<String>
    private lateinit var adapterData: StatesRecyclerViewAdapter
    private lateinit var smokedCountPercentageAdapter: SmokCountPercentageAdapter
    private lateinit var dayWiseSmokeCounterAdapter: DayWiseSmokeCounterAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dropDownAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, resources.getStringArray(
            R.array.drop_down))
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
        var layoutManager1 = LinearLayoutManager(context)
        var layoutManager2 = LinearLayoutManager(context)

        binding.rvData.layoutManager = layoutManager
        binding.rvData1.layoutManager = layoutManager1
        binding.rvData2.layoutManager = layoutManager2

        adapterData = StatesRecyclerViewAdapter(requireContext(), (requireActivity() as MainActivity).dbAdapter.getOneDayAnalytics())
        smokedCountPercentageAdapter = SmokCountPercentageAdapter(requireContext(), (requireContext() as MainActivity).dbAdapter.getOneDaySmokedPercentageAnalytic())
        binding.rvData.adapter = adapterData
        binding.rvData1.adapter = smokedCountPercentageAdapter
        adapterData.notifyDataSetChanged()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        }

    // Function to count occurrences of yes or no for a specific date
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var value = parent?.getItemAtPosition(position).toString()
        if (value == "Last 1 Day"){
            binding.tvCount.text = "Count"
            binding.rlReason2.visibility = View.GONE
            binding.rlTotalNoOfDays.visibility = View.GONE
            var list = (requireActivity() as MainActivity).dbAdapter.getOneDayAnalytics()
            adapterData = StatesRecyclerViewAdapter(requireContext(), list)
            binding.rvData.adapter = adapterData
            smokedCountPercentageAdapter = SmokCountPercentageAdapter(requireContext(), (requireContext() as MainActivity).dbAdapter.getOneDaySmokedPercentageAnalytic())
            binding.rvData1.adapter = smokedCountPercentageAdapter
            adapterData.notifyDataSetChanged()
        }
        if (value == "Last 7 Days"){
            binding.rlReason2.visibility = View.VISIBLE
            binding.tvDate3.visibility = View.GONE
            binding.rlTotalNoOfDays.visibility = View.GONE
            adapterData = StatesRecyclerViewAdapter(requireContext(), (requireActivity() as MainActivity).dbAdapter.getSevenDaysAnalytics())
            binding.rvData.adapter = adapterData
            adapterData.notifyDataSetChanged()

            smokedCountPercentageAdapter = SmokCountPercentageAdapter(requireContext(), (requireContext() as MainActivity).dbAdapter.getSevenDaysSmokePercentageAnalytic())
            binding.rvData1.adapter = smokedCountPercentageAdapter
            smokedCountPercentageAdapter.notifyDataSetChanged()

            dayWiseSmokeCounterAdapter = DayWiseSmokeCounterAdapter(requireContext(), (requireActivity() as MainActivity).dbAdapter.getSevenDaysDayWiseAnalytics())
            binding.rvData2.adapter = dayWiseSmokeCounterAdapter
            dayWiseSmokeCounterAdapter.notifyDataSetChanged()

        }
        if (value == "Last 30 Days"){
            binding.rlReason2.visibility = View.VISIBLE
            binding.tvDate3.visibility = View.GONE
            binding.rlTotalNoOfDays.visibility = View.GONE
            adapterData = StatesRecyclerViewAdapter(requireContext(), (requireActivity() as MainActivity).dbAdapter.get30DaysAnalytics())
            binding.rvData.adapter = adapterData
            adapterData.notifyDataSetChanged()

            smokedCountPercentageAdapter = SmokCountPercentageAdapter(requireContext(), (requireContext() as MainActivity).dbAdapter.get30DaysSmokePercentageAnalytic())
            binding.rvData1.adapter = smokedCountPercentageAdapter
            smokedCountPercentageAdapter.notifyDataSetChanged()

            dayWiseSmokeCounterAdapter = DayWiseSmokeCounterAdapter(requireContext(), (requireActivity() as MainActivity).dbAdapter.get30DaysDayWiseAnalytics())
            binding.rvData2.adapter = dayWiseSmokeCounterAdapter
            dayWiseSmokeCounterAdapter.notifyDataSetChanged()

        }

        if (value == "All"){
            binding.rlReason2.visibility = View.VISIBLE
            binding.tvDate3.visibility = View.GONE
            binding.rlTotalNoOfDays.visibility = View.VISIBLE
            binding.tvTotalDays.text = "Total No Of Days - ${(requireActivity() as MainActivity).dbAdapter.getTotalNoOfDays().toString()}"
            adapterData = StatesRecyclerViewAdapter(requireContext(), (requireActivity() as MainActivity).dbAdapter.getLifeTimeAnalytics())
            binding.rvData.adapter = adapterData
            adapterData.notifyDataSetChanged()

            smokedCountPercentageAdapter = SmokCountPercentageAdapter(requireContext(), (requireContext() as MainActivity).dbAdapter.getLifetimeAnalyticsCountPercentage())
            binding.rvData1.adapter = smokedCountPercentageAdapter
            smokedCountPercentageAdapter.notifyDataSetChanged()

            dayWiseSmokeCounterAdapter = DayWiseSmokeCounterAdapter(requireContext(), (requireActivity() as MainActivity).dbAdapter.getLifetimeAnalyticsDayWise())
            binding.rvData2.adapter = dayWiseSmokeCounterAdapter
            dayWiseSmokeCounterAdapter.notifyDataSetChanged()

        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}
package com.example.stopsmoking4me.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stopsmoking4me.model.HourlyAnalyticData
import com.stopsmokingforfamily.aityl.R
import com.stopsmokingforfamily.aityl.databinding.LayoutDataItemsBinding

class HourlyAnalyticAdapter(private val context: Context, private val listItem: List<HourlyAnalyticData>):
    RecyclerView.Adapter<HourlyAnalyticAdapter.CustomViewHolder>(){
    inner class CustomViewHolder(val layoutItemBinding: LayoutDataItemsBinding): RecyclerView.ViewHolder(layoutItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding: LayoutDataItemsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_data_items, parent, false)
            binding.tvSrNo.visibility = View.GONE
            binding.tvDate.visibility = View.GONE
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//        holder.layoutItemBinding.tvSrNo.text = (position + 1).toString()
//        holder.layoutItemBinding.tvSrNo.text = listItem[position].sno.toString()
//        holder.layoutItemBinding.tvSrNo.visibility = View.GONE
//        holder.layoutItemBinding.tvDate.text = listItem[position].sno.toString()
        holder.layoutItemBinding.tvDay.text = listItem[position].hour
        holder.layoutItemBinding.tvReason.text = listItem[position].reason
        holder.layoutItemBinding.tvCount.text = listItem[position].count.toString()
        holder.layoutItemBinding.tvPercentage.text = listItem[position].percentage
    }

    override fun getItemCount(): Int {
        return listItem.size
    }
}
package com.example.stopsmoking4me.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stopsmoking4me.model.SmokeCountAndPercentage
import com.stopsmoking4me.aitylgames.R
import com.stopsmoking4me.aitylgames.databinding.LayoutDataItemsBinding

class SmokCountPercentageAdapter(private val context: Context, private val listItem: List<SmokeCountAndPercentage>):
    RecyclerView.Adapter<SmokCountPercentageAdapter.CustomViewHolder>(){
    inner class CustomViewHolder(val layoutItemBinding: LayoutDataItemsBinding): RecyclerView.ViewHolder(layoutItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding: LayoutDataItemsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_data_items, parent, false)
        binding.tvSrNo.visibility = View.GONE
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.layoutItemBinding.tvSrNo.text = (position + 1).toString()
        holder.layoutItemBinding.tvDate.text = listItem[position].totalAttemts
        holder.layoutItemBinding.tvDay.text = listItem[position].smokedCount
        holder.layoutItemBinding.tvReason.text = listItem[position].noSmokedCount
        holder.layoutItemBinding.tvCount.text = listItem[position].smokedPercentage
        holder.layoutItemBinding.tvPercentage.text = listItem[position].noSmokedPercentage
    }

    override fun getItemCount(): Int {
        return listItem.size
    }
}
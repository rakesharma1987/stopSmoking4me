package com.example.stopsmoking4me.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stopsmoking4me.model.Messages
import com.stopsmoking4me.aitylgames.R
import com.stopsmoking4me.aitylgames.databinding.LayoutItemBinding

class MyRecyclerviewAdapter(private val context: Context, private val listItem: List<Messages>):
RecyclerView.Adapter<MyRecyclerviewAdapter.CustomViewHolder>(){
    inner class CustomViewHolder(val layoutItemBinding: LayoutItemBinding):RecyclerView.ViewHolder(layoutItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding: LayoutItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
        R.layout.layout_item, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.layoutItemBinding.tvItem.text = listItem[position].msg
    }

    override fun getItemCount(): Int {
        return listItem.size
    }
}
package com.example.stopsmoking4me.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stopsmoking4me.MainActivity
import com.example.stopsmoking4me.model.Quotes
import com.stopsmoking4me.aitylgames.R
import com.stopsmoking4me.aitylgames.databinding.LayoutQuotesItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuotesRecyclerViewAdapter(private val context: Context):
    RecyclerView.Adapter<QuotesRecyclerViewAdapter.CustomViewHolder>(){
    var listItem: List<Quotes> = mutableListOf()
    fun setData(listItem: List<Quotes>){
        this.listItem = listItem
        notifyDataSetChanged()
    }

    inner class CustomViewHolder(val layoutItemBinding: LayoutQuotesItemBinding): RecyclerView.ViewHolder(layoutItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding: LayoutQuotesItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_quotes_item, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.layoutItemBinding.tvQuotes.text = listItem[position].quotes
        """${listItem[position].author}""".also { holder.layoutItemBinding.tvAuther.text = it }
        holder.layoutItemBinding.ivDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                (context as MainActivity).viewModel.deleteQuotes(listItem[position].id)
            }
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }
}
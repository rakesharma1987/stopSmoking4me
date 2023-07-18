package com.example.stopsmoking4me.adapter

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.ProductDetails
import com.example.stopsmoking4me.OnCLickProduct
import com.example.stopsmoking4me.R


class ProductDetailAdapter(private val onCLickProduct: OnCLickProduct,
                           var context: Context, var productList: List<ProductDetails.SubscriptionOfferDetails>?,
                           private val productDetails: ProductDetails
) : RecyclerView.Adapter<ProductDetailAdapter.ViewHolder>(){
    private val array = SparseBooleanArray()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subscriptionOfferDetails = productList!![position]
        val subProductList = subscriptionOfferDetails.pricingPhases.pricingPhaseList[0].formattedPrice
        var price  = subProductList.toString()

        if(subscriptionOfferDetails.basePlanId.equals("month12")){
            holder.llItem.visibility = View.VISIBLE
            holder.type.text = "/Yearly"
            holder.grace.text = "Auto Renewed"
            holder.price.text = price.replace(".00","")
        }else if(subscriptionOfferDetails.basePlanId.equals("month") && price.equals("Free")){
            holder.llItem.visibility = View.VISIBLE
            holder.type.text = "/Monthly"
            holder.grace.text = "3 days Free Trial, Auto Renewed"
            holder.price.text = "30"
        }else{
            holder.llItem.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onCLickProduct.onClickItem(productDetails,subscriptionOfferDetails)
        }
      //  holder.duration.text = apps.

    }

    override fun getItemCount(): Int {
        return productList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    // Holds the views for adding it to text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val type: TextView = itemView.findViewById(R.id.type)
        val price: TextView = itemView.findViewById(R.id.price)
        val grace: TextView = itemView.findViewById(R.id.tv_grace)
        val llItem: LinearLayout = itemView.findViewById(R.id.ll_item)
    }

}
package com.example.stopsmoking4me

import com.android.billingclient.api.ProductDetails

interface OnCLickProduct {
    fun onClickItem (productDetails: ProductDetails, subscriptionOfferDetails: ProductDetails.SubscriptionOfferDetails)
}
package com.example.productcrud

import com.google.gson.annotations.SerializedName

data class ProductResponse (
    @SerializedName("products") var products: ArrayList<Product>
)

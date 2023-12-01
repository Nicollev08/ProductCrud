package com.example.productcrud

data class Product(
    var id: Int,
    var name: String,
    var description: String,
    var price: Double,
    var quantity: Int,
    var status: Int,
    var subcategory_id: Int,
)

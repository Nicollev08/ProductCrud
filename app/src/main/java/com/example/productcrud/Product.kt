package com.example.productcrud

data class Product(
    var id: Int,
    var name: String,
    var description: String,
    var image_path: String,
    var price: Double,
    var quantity: Int,
    var status: Int,
    var subcategory_id: Int
)

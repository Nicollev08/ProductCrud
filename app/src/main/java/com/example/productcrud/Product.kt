package com.example.productcrud

data class Product(
    var id: Int,
    var name: String,
    var description: String,
    var image_path: String,
    var price: Double,
    var quantity: Int,
    var status: Int,
    var subcategory_id: Int,

){
    fun updateFromForm(
        name: String,
        description: String,
        price: Double,
        quantity: Int,
        status: Int,
        subcategoryId: Int
    ) {
        this.name = name
        this.description = description
        this.price = price
        this.quantity = quantity
        this.status = status
        this.subcategory_id = subcategoryId
    }

}

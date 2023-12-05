package com.example.productcrud

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("description") var description: String,
    @SerializedName("image_path") var image_path: String,  // Asegúrate de que este tipo sea correcto
    @SerializedName("price") var price: Double,
    @SerializedName("quantity") var quantity: Int,
    @SerializedName("status") var status: Int,
    @SerializedName("subcategory_id") var subcategory_id: Int
) {
    constructor() : this(0, "", "", "", 0.0, 0, 0, 0)


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

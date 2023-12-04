package com.example.productcrud

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiProducts {

    @GET("/api/products")
    suspend fun getProducts(): List<Product>

    @Multipart
    @POST("/api/products")
    fun createProduct(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("price") price: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("status") status: RequestBody,
        @Part("subcategory_id") subcategoryId: RequestBody
    ): Call<Product>



    @PUT("/api/products/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body body:JsonObject): Response<JsonObject>

    @DELETE("/api/products/{id}")
    suspend fun deleteProduct(@Path("id") productId: Int): Response<Unit>


}
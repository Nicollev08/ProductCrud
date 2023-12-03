package com.example.productcrud

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiProducts {

    @GET("/api/products")
    suspend fun getProducts(): List<Product>

    @POST("/api/products")
    suspend fun createProduct(@Body product: Product): Response<Product>

    @PUT("/api/products/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body product: Product): Response<Product>


    @DELETE("/api/products/{id}")
    suspend fun deleteProduct(@Path("id") productId: Int): Response<Unit>


}
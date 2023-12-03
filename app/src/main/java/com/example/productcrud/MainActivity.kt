package com.example.productcrud

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var apiProducts: ApiProducts
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiProducts = RetrofitHelper.getInstance().create(ApiProducts::class.java)
        recyclerView = findViewById(R.id.recyclerView)

        // Configura el RecyclerView y el adaptador
        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(emptyList()) // Puedes inicializar con una lista vacía
        recyclerView.adapter = productAdapter

        // Realiza la carga inicial de productos
        getProducts()

    }

    private fun getProducts() {
        lifecycleScope.launch {
            // Tu lógica para obtener la lista de productos desde la API
            try {
                val productList = apiProducts.getProducts()
                productAdapter = ProductAdapter(productList)
                recyclerView.adapter = productAdapter
            } catch (e: Exception) {
                Log.e("ooooo", "getProducts Exception: ${e.message}")
            }
        }
    }


}
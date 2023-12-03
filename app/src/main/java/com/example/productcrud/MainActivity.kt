package com.example.productcrud

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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
        productAdapter = ProductAdapter(emptyList())
        recyclerView.adapter = productAdapter

        // Realiza la carga inicial de productos
        getProducts()

        val btnCreateProduct: Button = findViewById(R.id.buttonCreateProduct)
        btnCreateProduct.setOnClickListener {
            showCreateProductDialog()
        }
    }

    private fun getProducts() {
        lifecycleScope.launch {
            try {
                val productList = apiProducts.getProducts()
                productAdapter.updateProductList(productList)
            } catch (e: Exception) {
                Log.e("ooooo", "getProducts Exception: ${e.message}")
            }
        }
    }

    private fun showCreateProductDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_main)

        val nameEditText = dialog.findViewById<EditText>(R.id.editTextName)
        val descriptionEditText = dialog.findViewById<EditText>(R.id.editTextDescription)
        val priceEditText = dialog.findViewById<EditText>(R.id.editTextPrice)
        val quantityEditText = dialog.findViewById<EditText>(R.id.editTextQuantity)
        val statusEditText = dialog.findViewById<EditText>(R.id.editTextStatus)
        val subcategoryIdEditText = dialog.findViewById<EditText>(R.id.editTextSubcategoryId)
        val btnCreateProduct = dialog.findViewById<Button>(R.id.buttonCreateProduct)

        btnCreateProduct.setOnClickListener {
            val name = nameEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val price = priceEditText.text.toString().toDouble()
            val quantity = quantityEditText.text.toString().toInt()
            val status = statusEditText.text.toString().toInt()
            val subcategoryId = subcategoryIdEditText.text.toString().toIntOrNull() ?: 0

            val newProduct = Product(
                id = 0,
                name = name,
                description = description,
                price = price,
                quantity = quantity,
                status = status,
                image_path = "",
                subcategory_id = subcategoryId
            )

            createProduct(newProduct)

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun createProduct(product: Product) {
        lifecycleScope.launch {
            try {
                val response = apiProducts.createProduct(product)
                if (response.isSuccessful) {
                    getProducts()
                } else {
                    Log.e("ooooo", "createProduct Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("ooooo", "createProduct Exception: ${e.message}")
            }
        }
    }

}
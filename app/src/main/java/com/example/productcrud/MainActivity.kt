package com.example.productcrud

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.google.gson.JsonObject
import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var apiProducts: ApiProducts
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        apiProducts =  RetrofitHelper.getInstance().create(ApiProducts::class.java)

        findViewById<Button>(R.id.btnGet).setOnClickListener{
            getProducts()
        }
        findViewById<Button>(R.id.btnGetId).setOnClickListener{
            getProductById("6")
        }
        findViewById<Button>(R.id.btnUpdate).setOnClickListener{
            updateProduct()
        }
        findViewById<Button>(R.id.btnDelete).setOnClickListener{
            deleteProduct()
        }
        findViewById<Button>(R.id.btnCreate).setOnClickListener{
            createProduct()
        }
    }

    private fun getProducts(){
        lifecycleScope.launch {
            showLoading("Espere por favor")

            try {
                val productList = apiProducts.getProducts()

                // Check if the list is not null or empty
                if (productList.isNullOrEmpty()) {
                    Log.e("ooooo", "getProducts Error: Empty product list")
                } else {
                    Log.e("ooooo", "getProducts Success: $productList")
                }
            } catch (e: Exception) {
                Log.e("ooooo", "getProducts Exception: ${e.message}")
            } finally {
                progressDialog?.dismiss()
            }
        }
    }

    private fun getProductById(productId: String) {
        lifecycleScope.launch {
            showLoading("Espere por favor")

            try {
                val response = apiProducts.getProductById(productId)

                if (response.isSuccessful) {
                    val productResponse = response.body()

                    if (productResponse != null) {
                        Log.e("ooooo", "getProductById Success: $productResponse")
                    } else {
                        Log.e("ooooo", "getProductById Error: Response body is null")
                    }
                } else {
                    Log.e("ooooo", "getProductById Error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ooooo", "getProductById Exception: ${e.message}")
            } finally {
                progressDialog?.dismiss()
            }
        }
    }

    private fun updateProduct(){
        lifecycleScope.launch {
            showLoading("Actualizando")
            val body = JsonObject().apply {

                addProperty("name", "laura")
                addProperty("description", "nicole")
                addProperty("price", 123)
                addProperty("quantity", 123)
                addProperty("status", 1)
                addProperty("subcategory_id", 1)

            }
            val result = apiProducts.updateProduct("10", body)
            if(result.isSuccessful){
                Log.e("ooooo", "updateProduct Success: ${result.body()}")
            }else{
                Log.e("ooooo", "updateProductById Field: ${result.message()}")
            }
            progressDialog?.dismiss()
        }
    }

    private fun deleteProduct(){
        lifecycleScope.launch{
            showLoading("eliminando...")
            val result = apiProducts.deleteProduct("17")
            if(result.isSuccessful){
                Log.e("ooooo", "deleteProduct Success ${result.body()}")
            }else{
                Log.e("ooooo", "deleteProduct Field ${result.message()}")
            }
            progressDialog?.dismiss()
        }
    }

    private fun createProduct(){
        lifecycleScope.launch{
            showLoading("creando...")
            val body = JsonObject().apply {
                addProperty("name", "GERMANNNN")
                addProperty("description", "nicole")
                addProperty("price", 321)
                addProperty("quantity", 123)
                addProperty("status", 1)
                addProperty("subcategory_id", 1)
            }
            val result = apiProducts.createProduct(body)
            if(result.isSuccessful){
                Log.e("ooooo", "createProduct Success ${result.body()}")
            }else{
                Log.e("ooooo", "createProduct Failed ${result.message()}")
            }
            progressDialog?.dismiss()
        }
    }

    private fun showLoading(msg: String){
        progressDialog = ProgressDialog.show(this, null, msg, true)
    }
}
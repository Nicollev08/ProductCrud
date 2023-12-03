package com.example.productcrud

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

        // Configura el listener para manejar la eliminación del producto
        productAdapter = ProductAdapter(emptyList()) { product ->
            showDeleteConfirmationDialog(product.id)
        }
        recyclerView.adapter = productAdapter

        // Realiza la carga inicial de productos
        getProducts()


        val btnSave = findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            // Lógica para obtener datos del formulario y crear un nuevo producto
            createProductFromForm()
        }
    }

    private fun getProducts() {
        lifecycleScope.launch {
            try {
                val productList = apiProducts.getProducts()
                productAdapter.updateData(productList)
            } catch (e: Exception) {
                Log.e("ooooo", "getProducts Exception: ${e.message}")
            }
        }
    }

    private fun showDeleteConfirmationDialog(productId: Int) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar producto")
            .setMessage("¿Estás seguro de que deseas eliminar este producto?")
            .setPositiveButton("Sí") { _, _ ->
                deleteProduct(productId)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteProduct(productId: Int) {
        lifecycleScope.launch {
            try {
                val response = apiProducts.deleteProduct(productId)

                if (response.isSuccessful) {
                    // Actualiza la lista después de la eliminación
                    getProducts()
                    Toast.makeText(applicationContext, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Error al eliminar el producto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ooooo", "deleteProduct Exception: ${e.message}")
                Toast.makeText(applicationContext, "Error al eliminar el producto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createProductFromForm() {
        val nameEditText = findViewById<EditText>(R.id.editTextName)
        val descriptionEditText = findViewById<EditText>(R.id.editTextDescription)
        val priceEditText = findViewById<EditText>(R.id.editTextPrice)
        val quantityEditText = findViewById<EditText>(R.id.editTextQuantity)
        val statusEditText = findViewById<EditText>(R.id.editTextStatus)
        val subcategoryIdEditText = findViewById<EditText>(R.id.editTextSubcategoryId)

        val newProduct = Product(
            id = 0,  // Puedes establecer un valor predeterminado para el ID o manejarlo de otra manera
            name = nameEditText.text.toString(),
            description = descriptionEditText.text.toString(),
            image_path = "",  // Puedes manejar la lógica para la imagen según tu implementación
            price = priceEditText.text.toString().toDouble(),
            quantity = quantityEditText.text.toString().toInt(),
            status = statusEditText.text.toString().toInt(),
            subcategory_id = subcategoryIdEditText.text.toString().toInt()
        )

        // Llama al método para crear el producto
        createProduct(newProduct)
    }

    private fun createProduct(newProduct: Product) {
        lifecycleScope.launch {
            try {
                val response = apiProducts.createProduct(newProduct)

                if (response.isSuccessful) {
                    // Producto creado con éxito
                    // Puedes actualizar la lista de productos o realizar otras acciones si es necesario
                    getProducts()
                    Toast.makeText(applicationContext, "Producto creado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Error al crear el producto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ooooo", "createProduct Exception: ${e.message}")
                Toast.makeText(applicationContext, "Error al crear el producto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

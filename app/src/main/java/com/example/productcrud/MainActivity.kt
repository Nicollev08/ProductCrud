package com.example.productcrud

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import android.widget.EditText


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
        productAdapter = ProductAdapter(emptyList(),
            onEditClick = { product ->
                showEditDialog(product)
            },
            onDeleteClick = { product ->
                showDeleteConfirmationDialog(product.id)
            }
        )

        recyclerView.adapter = productAdapter

        // Realiza la carga de todos los productos
        getProducts()


        val btnCreate = findViewById<Button>(R.id.btnCreate)
        btnCreate.setOnClickListener {
            showEditDialog(null)
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

    private fun showEditDialog(product: Product?) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.form_product, null)

        val nameEditText = view.findViewById<EditText>(R.id.editTextName)
        val descriptionEditText = view.findViewById<EditText>(R.id.editTextDescription)
        val priceEditText = view.findViewById<EditText>(R.id.editTextPrice)
        val quantityEditText = view.findViewById<EditText>(R.id.editTextQuantity)
        val statusEditText = view.findViewById<EditText>(R.id.editTextStatus)
        val subcategoryIdEditText = view.findViewById<EditText>(R.id.editTextSubcategoryId)

        if (product != null) {
            setProductValuesInForm(
                product,
                nameEditText,
                descriptionEditText,
                priceEditText,
                quantityEditText,
                statusEditText,
                subcategoryIdEditText
            )
        }

        builder.setView(view)
            .setPositiveButton("Guardar Cambios") { _, _ ->
                if (product == null) {
                    createProductFromForm(
                        nameEditText,
                        descriptionEditText,
                        priceEditText,
                        quantityEditText,
                        statusEditText,
                        subcategoryIdEditText
                    )
                } else {
                    updateProductFromForm(
                        product,
                        nameEditText,
                        descriptionEditText,
                        priceEditText,
                        quantityEditText,
                        statusEditText,
                        subcategoryIdEditText
                    )
                }
            }
            .setNegativeButton("Cancelar", null)

        val dialog = builder.create()
        dialog.show()
    }



    private fun setProductValuesInForm(
        product: Product,
        nameEditText: EditText,
        descriptionEditText: EditText,
        priceEditText: EditText,
        quantityEditText: EditText,
        statusEditText: EditText,
        subcategoryIdEditText: EditText
    ) {
        nameEditText.setText(product.name)
        descriptionEditText.setText(product.description)
        priceEditText.setText(product.price.toString())
        quantityEditText.setText(product.quantity.toString())
        statusEditText.setText(product.status.toString())
        subcategoryIdEditText.setText(product.subcategory_id.toString())
    }

    private fun createProductFromForm(
        nameEditText: EditText,
        descriptionEditText: EditText,
        priceEditText: EditText,
        quantityEditText: EditText,
        statusEditText: EditText,
        subcategoryIdEditText: EditText
    ) {
        val newProduct = Product(
            id = 0,
            name = nameEditText.text.toString(),
            description = descriptionEditText.text.toString(),
            image_path = "",
            price = priceEditText.text.toString().toDouble(),
            quantity = quantityEditText.text.toString().toInt(),
            status = statusEditText.text.toString().toInt(),
            subcategory_id = subcategoryIdEditText.text.toString().toInt()
        )

        createProduct(newProduct)
    }

    private fun updateProductFromForm(
        product: Product,
        nameEditText: EditText,
        descriptionEditText: EditText,
        priceEditText: EditText,
        quantityEditText: EditText,
        statusEditText: EditText,
        subcategoryIdEditText: EditText
    ) {
        product.updateFromForm(
            nameEditText.text.toString(),
            descriptionEditText.text.toString(),
            priceEditText.text.toString().toDouble(),
            quantityEditText.text.toString().toInt(),
            statusEditText.text.toString().toInt(),
            subcategoryIdEditText.text.toString().toInt()
        )

        updateProduct(product)
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

    private fun updateProduct(product: Product) {
        lifecycleScope.launch {
            try {
                val response = apiProducts.updateProduct(product.id.toString(), product.copy(image_path = ""))

                if (response.isSuccessful) {
                    getProducts()
                } else {
                    Toast.makeText(applicationContext, "Error al actualizar el producto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "updateProduct Exception: ${e.message}")
                Toast.makeText(applicationContext, "Error al actualizar el producto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
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

}

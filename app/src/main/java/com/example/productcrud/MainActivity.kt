package com.example.productcrud


import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.documentfile.provider.DocumentFile
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.productcrud.databinding.ActivityMainBinding
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.io.InputStream
import android.Manifest
import android.content.Context
import android.database.Cursor


class MainActivity : AppCompatActivity() {

    private lateinit var apiProducts: ApiProducts
    private lateinit var productAdapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageView: ImageView



    private lateinit var binding: ActivityMainBinding


    private val productService = RetrofitHelper.getInstance().create(ApiProducts::class.java)
    private lateinit var imageUri: Uri

    private val STORAGE_PERMISSION_CODE = 1
    private val galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    handleGalleryResult(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)



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





        val storagePermission = Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, storagePermission) != PackageManager.PERMISSION_GRANTED) {
            // Si no tienes permisos, solicítalos
            ActivityCompat.requestPermissions(this, arrayOf(storagePermission), STORAGE_PERMISSION_CODE)
        } else {
            // Continúa con la lógica para obtener la ruta del archivo y realizar la llamada a la API
            setupViews()
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
                    Toast.makeText(
                        applicationContext,
                        "Producto eliminado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Error al eliminar el producto",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("ooooo", "deleteProduct Exception: ${e.message}")
                Toast.makeText(
                    applicationContext,
                    "Error al eliminar el producto",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun handleGalleryResult(data: Intent) {
        val selectedImage: Uri? = data.data
        selectedImage?.let {
            // Hacer algo con la imagen seleccionada, como mostrarla en un ImageView
            val imageView: ImageView = findViewById(R.id.imageView)
            imageView.setImageURI(it)
            imageUri = it
        }
    }

    private fun saveProduct() {
        // Obtener referencias a los EditText
        val editTextName: EditText = findViewById(R.id.editTextName)
        val editTextDescription: EditText = findViewById(R.id.editTextDescription)
        val editTextPrice: EditText = findViewById(R.id.editTextPrice)
        val editTextQuantity: EditText = findViewById(R.id.editTextQuantity)
        val editTextStatus: EditText = findViewById(R.id.editTextStatus)
        val editTextSubcategoryId: EditText = findViewById(R.id.editTextSubcategoryId)

        // Crear partes para los datos del producto
        val name = RequestBody.create("text/plain".toMediaTypeOrNull(), editTextName.text.toString())
        val description = RequestBody.create("text/plain".toMediaTypeOrNull(), editTextDescription.text.toString())
        val price = RequestBody.create("text/plain".toMediaTypeOrNull(), editTextPrice.text.toString())
        val quantity = RequestBody.create("text/plain".toMediaTypeOrNull(), editTextQuantity.text.toString())
        val status = RequestBody.create("text/plain".toMediaTypeOrNull(), editTextStatus.text.toString())
        val subcategoryId = RequestBody.create("text/plain".toMediaTypeOrNull(), editTextSubcategoryId.text.toString())

        // Crear parte para la imagen
        val image_path = File(getRealPathFromURI(imageUri))
        val requestFile = RequestBody.create("image_path/*".toMediaTypeOrNull(), image_path)

        val imagePart = MultipartBody.Part.createFormData("image_path", "${System.currentTimeMillis()}_image.jpg", requestFile)



        Log.d("SaveProduct", "Name: ${editTextName.text}")
        Log.d("SaveProduct", "Description: ${editTextDescription.text}")
        Log.d("SaveProduct", "Price: ${editTextPrice.text}")
        Log.d("SaveProduct", "Quantity: ${editTextQuantity.text}")
        Log.d("SaveProduct", "Status: ${editTextStatus.text}")
        Log.d("SaveProduct", "SubcategoryId: ${editTextSubcategoryId.text}")
        Log.d("SaveProduct", "ImageFile: ${image_path.path}")

        // Realizar la llamada a la API
        productService.createProduct(name, description, imagePart, price, quantity, status, subcategoryId)
            .enqueue(object : Callback<Product> {
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    Log.d("SaveProduct", "Response code: ${response.code()}")
                    Log.d("SaveProduct", "Response body: ${response.body()}")
                    // Resto del código...
                }

                override fun onFailure(call: Call<Product>, t: Throwable) {
                    Log.e("SaveProduct", "Error during API call: ${t.message}", t)
                    // Resto del código...
                }
            })
    }

    // Obtener la ruta real de la URI de la imagen seleccionada
    private fun getRealPathFromURI(uri: Uri): String {
        var realPath: String? = null
        val scheme = uri.scheme

        if (scheme == "file") {
            // La URI ya es una ruta de archivo
            realPath = uri.path
        } else if (scheme == "content") {
            // Si la URI es de tipo "content", consulta la base de datos de medios
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null

            try {
                cursor = contentResolver.query(uri, projection, null, null, null)

                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    realPath = cursor.getString(columnIndex)
                }
            } catch (e: Exception) {
                Log.e("getRealPathFromUri", "Error al obtener la ruta real de la URI: ${e.message}", e)
            } finally {
                cursor?.close()
            }
        }

        return realPath ?: ""
    }

    private fun setupViews() {
        // Configurar vistas, botones, etc.
        val btnSelectImage: Button = findViewById(R.id.btnSelectImage)
        btnSelectImage.setOnClickListener {
            openGallery()
        }

        val btnSave: Button = findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            saveProduct()
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido, continúa con la lógica para obtener la ruta del archivo y realizar la llamada a la API
                    setupViews()
                } else {
                    // Permiso denegado, puedes mostrar un mensaje al usuario o tomar otras acciones
                }
            }
            // Otros casos si tienes más códigos de solicitud de permisos
        }
    }

}

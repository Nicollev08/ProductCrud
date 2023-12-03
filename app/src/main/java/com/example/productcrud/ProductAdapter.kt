package com.example.productcrud

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText

class ProductAdapter(private var productList: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Inicializa los elementos de la vista aquí
        val tvProductId: TextView = itemView.findViewById(R.id.tvProductId)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvProductDescription: TextView = itemView.findViewById(R.id.tvProductDescription)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val tvProductQuantity: TextView = itemView.findViewById(R.id.tvProductQuantity)
        val tvProductStatus: TextView = itemView.findViewById(R.id.tvProductStatus)
        val tvProductSubcategoryId: TextView = itemView.findViewById(R.id.tvProductSubcategoryId)


        // ... otros elementos de la vista
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_product, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        // Asigna los valores a los elementos de la vista
        holder.tvProductId.text = "ID: ${product.id}"
        holder.tvProductName.text = "Name: ${product.name}"
        holder.tvProductDescription.text = "Descripcion: ${product.description}"
        holder.tvProductPrice.text = "Precio: ${product.price}"
        holder.tvProductQuantity.text = "Cantidad: ${product.quantity}"
        holder.tvProductStatus.text = "Estado: ${product.status}"
        holder.tvProductSubcategoryId.text = "Subcategorìa: ${product.subcategory_id}"

        // Configura los clics de los botones
        val context = holder.itemView.context

        // Configura los clics de los botones
        holder.btnEdit.setOnClickListener {
            // Aquí llamas a la función mostrarModal y le pasas el producto correspondiente
            mostrarModal(context, product)
        }

        holder.btnDelete.setOnClickListener {
            // Lógica para eliminar el producto
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    private fun mostrarModal(context: Context, product: Product) {
        // Inflar el layout del modal
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.update_product)

        // Obtener referencias a los elementos del layout del modal
        val idEditText = dialog.findViewById<TextInputEditText>(R.id.id)
        val nameEditText = dialog.findViewById<TextInputEditText>(R.id.name)
        val descriptionEditText = dialog.findViewById<TextInputEditText>(R.id.description)
        val priceEditText = dialog.findViewById<TextInputEditText>(R.id.price)
        val quantityEditText = dialog.findViewById<TextInputEditText>(R.id.quantity)
        val statusEditText = dialog.findViewById<TextInputEditText>(R.id.status)
        val subcategoryIdEditText = dialog.findViewById<TextInputEditText>(R.id.subcategory_id)

        // Configurar los valores de los elementos del modal según el producto seleccionado
        idEditText.setText(product.id.toString())
        nameEditText.setText(product.name)
        descriptionEditText.setText(product.description)
        priceEditText.setText(product.price.toString())
        quantityEditText.setText(product.quantity.toString())
        statusEditText.setText(product.status.toString()) // Convertir a String
        subcategoryIdEditText.setText(product.subcategory_id.toString())

        // Configurar el botón para guardar cambios
        val btnSaveChanges = dialog.findViewById<Button>(R.id.btnSaveChanges)
        btnSaveChanges.setOnClickListener {
            // Actualizar el objeto Product con los nuevos valores
            product.id = idEditText.text.toString().toInt()
            product.name = nameEditText.text.toString()
            product.description = descriptionEditText.text.toString()
            product.price = priceEditText.text.toString().toDouble()
            product.quantity = quantityEditText.text.toString().toInt()
            product.status = statusEditText.text.toString().toInt()
            product.subcategory_id = subcategoryIdEditText.text.toString().toInt()

            // Puedes agregar aquí la lógica para guardar los cambios en tu base de datos o realizar otras acciones
            // ...

            dialog.dismiss()
        }

        // Mostrar el modal
        dialog.show()
    }


}
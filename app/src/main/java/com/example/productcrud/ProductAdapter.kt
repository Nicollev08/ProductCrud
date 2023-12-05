package com.example.productcrud
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private lateinit var apiProducts: ApiProducts
class ProductAdapter(
    private var productList: List<Product>,
    private var onEditClick: (Product) -> Unit,
    private var onDeleteClick: (Product) -> Unit


) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {


    fun updateData(newProductList: List<Product>) {
        productList = newProductList
        notifyDataSetChanged()
    }
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductId: TextView = itemView.findViewById(R.id.tvProductId)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvProductDescription: TextView = itemView.findViewById(R.id.tvProductDescription)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val tvProductQuantity: TextView = itemView.findViewById(R.id.tvProductQuantity)
        val tvProductStatus: TextView = itemView.findViewById(R.id.tvProductStatus)
        val tvProductSubcategoryId: TextView = itemView.findViewById(R.id.tvProductSubcategoryId)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_product, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.tvProductId.text = "ID: ${product.id}"
        holder.tvProductName.text = "Name: ${product.name}"
        holder.tvProductDescription.text = "Descripcion: ${product.description}"
        holder.tvProductPrice.text = "Precio: ${product.price}"
        holder.tvProductQuantity.text = "Cantidad: ${product.quantity}"
        holder.tvProductStatus.text = "Estado: ${product.status}"
        holder.tvProductSubcategoryId.text = "Subcategorìa: ${product.subcategory_id}"

        val context = holder.itemView.context

        holder.btnEdit.setOnClickListener {
            onEditClick(product)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(product)
        }
    }



    override fun getItemCount(): Int {
        return productList.size
    }

}
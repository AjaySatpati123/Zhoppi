package com.example.zhoppi

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ProductsAdapter(private val layoutInflater: LayoutInflater, private val products: List<DocumentSnapshot>) : BaseAdapter()  {
    override fun getCount(): Int {
        return products.size
    }

    override fun getItem(position: Int): Any {
        return products[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: layoutInflater.inflate(R.layout.all_products_item_card, parent, false)

        val productImage = view.findViewById<ImageView>(R.id.productsImage)
        val productName = view.findViewById<TextView>(R.id.productsName)
        val productPrice = view.findViewById<TextView>(R.id.productsPrice)
        val productNegotiable = view.findViewById<TextView>(R.id.productsNegotiable)

        val product = products[position]

        val storageReference = FirebaseStorage.getInstance().reference.child("images/${product.id}.jpg")
        val localFile = File.createTempFile("images", "jpg")
        storageReference.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            val px = dpToPx(80, parent.context)
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, px, px, false)
            productImage.setImageBitmap(resizedBitmap)
        }
        productName.text = product.getString("itemName")
        productPrice.text = "₹" + product.getString("price")
        if(product.getString("negotiable")=="no"){
            productNegotiable.text = "Non-Negotiable"
        }else{
            productNegotiable.text = "Negotiable"
        }

        return view
    }
    private fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}
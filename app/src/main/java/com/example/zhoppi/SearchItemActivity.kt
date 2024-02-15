package com.example.zhoppi

import android.os.Bundle
import android.util.Log
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SearchItemActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val products: MutableList<DocumentSnapshot> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_item)

        val gridView = findViewById<GridView>(R.id.itemGridView)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = db.collectionGroup("products").get().await()
                Toast.makeText(this@SearchItemActivity, "Number of documents in 'products' collection group: ${result.size()}", Toast.LENGTH_SHORT).show()
                products.addAll(result.documents)
                val adapter = ItemUserAdapter(this@SearchItemActivity, layoutInflater, products)
                gridView.adapter = adapter
            } catch (e: Exception) {
                Toast.makeText(this@SearchItemActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }

        Log.d("Products", products.toString())

        gridView.setOnItemClickListener { parent, view, position, id ->
            val product = parent.getItemAtPosition(position) as DocumentSnapshot
            Toast.makeText(applicationContext, "UID: ${product.id}", Toast.LENGTH_SHORT).show()
        }
    }
}

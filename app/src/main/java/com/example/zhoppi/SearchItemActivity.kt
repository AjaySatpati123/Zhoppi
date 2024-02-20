package com.example.zhoppi

import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
        val searchView = findViewById<SearchView>(R.id.itemSearchView)
        searchView.isIconified = false

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = db.collectionGroup("products").get().await()
                products.addAll(result.documents)
                val adapter = ItemUserAdapter(this@SearchItemActivity, layoutInflater, products)
                gridView.adapter = adapter
            } catch (e: Exception) {
                Toast.makeText(this@SearchItemActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val filteredList = products.filter { product ->
                    val name = product.getString("itemName") ?: ""
                    val category = product.getString("itemCategory") ?: ""
                    val price = product.getString("price") ?: ""
                    name.contains(newText, ignoreCase = true)||
                            category.contains(newText, ignoreCase = true)||
                            price.contains(newText, ignoreCase = true)
                }

                val adapter = ItemUserAdapter(this@SearchItemActivity, layoutInflater, filteredList)
                gridView.adapter = adapter

                return false
            }
        })

        gridView.setOnItemClickListener { parent, _, position, _ ->
            val product = parent.getItemAtPosition(position) as DocumentSnapshot
            val uid = product.getString("uid")
            val intent = Intent(this, ItemViewBuyerActivity::class.java)
            intent.putExtra("id", uid)
            intent.putExtra("pid", product.id)
            startActivity(intent)
            finish()
        }
    }
}

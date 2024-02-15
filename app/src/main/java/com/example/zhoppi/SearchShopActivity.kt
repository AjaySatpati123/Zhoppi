package com.example.zhoppi

import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class SearchShopActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_shop)

        val db = FirebaseFirestore.getInstance()
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.isIconified = false
        val gridView = findViewById<GridView>(R.id.gridView)

        var documents: List<DocumentSnapshot> = listOf()

        db.collection("user")
            .whereEqualTo("type", "seller")
            .get()
            .addOnSuccessListener { result  ->
                documents = result.documents
                val adapter = ShopUserAdapter(layoutInflater, documents)
                gridView.adapter = adapter
            }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val filteredList = documents.filter { document ->
                    val name = document.getString("name") ?: ""
                    val shopDetails = document.get("shopDetails") as Map<*, *>
                    val shopName = shopDetails["shopName"] as String
                    val address = shopDetails["address"] as Map<*, *>
                    val shopStreet = address["shopStreet"] as String
                    val shopCity = address["shopCity"] as String
                    val shopState = address["shopState"] as String
                    val pinCode = address["pinCode"] as String

                    name.contains(newText, ignoreCase = true) ||
                            shopName.contains(newText, ignoreCase = true) ||
                            shopStreet.contains(newText, ignoreCase = true) ||
                            shopCity.contains(newText, ignoreCase = true) ||
                            shopState.contains(newText, ignoreCase = true) ||
                            pinCode.contains(newText, ignoreCase = true)
                }

                val adapter = ShopUserAdapter(layoutInflater, filteredList)
                gridView.adapter = adapter

                return false
            }
        })

        gridView.setOnItemClickListener { parent, view, position, id ->
            val document = parent.getItemAtPosition(position) as DocumentSnapshot
            Toast.makeText(applicationContext, "UID: ${document.id}", Toast.LENGTH_SHORT).show()
        }
    }
}
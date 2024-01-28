package com.example.zhoppi

import android.os.Bundle
import android.widget.GridView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val db = FirebaseFirestore.getInstance()
        val searchView = findViewById<SearchView>(R.id.searchView)
        val gridView = findViewById<GridView>(R.id.gridView)

        db.collection("user")
            .whereEqualTo("type", "seller")
            .get()
            .addOnSuccessListener { documents ->
                val adapter = UserAdapter(layoutInflater, documents.documents)
                gridView.adapter = adapter
            }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Implement your own filter logic here
                return false
            }
        })

        gridView.setOnItemClickListener { parent, view, position, id ->
            val document = parent.getItemAtPosition(position) as DocumentSnapshot
            Toast.makeText(applicationContext, "UID: ${document.id}", Toast.LENGTH_SHORT).show()
        }
    }
}
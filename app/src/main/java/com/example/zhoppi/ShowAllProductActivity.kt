package com.example.zhoppi

import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

class ShowAllProductActivity : AppCompatActivity() {
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_all_product)

        val documentId = FirebaseAuth.getInstance().currentUser!!.uid
        val gridView = findViewById<GridView>(R.id.showThese)
        var documents: List<DocumentSnapshot>
        db.collection("product").document(documentId).collection("products")
            .get()
            .addOnSuccessListener { result ->
                documents = result.documents
                val adapter = ProductsAdapter(layoutInflater, documents)
                gridView.adapter = adapter
            }
    }
}
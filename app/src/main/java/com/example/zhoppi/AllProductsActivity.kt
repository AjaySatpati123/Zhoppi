package com.example.zhoppi

import android.os.Bundle
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

class AllProductsActivity : AppCompatActivity() {
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_products)

        val bundle: Bundle? = intent.extras
        val id: String? = bundle?.getString("id")

        val ref = id?.let { db.collection("user").document(it) }
        val greeting = findViewById<TextView>(R.id.greet)
        val street = findViewById<TextView>(R.id.street)
        val city = findViewById<TextView>(R.id.city)
        val state = findViewById<TextView>(R.id.state)
        val pin = findViewById<TextView>(R.id.pin)
        ref?.get()?.addOnSuccessListener { document ->
            if(document != null){
                val name = document.data?.get("name")?.toString()
                greeting.text = name

                val shopDetails = document.data?.get("shopDetails") as? Map<*, *>
                val address = shopDetails?.get("address") as? Map<*, *>

                street.text = address?.get("shopStreet")?.toString()
                city.text = address?.get("shopCity")?.toString()
                state.text = address?.get("shopState")?.toString()
                pin.text = address?.get("pinCode")?.toString()
            }
        }?.addOnFailureListener {
            Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
        }
        val gridView = findViewById<GridView>(R.id.all)
        var documents: List<DocumentSnapshot>
        if (id != null) {
            db.collection("product").document(id).collection("products")
                .get()
                .addOnSuccessListener { result ->
                    documents = result.documents
                    val adapter = ProductsAdapter(layoutInflater, documents)
                    gridView.adapter = adapter
                }

        }
        gridView.setOnItemClickListener { parent, _, position, _ ->
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
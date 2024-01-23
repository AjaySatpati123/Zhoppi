package com.example.zhoppi

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class AddItemActivity : AppCompatActivity() {
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        val documentId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("user").document(documentId)
        val shopNameChange = findViewById<TextView>(R.id.shopNameChange)
        ref.get().addOnSuccessListener {
            if(it!=null){
                val shopDetails = it.data?.get("shopDetails") as? Map<*, *>
                val shopName = shopDetails?.get("shopName")?.toString()
                shopNameChange.text = shopName
            }
        }.addOnFailureListener {
            shopNameChange.text = "Add Item"
        }
    }
}
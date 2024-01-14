package com.example.zhoppi

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class BuyerDashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buyer_dashboard)

        val textView = findViewById<TextView>(R.id.textView3)
        val documentId = FirebaseAuth.getInstance().currentUser!!.uid
        // Set text to the TextView
        textView.text = documentId
    }
}
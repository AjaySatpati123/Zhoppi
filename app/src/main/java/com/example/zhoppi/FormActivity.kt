package com.example.zhoppi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class FormActivity : AppCompatActivity() {
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val intent = intent
        val receivedData = intent.getStringExtra("check")
        Toast.makeText(this, receivedData, Toast.LENGTH_SHORT).show()
        if(receivedData == "1" ){
            findViewById<EditText>(R.id.shopName).visibility = View.GONE
            findViewById<EditText>(R.id.shopStreet).visibility = View.GONE
            findViewById<EditText>(R.id.shopCity).visibility = View.GONE
            findViewById<EditText>(R.id.shopState).visibility = View.GONE
            findViewById<EditText>(R.id.pincode).visibility = View.GONE
            findViewById<EditText>(R.id.shopGstin).visibility = View.GONE
            val letsGoButton = findViewById<Button>(R.id.letsGo)
            val layoutParams = letsGoButton.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.verticalBias = 0.5f
            letsGoButton.layoutParams = layoutParams
            letsGoButton.setOnClickListener {
                val sName = findViewById<EditText>(R.id.name).text.toString()
                val sContactNo = findViewById<EditText>(R.id.contactNo).text.toString()
                val sUserName = findViewById<EditText>(R.id.username).text.toString()
                val sType = "buyer"
                val userMap = hashMapOf(
                    "name" to sName,
                    "contactNo" to sContactNo,
                    "username" to sUserName,
                    "type" to sType
                )
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                db.collection("user").document(userId).set(userMap).addOnSuccessListener {
                    Toast.makeText(this, "All set", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,SignInActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
                }
            }
        }else{

        }
    }
}
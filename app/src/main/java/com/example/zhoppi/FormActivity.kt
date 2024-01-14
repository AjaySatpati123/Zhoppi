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
        if (receivedData == "1") {
            buyer()
        } else {
            seller()
        }
    }
    private fun buyer(){
        findViewById<EditText>(R.id.shopName).visibility = View.GONE
        findViewById<EditText>(R.id.shopStreet).visibility = View.GONE
        findViewById<EditText>(R.id.shopCity).visibility = View.GONE
        findViewById<EditText>(R.id.shopState).visibility = View.GONE
        findViewById<EditText>(R.id.pinCode).visibility = View.GONE
        findViewById<EditText>(R.id.shopGstIn).visibility = View.GONE
        val letsGoButton = findViewById<Button>(R.id.letsGo)
        val layoutParams = letsGoButton.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.verticalBias = 0.5f
        letsGoButton.layoutParams = layoutParams
        letsGoButton.setOnClickListener {
            val sName = findViewById<EditText>(R.id.name).text.toString()
            val sContactNo = findViewById<EditText>(R.id.contactNo).text.toString()
            val sUserName = findViewById<EditText>(R.id.username).text.toString()
            val sType = "buyer"
            if (sName.isNotEmpty() && sContactNo.isNotEmpty() && sUserName.isNotEmpty() && sType.isNotEmpty()) {
                val userMap = hashMapOf(
                    "name" to sName,
                    "contactNo" to sContactNo,
                    "username" to sUserName,
                    "type" to sType
                )
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                db.collection("user").document(userId).set(userMap).addOnSuccessListener {
                    Toast.makeText(this, "Buyer Account Created", Toast.LENGTH_SHORT).show()
                    val auth = FirebaseAuth.getInstance()
                    auth.signOut()
                    startActivity(Intent(this, WelcomeScreenActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fill all the details", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun seller(){
        val letsGoButton = findViewById<Button>(R.id.letsGo)
        letsGoButton.setOnClickListener {
            val sName = findViewById<EditText>(R.id.name).text.toString()
            val sContactNo = findViewById<EditText>(R.id.contactNo).text.toString()
            val sUserName = findViewById<EditText>(R.id.username).text.toString()
            val sType = "seller"

            val sShopName = findViewById<EditText>(R.id.shopName).text.toString()
            val sShopStreet = findViewById<EditText>(R.id.shopStreet).text.toString()
            val sShopCity = findViewById<EditText>(R.id.shopCity).text.toString()
            val sShopState = findViewById<EditText>(R.id.shopState).text.toString()
            val sPinCode = findViewById<EditText>(R.id.pinCode).text.toString()
            val sAddress = hashMapOf(
                "shopStreet" to sShopStreet,
                "shopCity" to sShopCity,
                "shopState" to sShopState,
                "pinCode" to sPinCode
            )
            val sGstIn = findViewById<EditText>(R.id.shopGstIn).text.toString()

            val sShopDetails = hashMapOf(
                "shopName" to sShopName,
                "address" to sAddress,
                "gstIn" to sGstIn
            )
            if (sName.isNotEmpty() && sContactNo.isNotEmpty() && sUserName.isNotEmpty() && sType.isNotEmpty()
                && sShopName.isNotEmpty() && sShopStreet.isNotEmpty() && sShopCity.isNotEmpty() && sShopState.isNotEmpty()
                && sPinCode.isNotEmpty() && sGstIn.isNotEmpty()
            ) {
                val userMap = hashMapOf(
                    "name" to sName,
                    "contactNo" to sContactNo,
                    "username" to sUserName,
                    "type" to sType,
                    "shopDetails" to sShopDetails
                )
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                db.collection("user").document(userId).set(userMap).addOnSuccessListener {
                    Toast.makeText(this, "Seller Account Created", Toast.LENGTH_SHORT).show()
                    val auth = FirebaseAuth.getInstance()
                    auth.signOut()
                    startActivity(Intent(this, WelcomeScreenActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fill all the details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
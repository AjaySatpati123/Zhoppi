package com.example.zhoppi

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class BuyerDashboardActivity : AppCompatActivity() {
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buyer_dashboard)

        val documentId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("user").document(documentId)
        val greeting = findViewById<TextView>(R.id.greetingNameBuyer)
        ref.get().addOnSuccessListener {
            if(it!=null){
                val name = it.data?.get("name")?.toString()

                greeting.text = getString(R.string.welcome_message, name)
            }
        }.addOnFailureListener {
            Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageButton>(R.id.logoutbuyer).setOnClickListener {
            logOut()
        }

        findViewById<ImageView>(R.id.search).setOnClickListener {
            val popup = PopupMenu(this, it, Gravity.END)
            // Inflate the menu from xml
            popup.menuInflater.inflate(R.menu.search_menu, popup.menu)
            // Set Click Listener
            popup.setOnMenuItemClickListener { menuItem ->
                val intent = when (menuItem.itemId) {
                    R.id.search_shop -> Intent(this, SearchShopActivity::class.java)
                    R.id.search_item -> Intent(this, SearchItemActivity::class.java)
                    else -> return@setOnMenuItemClickListener false
                }
                startActivity(intent)
                finish()
                true
            }
            // Show the PopupMenu
            popup.show()
        }
    }
    private fun logOut(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { _, _ ->
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
        builder.setNegativeButton("No", null)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        val titleView = dialog.findViewById<TextView>(android.R.id.title)
        titleView?.setTextColor(ContextCompat.getColor(this, R.color.black))
        val messageView = dialog.findViewById<TextView>(android.R.id.message)
        messageView?.setTextColor(ContextCompat.getColor(this, R.color.grey))
    }
}
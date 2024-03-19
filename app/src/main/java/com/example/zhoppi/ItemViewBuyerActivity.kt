package com.example.zhoppi

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ItemViewBuyerActivity : AppCompatActivity() {
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_view_buyer)

        val bundle: Bundle? = intent.extras
        val id: String? = bundle?.getString("id")
        val pid: String? = bundle?.getString("pid")

        val img = findViewById<ImageView>(R.id.displayItem)
        val storageReference = FirebaseStorage.getInstance().reference.child("images/$pid.jpg")
        val localFile = File.createTempFile("images", "jpg")
        storageReference.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            val px = dpToPx(150, this)
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, px, px, false)
            img.setImageBitmap(resizedBitmap)
        }

        val name = findViewById<TextView>(R.id.displayName)
        val des = findViewById<TextView>(R.id.displayDescription)
        val negotiable = findViewById<TextView>(R.id.displayNegotiable)
        val price = findViewById<TextView>(R.id.displayPrice)
        if (id != null && pid != null) {
            val docRef = db.collection("product").document(id).collection("products").document(pid)
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        name.text = document.getString("itemName")
                        des.text = document.getString("description")
                        if(document.getString("negotiable")=="no"){
                            negotiable.text = "Not Negotiable"
                        }else{
                            negotiable.text = "Negotiable"
                        }
                        price.text = "\u20B9 ${document.getString("price")}"
                    }
                } else {
                    Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<Button>(R.id.openChat).setOnClickListener{

        }

        findViewById<Button>(R.id.seeMore).setOnClickListener {
            val intent = Intent(this, AllProductsActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
            finish()
        }
    }
    private fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, BuyerDashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }
}
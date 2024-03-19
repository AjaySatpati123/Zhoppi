package com.example.zhoppi

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddItemActivity : AppCompatActivity() {
    private var db = Firebase.firestore
    private val REQUEST_IMAGE_CAPTURE = 1
    private var capturedImage: Bitmap? = null
    private var selectedValue: String = ""
    private var negotiable: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        val spinner: Spinner = findViewById(R.id.itemCategory)
        val values = arrayOf("Machinery", "Grocery", "Household", "Others")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, values)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedValue = values[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedValue = "Others"
            }
        }

        val spinner2: Spinner = findViewById(R.id.negotiable)
        val values2 = arrayOf("Yes", "no")
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, values2)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapter2
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                negotiable = values2[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                negotiable = "Yes"
            }
        }

        val documentId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("user").document(documentId)
        val shopNameChange = findViewById<TextView>(R.id.shopNameChange)
        ref.get().addOnSuccessListener {
            if (it != null) {
                val shopDetails = it.data?.get("shopDetails") as? Map<*, *>
                val shopName = shopDetails?.get("shopName")?.toString()
                shopNameChange.text = shopName
            }
        }.addOnFailureListener {
            shopNameChange.text = getString(R.string.add_item)
        }

        findViewById<ImageButton>(R.id.itemPhoto).setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_IMAGE_CAPTURE
                )
            } else {
                openCamera()
            }
        }

        findViewById<Button>(R.id.addProduct).setOnClickListener {
            if (capturedImage == null) {
                Toast.makeText(
                    this,
                    "Please capture an image before submitting",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val map = addProduct()
                if (uid != null) {
                    val newProductId =
                        db.collection("product").document(uid).collection("products").document().id

                    val baos = ByteArrayOutputStream()
                    capturedImage!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    val storageRef = FirebaseStorage.getInstance().reference
                    val imageRef = storageRef.child("images/$newProductId.jpg")
                    val uploadTask = imageRef.putBytes(data)
                    uploadTask.addOnFailureListener {
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener {

                    }
                    db.collection("product").document(uid).collection("products")
                        .document(newProductId)
                        .set(map, SetOptions.merge())
                        .addOnSuccessListener {
                            successful()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    private fun successful() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Product Added")
        builder.setMessage("Added Successfully")
        builder.setPositiveButton("OK") { _, _ ->
            startActivity(Intent(this, SellerDashboardActivity::class.java))
            finish()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(this.packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted, open the camera.
                openCamera()
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT)
                    .show();
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            capturedImage = data?.extras?.get("data") as Bitmap
            findViewById<ImageButton>(R.id.itemPhoto).setImageBitmap(capturedImage)
            findViewById<ImageButton>(R.id.itemPhoto).setBackgroundColor(Color.WHITE)
        }
    }

    private fun addProduct(): HashMap<String, String?> {
        val sProductId = UUID.randomUUID().toString()
        val sItemName = findViewById<EditText>(R.id.itemName).text.toString()
        val sItemCategory = selectedValue
        val sDescription = findViewById<EditText>(R.id.description).text.toString()
        val sPrice = findViewById<EditText>(R.id.price).text.toString()
        val sNegotiable = negotiable
        val sUid = FirebaseAuth.getInstance().currentUser!!.uid
        return hashMapOf(
            "productId" to sProductId,
            "itemName" to sItemName,
            "itemCategory" to sItemCategory,
            "description" to sDescription,
            "price" to sPrice,
            "negotiable" to sNegotiable,
            "uid" to sUid
        )
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, SellerDashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }
}
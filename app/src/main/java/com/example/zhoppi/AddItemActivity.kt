package com.example.zhoppi

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddItemActivity : AppCompatActivity() {
    private var db = Firebase.firestore
    private val REQUEST_IMAGE_CAPTURE = 100
    private var imageUri: Uri? = null
    private var selectedValue: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        val spinner: Spinner = findViewById(R.id.itemCategory)
        val values = arrayOf("Machinery", "Grocery", "Household", "Others")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, values)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedValue = values[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedValue = "Others"
            }
        }

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
            shopNameChange.text = getString(R.string.add_item)
        }

        findViewById<ImageButton>(R.id.itemPhoto).setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try{
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }catch (e:ActivityNotFoundException){
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show();
            }
        }

        findViewById<Button>(R.id.addProduct).setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val map = addProduct()
            if (uid != null) {
                // Generate a new document ID for the product
                val newProductId = db.collection("product").document(uid).collection("products").document().id

                // Store the product in the 'products' subcollection of the user's document
                db.collection("product").document(uid).collection("products").document(newProductId)
                    .set(map, SetOptions.merge())
                    .addOnSuccessListener {
                        map["productId"]?.let { it1 -> uploadImage(it1) }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show()
                    }
            }
            startActivity(Intent(this, SellerDashboardActivity::class.java))
            finish()
        }
    }

    private fun uploadImage(productId: String) {
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/$productId.jpg")
        val uploadTask = imageUri?.let { imageRef.putFile(it) }

        uploadTask?.addOnFailureListener {
            Toast.makeText(this, "Upload Unsuccessful", Toast.LENGTH_SHORT).show()
        }?.addOnSuccessListener {
            Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show()
            imageRef.downloadUrl.addOnSuccessListener { uri ->
            }
        }
    }

    private fun addProduct(): HashMap<String, String?> {
        val sProductId = UUID.randomUUID().toString()
        val sItemName = findViewById<EditText>(R.id.itemName).text.toString()
        val sItemCategory = selectedValue
        val sDescription = findViewById<EditText>(R.id.description).text.toString()
        val sPrice = findViewById<EditText>(R.id.price).text.toString()
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        var sNegotiable: String? = null

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            sNegotiable = if (checkedId == R.id.negotiable) {
                "yes"
            } else {
                "no"
            }
        }
        return hashMapOf(
            "productId" to sProductId,
            "itemName" to sItemName,
            "itemCategory" to sItemCategory,
            "description" to sDescription,
            "price" to sPrice,
            "negotiable" to sNegotiable
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode== RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
            findViewById<ImageButton>(R.id.itemPhoto).setImageBitmap(imageBitmap)
            imageUri = getImageUri(applicationContext, imageBitmap)
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
}
package com.example.zhoppi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ItemUserAdapter (private val context: Context, private val layoutInflater: LayoutInflater, private val users: List<DocumentSnapshot>) : BaseAdapter() {

    override fun getCount(): Int {
        return users.size
    }

    override fun getItem(position: Int): Any {
        return users[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: layoutInflater.inflate(R.layout.item_item_card, parent, false)

        val nameTextView = view.findViewById<TextView>(R.id.itemNameTextView)
        val categoryTextView = view.findViewById<TextView>(R.id.itemCategoryTextView)
        val priceTextView = view.findViewById<TextView>(R.id.itemPriceTextView)
        val shopNameTextView = view.findViewById<TextView>(R.id.itemShopNameTextView)
        val addressTextView = view.findViewById<TextView>(R.id.itemAddressTextView)

        val user = users[position]
        nameTextView.text = "Item Name - ${user.getString("itemName")}"
        categoryTextView.text = "Category - ${user.getString("itemCategory")}"
        priceTextView.text = "Price - ${user.getString("price")}"

        val uid = user.getString("uid")
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("user").document(uid)
                .get()
                .addOnSuccessListener { userDocument ->
                    val shopDetails = userDocument.get("shopDetails") as Map<*, *>
                    val shopName = shopDetails["shopName"] as String
                    shopNameTextView.text = "Shop Name - $shopName"

                    val address = shopDetails["address"] as Map<*, *>
                    val shopStreet = address["shopStreet"] as String
                    val shopCity = address["shopCity"] as String
                    val shopState = address["shopState"] as String
                    val pinCode = address["pinCode"] as String
                    addressTextView.text = "Address - $shopStreet, $shopCity, $shopState, $pinCode"
                    Toast.makeText(context, "done", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { exception ->
                Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
}
package com.example.zhoppi
//import android.os.Build.VERSION_CODES.R
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.firestore.DocumentSnapshot

class ShopUserAdapter(private val layoutInflater: LayoutInflater, private val users: List<DocumentSnapshot>) : BaseAdapter() {

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
        val view = convertView ?: layoutInflater.inflate(R.layout.shop_item_card, parent, false)

        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
        val shopNameTextView = view.findViewById<TextView>(R.id.shopNameTextView)
        val streetTextView = view.findViewById<TextView>(R.id.streetTextView)
        val cityTextView = view.findViewById<TextView>(R.id.cityTextView)
        val stateTextView = view.findViewById<TextView>(R.id.stateTextView)
        val pinCodeTextView = view.findViewById<TextView>(R.id.pinCodeTextView)

        val user = users[position]
        nameTextView.text = Html.fromHtml("<b>Name</b> - ${user.getString("name")}", Html.FROM_HTML_MODE_COMPACT)

        val shopDetails = user.get("shopDetails") as Map<*, *>

        val shopName = shopDetails["shopName"] as String
        shopNameTextView.text = Html.fromHtml("<b>Shop Name</b> - $shopName", Html.FROM_HTML_MODE_COMPACT)

        val address = shopDetails["address"] as Map<*, *>
        val street = address["shopStreet"] as String
        streetTextView.text = "$street,"
        val city = address["shopCity"] as String
        cityTextView.text = city
        val state = address["shopState"] as String
        stateTextView.text = "$state,"
        val pinCode = address["pinCode"] as String
        pinCodeTextView.text = pinCode

        return view
    }
}
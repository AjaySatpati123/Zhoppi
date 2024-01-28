package com.example.zhoppi
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.firestore.DocumentSnapshot

class UserAdapter(private val layoutInflater: LayoutInflater, private val users: List<DocumentSnapshot>) : BaseAdapter() {

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
        val view = convertView ?: layoutInflater.inflate(R.layout.item_card, parent, false)

        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)

        val user = users[position]
        nameTextView.text = user.getString("name")

        return view
    }
}
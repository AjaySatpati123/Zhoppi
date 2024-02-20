package com.example.zhoppi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ItemViewBuyerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_view_buyer)

        val bundle: Bundle? = intent.extras
        val id: String? = bundle?.getString("id")
        val pid: String? = bundle?.getString("pid")
        findViewById<Button>(R.id.seeMore).setOnClickListener {
            val intent = Intent(this, AllProductsActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
            finish()
        }
    }
}
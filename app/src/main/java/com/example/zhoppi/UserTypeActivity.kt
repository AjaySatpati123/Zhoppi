package com.example.zhoppi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class UserTypeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_type)

        findViewById<Button>(R.id.chooseBuyer).setOnClickListener {
            val value = "1"
            val intent = Intent(this, FormActivity::class.java)
            intent.putExtra("check", value)
            startActivity(intent)
            finish()
        }
        findViewById<Button>(R.id.chooseSeller).setOnClickListener {
            val value = "2"
            val intent = Intent(this, FormActivity::class.java)
            intent.putExtra("check", value)
            startActivity(intent)
            finish()
        }
    }
}
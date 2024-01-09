package com.example.zhoppi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val intent = intent
        val receivedData = intent.getStringExtra("check")
        Toast.makeText(this, receivedData, Toast.LENGTH_SHORT).show()
    }
}
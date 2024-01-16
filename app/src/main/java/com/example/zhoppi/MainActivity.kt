package com.example.zhoppi

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        },4000)

        val textView = findViewById<TextView>(R.id.tagLine)
        textView.alpha = 0f
        val fadeInAnimator = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f)
        fadeInAnimator.duration = 1000
        fadeInAnimator.interpolator = AccelerateInterpolator()
        fadeInAnimator.start()
    }
}
package com.example.zhoppi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    companion object{
        lateinit var auth:FirebaseAuth
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        auth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.signIn).setOnClickListener {
            val email = findViewById<EditText>(R.id.emailSignIn).text.toString()
            val password = findViewById<EditText>(R.id.passwordSignIn).text.toString()
            if(email.isNotEmpty() && password.isNotEmpty()){
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this, "You are signed in", Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this, ChangeHere::class.java))
//                        finish()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<TextView>(R.id.register).setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}
package com.example.zhoppi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {
    companion object {
        lateinit var auth: FirebaseAuth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<Button>(R.id.signIn).setOnClickListener {
            val email = findViewById<EditText>(R.id.emailSignIn).text.toString()
            val password = findViewById<EditText>(R.id.passwordSignIn).text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "You are signed in", Toast.LENGTH_SHORT).show()
                        dashboard()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<Button>(R.id.googleBtnSignIn).setOnClickListener {
            googleSignInClient.signOut()
            startActivityForResult(googleSignInClient.signInIntent, 13)
        }

        findViewById<TextView>(R.id.register).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun dashboard() {
        val db = FirebaseFirestore.getInstance()
        val documentId = FirebaseAuth.getInstance().currentUser!!.uid
        val documentRef = db.collection("user").document(documentId)
        documentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val userTypes = documentSnapshot.getString("type")
                if (userTypes == "buyer") {
                    startActivity(Intent(this, BuyerDashboardActivity::class.java))
                    finish()
                } else if (userTypes == "seller") {
                    startActivity(Intent(this, SellerDashboardActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Please Register Again", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please Register Again", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            println("Error getting document: $exception")
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 && resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val documentId = FirebaseAuth.getInstance().currentUser!!.uid
                    val db = FirebaseFirestore.getInstance()
                    val collectionReference = db.collection("user")
                    val documentReference = collectionReference.document(documentId)
                    documentReference.get().addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                            dashboard()
                        } else {
                            val gso =
                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(getString(R.string.client_id))
                                    .requestEmail()
                                    .build()
                            val googleSignInClient = GoogleSignIn.getClient(this, gso)
                            googleSignInClient.signOut()
                            Toast.makeText(this, "Please Register", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { exception ->
                        println("Error getting document: $exception")
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }
}
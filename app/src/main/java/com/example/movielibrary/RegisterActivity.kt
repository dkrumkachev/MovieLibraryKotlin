package com.example.movielibrary

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        auth = Firebase.auth
        dbReference = Firebase.database.reference
        findViewById<Button>(R.id.registerButton).setOnClickListener {
            val email: String = findViewById<TextView>(R.id.emailTextView).text.toString()
            val password: String = findViewById<TextView>(R.id.passwordTextView).text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter email",
                    Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Please enter password",
                    Toast.LENGTH_SHORT).show()
            }
            register(email, password)
        }

        findViewById<TextView>(R.id.login_instead).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = User(email = email)
                    val userKey = Base64.encode(email.toByteArray())
                    dbReference.child("users").child(userKey).setValue(user)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("userEmail", email)
                    startActivity(intent)
                } else {
                    Toast.makeText(this,"Authentication failed.",
                        Toast.LENGTH_SHORT,).show()
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
package com.example.eventmanagerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.app.AlertDialog

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val forgotPasswordBtn = findViewById<Button>(R.id.forgotPasswordBtn)
        val goToRegisterBtn = findViewById<Button>(R.id.goToRegisterBtn)
        val signUpBtn = findViewById<Button>(R.id.signUpBtn)

        loginBtn.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val user = result.user
                    if (user != null) {
                        fetchUserRoleAndProceed(user.uid)
                    } else {
                        Toast.makeText(this, "User not found.", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message ?: "Login failed.", Toast.LENGTH_LONG).show()
                }
        }

        forgotPasswordBtn?.setOnClickListener {
            val emailInput = EditText(this)
            emailInput.hint = "Enter your email"
            AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("Enter your email to receive a password reset link.")
                .setView(emailInput)
                .setPositiveButton("Send") { _, _ ->
                    val email = emailInput.text.toString().trim()
                    if (email.isNotEmpty()) {
                        auth.sendPasswordResetEmail(email)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Reset email sent! Check your inbox.", Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, e.message ?: "Failed to send reset email.", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        goToRegisterBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        signUpBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun fetchUserRoleAndProceed(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val role = documentSnapshot.getString("role")
                    Log.d("ROLE_CHECK", "Role from Firestore: $role, data: ${documentSnapshot.data}")
                    if (role == "client" || role == "organizer" || role == "admin") {
                        goToRoleActivity(role)
                    } else {
                        Toast.makeText(this, "Please complete your registration and select a role.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, RegisterActivity::class.java)
                        intent.putExtra("uid", userId)
                        intent.putExtra("email", documentSnapshot.getString("email") ?: "")
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this, "User data not found.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message ?: "Failed to fetch user role.", Toast.LENGTH_LONG).show()
            }
    }

    private fun goToRoleActivity(role: String) {
        val intent = when (role) {
            "client" -> Intent(this, ClientDashboardActivity::class.java)
            "organizer" -> Intent(this, OrganizerDashboardActivity::class.java)
            "admin" -> Intent(this, AdminDashboardActivity::class.java)
            else -> null
        }
        intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
} 
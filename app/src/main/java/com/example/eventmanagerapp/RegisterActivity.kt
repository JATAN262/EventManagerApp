package com.example.eventmanagerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventmanagerapp.Model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Toast.makeText(this, "RegisterActivity created", Toast.LENGTH_SHORT).show()

        // Declare variables at the top
        val nameEditText: com.google.android.material.textfield.TextInputEditText
        val emailEditText: com.google.android.material.textfield.TextInputEditText
        val passwordEditText: com.google.android.material.textfield.TextInputEditText
        val roleGroup: com.google.android.material.chip.ChipGroup
        val registerBtn: com.google.android.material.button.MaterialButton

        try {
            nameEditText = findViewById(R.id.nameEditText)
            emailEditText = findViewById(R.id.emailEditText)
            passwordEditText = findViewById(R.id.passwordEditText)
            roleGroup = findViewById(R.id.roleGroup)
            registerBtn = findViewById(R.id.registerBtn)
            Log.d("RegisterActivity", "All views found successfully")
        } catch (e: Exception) {
            Log.e("RegisterActivity", "Error finding views: ${e.message}", e)
            Toast.makeText(this, "Error initializing UI: ${e.message}", Toast.LENGTH_LONG).show()
            return
        }

        auth = FirebaseAuth.getInstance()

        // Pre-fill email and UID if provided (for users who signed up via other means)
        val prefillUid = intent.getStringExtra("uid") ?: ""
        val prefillEmail = intent.getStringExtra("email") ?: ""
        if (prefillEmail.isNotEmpty()) emailEditText.setText(prefillEmail)

        registerBtn.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val selectedRoleId = roleGroup.checkedChipId
            val role = if (selectedRoleId != -1) findViewById<com.google.android.material.chip.Chip>(selectedRoleId).text.toString().lowercase() else ""

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
                Toast.makeText(this, "Please fill all fields and select a role.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val user = result.user
                    if (user != null) {
                        val userModel = UserModel(user.uid, name, email, role)
                        db.collection("users").document(user.uid).set(userModel)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show()
                                goToRoleActivity(role)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to save user: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(this, "User creation failed.", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Registration error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun goToRoleActivity(role: String) {
        try {
            val intent = when (role) {
                "client" -> Intent(this, ClientDashboardActivity::class.java)
                "organizer" -> Intent(this, OrganizerDashboardActivity::class.java)
                "admin" -> Intent(this, AdminDashboardActivity::class.java)
                else -> {
                    Toast.makeText(this, "Unknown role: $role", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e("RegisterActivity", "Error navigating to dashboard: ${e.message}", e)
            Toast.makeText(this, "Error navigating to dashboard: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
} 
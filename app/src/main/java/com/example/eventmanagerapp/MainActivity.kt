package com.example.eventmanagerapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import android.view.WindowManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (!isTaskRoot) {
            finish()
            return
        }
        
        // Prevent surface layer issues during transitions
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // User not logged in → go to login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            // User logged in → get role from Firestore
            val uid = user.uid
            FirebaseFirestore.getInstance().collection("users")
                .document(uid).get()
                .addOnSuccessListener { snapshot ->
                    val role = snapshot.getString("role")
                    Toast.makeText(this, "Detected role: $role", Toast.LENGTH_LONG).show()
                    android.util.Log.d("MAIN_ACTIVITY_ROLE", "Detected role: $role")
                    if (role == null || role.isEmpty()) {
                        Toast.makeText(this, "No role set, redirecting to RegisterActivity", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, RegisterActivity::class.java)
                        intent.putExtra("uid", uid)
                        intent.putExtra("email", user.email ?: "")
                        startActivity(intent)
                        finish()
                        return@addOnSuccessListener
                    }
                    val intent = when (role) {
                        "client" -> {
                            Toast.makeText(this, "Launching ClientDashboardActivity", Toast.LENGTH_SHORT).show()
                            android.util.Log.d("MAIN_ACTIVITY_ROLE", "Launching ClientDashboardActivity")
                            Intent(this, ClientDashboardActivity::class.java)
                        }
                        "organizer" -> {
                            Toast.makeText(this, "Launching OrganizerDashboardActivity", Toast.LENGTH_SHORT).show()
                            android.util.Log.d("MAIN_ACTIVITY_ROLE", "Launching OrganizerDashboardActivity")
                            Intent(this, OrganizerDashboardActivity::class.java)
                        }
                        "admin" -> {
                            Toast.makeText(this, "Launching AdminDashboardActivity", Toast.LENGTH_SHORT).show()
                            android.util.Log.d("MAIN_ACTIVITY_ROLE", "Launching AdminDashboardActivity")
                            Intent(this, AdminDashboardActivity::class.java)
                        }
                        else -> {
                            Toast.makeText(this, "Unknown role, logging out", Toast.LENGTH_SHORT).show()
                            android.util.Log.d("MAIN_ACTIVITY_ROLE", "Unknown role, logging out")
                            FirebaseAuth.getInstance().signOut()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                            return@addOnSuccessListener
                        }
                    }
                    // Prevent duplicate navigation if already in dashboard
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to get user role, logging out", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
        }
    }
} 
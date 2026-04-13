package com.example.eventmanagerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagerapp.Adapter.AdminBookingAdapter
import com.example.eventmanagerapp.Model.Booking
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import android.widget.ImageButton

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var bookingsRecyclerView: RecyclerView
    private lateinit var adapter: AdminBookingAdapter
    private val bookings = mutableListOf<Pair<String, Booking>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)
        android.widget.Toast.makeText(this, "AdminDashboardActivity launched", android.widget.Toast.LENGTH_SHORT).show()
        android.util.Log.d("DASHBOARD_FLOW", "AdminDashboardActivity launched")

        // Logout button logic
        val logoutBtn = findViewById<ImageButton>(R.id.logoutBtn)
        logoutBtn?.setOnClickListener {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        bookingsRecyclerView = findViewById(R.id.adminBookingsRecyclerView)
        bookingsRecyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AdminBookingAdapter(
            bookings,
            onAction = { bookingId, action ->
                when (action) {
                    "accepted" -> updateBookingStatus(bookingId, "accepted")
                    "rejected" -> updateBookingStatus(bookingId, "rejected")
                    "delete" -> deleteBooking(bookingId)
                }
            }
        )
        bookingsRecyclerView.adapter = adapter

        // Load bookings
        loadBookings()

        // ... rest of the original code ...
    }
    
    private fun updateBookingStatus(bookingId: String, status: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("bookings").document(bookingId)
            .update("status", status)
            .addOnSuccessListener {
                Toast.makeText(this, "Booking status updated to $status", Toast.LENGTH_SHORT).show()
                loadBookings() // Reload the bookings
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update booking status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun deleteBooking(bookingId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("bookings").document(bookingId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Booking deleted successfully", Toast.LENGTH_SHORT).show()
                loadBookings() // Reload the bookings
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete booking: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun loadBookings() {
        val db = FirebaseFirestore.getInstance()
        db.collection("bookings")
            .get()
            .addOnSuccessListener { documents ->
                bookings.clear()
                for (document in documents) {
                    document.toObject(Booking::class.java)?.let { booking ->
                        bookings.add(Pair(document.id, booking))
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load bookings: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
} 
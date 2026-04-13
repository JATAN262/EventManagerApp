package com.example.eventmanagerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagerapp.Adapter.BookingAdapter
import com.example.eventmanagerapp.Model.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.ImageButton
import android.widget.Button
import android.view.WindowManager
import android.view.View
import android.widget.TextView

class OrganizerDashboardActivity : AppCompatActivity() {

    private lateinit var bookingsRecyclerView: RecyclerView
    private lateinit var bookingsAdapter: BookingAdapter
    private val bookings = mutableListOf<Pair<String, Booking>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Prevent surface layer issues
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        
        android.widget.Toast.makeText(this, "OrganizerDashboardActivity launched", android.widget.Toast.LENGTH_SHORT).show()
        android.util.Log.d("DASHBOARD_FLOW", "OrganizerDashboardActivity launched")
        setContentView(R.layout.activity_organizer_dashboard)

        // Wire up Edit Portfolio button
        val editPortfolioBtn = findViewById<com.google.android.material.button.MaterialButton>(R.id.editPortfolioBtn)
        editPortfolioBtn.setOnClickListener {
            startActivity(Intent(this, EditPortfolioActivity::class.java))
        }

        // Logout button logic
        val logoutBtn = findViewById<ImageButton>(R.id.logoutBtn)
        logoutBtn?.setOnClickListener {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        bookingsRecyclerView = findViewById(R.id.bookingsRecyclerView)
        bookingsRecyclerView.layoutManager = LinearLayoutManager(this)
        bookingsAdapter = BookingAdapter(
            bookings,
            onAction = { bookingId, newStatus -> updateBookingStatus(bookingId, newStatus) },
            onChatClick = { clientId ->
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("otherUserId", clientId)
                startActivity(intent)
            },
            showActionButtons = true
        )
        bookingsRecyclerView.adapter = bookingsAdapter

        // Create organizer profile if it doesn't exist
        createOrganizerProfileIfNeeded()
        
        // Load bookings
        loadBookings()
        // Add a test pending booking for demo (remove after confirming UI)
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
    
    private fun createOrganizerProfileIfNeeded() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) return
        
        val db = FirebaseFirestore.getInstance()
        db.collection("organizers").document(currentUserId).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    // Create a default organizer profile
                    val organizer = com.example.eventmanagerapp.Model.Organizer(
                        uid = currentUserId,
                        name = "Event Organizer",
                        location = "Location TBD",
                        bio = "Professional event organizer",
                        imageUrl = "https://via.placeholder.com/150",
                        priceRange = "$100-$500"
                    )
                    
                    db.collection("organizers").document(currentUserId).set(organizer)
                        .addOnSuccessListener {
                            Log.d("OrganizerDashboard", "Organizer profile created")
                        }
                        .addOnFailureListener { e ->
                            Log.e("OrganizerDashboard", "Failed to create organizer profile: ${e.message}")
                        }
                }
            }
    }
    
    private fun loadBookings() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }
        val db = FirebaseFirestore.getInstance()
        db.collection("bookings")
            .whereEqualTo("organizerId", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                bookings.clear()
                for (document in documents) {
                    document.toObject(Booking::class.java)?.let { booking ->
                        val pair = Pair(document.id, booking)
                        bookings.add(pair)
                    }
                }
                bookingsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load bookings: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addTestPendingBooking() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val testBooking = Booking(
            clientId = "testClient123",
            organizerId = currentUserId,
            date = "2025-08-01",
            occasion = "Test Event",
            status = "pending"
        )
        db.collection("bookings")
            .add(testBooking)
            .addOnSuccessListener { Log.d("OrganizerDashboard", "Test pending booking added") }
            .addOnFailureListener { e -> Log.e("OrganizerDashboard", "Failed to add test booking: ${e.message}") }
    }
} 
package com.example.eventmanagerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagerapp.Adapter.OrganizerAdapter
import com.example.eventmanagerapp.Model.Organizer
import com.example.eventmanagerapp.Model.Booking
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import android.view.View
import android.widget.TextView
import com.google.firebase.firestore.ListenerRegistration
import android.widget.ImageButton
import com.google.android.material.button.MaterialButton

class ClientDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val organizers = mutableListOf<Organizer>()
    private lateinit var adapter: OrganizerAdapter
    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, "ClientDashboardActivity launched", Toast.LENGTH_SHORT).show()
        Log.d("DASHBOARD_FLOW", "ClientDashboardActivity launched")
        setContentView(R.layout.activity_client_dashboard)

        // Initialize views
        recyclerView = findViewById(R.id.organizerRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup logout button
        val logoutBtn = findViewById<ImageButton>(R.id.logoutBtn)
        logoutBtn?.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Setup My Bookings button
        val myBookingsBtn = findViewById<MaterialButton>(R.id.myBookingsBtn)
        myBookingsBtn?.setOnClickListener {
            val intent = Intent(this, ClientBookingsActivity::class.java)
            startActivity(intent)
        }

        // Setup adapter
        adapter = OrganizerAdapter(
            organizers,
            onDetailsClick = { selectedOrganizer ->
                try {
                    val intent = Intent(this, OrganizerDetailsActivity::class.java)
                    intent.putExtra("organizerId", selectedOrganizer.uid)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("ClientDashboard", "Error opening organizer details: ${e.message}", e)
                    Toast.makeText(this, "Error opening organizer details", Toast.LENGTH_SHORT).show()
                }
            },
            onChatClick = { selectedOrganizer ->
                Toast.makeText(this, "Chat with: ${selectedOrganizer.uid}", Toast.LENGTH_SHORT).show()
                Log.d("ClientDashboard", "Chat button clicked for UID: ${selectedOrganizer.uid}")
                try {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("otherUserId", selectedOrganizer.uid) // FIXED KEY
                    intent.putExtra("organizerName", selectedOrganizer.name)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("ClientDashboard", "Error opening chat: ", e)
                    Toast.makeText(this, "Error opening chat", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView.adapter = adapter

        // Load organizers
        loadOrganizers()
        
        // Add sample organizers for testing (comment out after first run)
        addSampleOrganizers()
    }

    private fun loadOrganizers() {
        db.collection("organizers")
            .get()
            .addOnSuccessListener { documents ->
                organizers.clear()
                for (document in documents) {
                    val organizer = document.toObject(Organizer::class.java)
                    organizer?.let {
                        val organizerWithId = it.copy(uid = document.id)
                        organizers.add(organizerWithId)
                    }
                }
                adapter.notifyDataSetChanged()
                Log.d("ClientDashboard", "Loaded ${organizers.size} organizers")
                
                if (organizers.isEmpty()) {
                    Toast.makeText(this, "No organizers found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ClientDashboard", "Error loading organizers: ${exception.message}", exception)
                Toast.makeText(this, "Error loading organizers: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addSampleOrganizers() {
        val sampleOrganizers = listOf(
            Organizer(
                uid = "sample1",
                name = "Elite Events",
                location = "New York, NY",
                bio = "Premium event planning services for corporate and private events",
                imageUrl = "https://via.placeholder.com/150/FF6B6B/FFFFFF?text=Elite",
                priceRange = "$500-$2000"
            ),
            Organizer(
                uid = "sample2", 
                name = "Wedding Wonders",
                location = "Los Angeles, CA",
                bio = "Specialized in creating magical wedding experiences",
                imageUrl = "https://via.placeholder.com/150/4ECDC4/FFFFFF?text=Wedding",
                priceRange = "$1000-$5000"
            ),
            Organizer(
                uid = "sample3",
                name = "Corporate Connect",
                location = "Chicago, IL", 
                bio = "Professional corporate event management and team building",
                imageUrl = "https://via.placeholder.com/150/45B7D1/FFFFFF?text=Corporate",
                priceRange = "$300-$1500"
            )
        )

        for (organizer in sampleOrganizers) {
            db.collection("organizers").document(organizer.uid).set(organizer)
                .addOnSuccessListener {
                    Log.d("ClientDashboard", "Added sample organizer: ${organizer.name}")
                }
                .addOnFailureListener { e ->
                    Log.e("ClientDashboard", "Failed to add sample organizer: ${e.message}")
                }
        }
    }
} 
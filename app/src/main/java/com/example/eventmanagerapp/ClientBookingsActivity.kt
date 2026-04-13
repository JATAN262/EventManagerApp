package com.example.eventmanagerapp

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagerapp.Model.Booking
import com.example.eventmanagerapp.Model.Organizer
import com.example.eventmanagerapp.Adapter.ClientBookingAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import android.widget.Toast

class ClientBookingsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClientBookingAdapter
    private val bookings = mutableListOf<Pair<String, Booking>>()
    private var bookingsListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_bookings)
        recyclerView = findViewById(R.id.clientBookingsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ClientBookingAdapter(bookings, onDelete = { bookingId -> deleteBooking(bookingId) })
        recyclerView.adapter = adapter
        listenToBookings()
    }

    private fun listenToBookings() {
        val clientId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        bookingsListener = FirebaseFirestore.getInstance().collection("bookings")
            .whereEqualTo("clientId", clientId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                bookings.clear()
                if (snapshot != null) {
                    for (doc in snapshot.documents) {
                        val booking = doc.toObject(Booking::class.java)
                        if (booking != null) {
                            bookings.add(Pair(doc.id, booking))
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun deleteBooking(bookingId: String) {
        FirebaseFirestore.getInstance().collection("bookings").document(bookingId)
            .delete()
            .addOnSuccessListener { Toast.makeText(this, "Booking deleted", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { Toast.makeText(this, "Failed to delete booking", Toast.LENGTH_SHORT).show() }
    }

    override fun onDestroy() {
        super.onDestroy()
        bookingsListener?.remove()
    }
} 
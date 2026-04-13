package com.example.eventmanagerapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import androidx.core.content.ContextCompat

class BookingActivity : AppCompatActivity() {

    private lateinit var dateEditText: EditText
    private lateinit var occasionEditText: EditText
    private lateinit var bookNowBtn: Button
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        // Set status bar color to app theme
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)

        // Initialize views
        dateEditText = findViewById(R.id.dateEditText)
        occasionEditText = findViewById(R.id.occasionEditText)
        bookNowBtn = findViewById(R.id.bookNowBtn)

        // Get organizer ID from intent
        val organizerId = intent.getStringExtra("organizerId")
        val clientId = FirebaseAuth.getInstance().currentUser?.uid

        if (organizerId == null || clientId == null) {
            Toast.makeText(this, "Error: Missing user or organizer info", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Date Picker setup
        dateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(this,
                { _, year, month, dayOfMonth ->
                    selectedDate = "$year-${month + 1}-$dayOfMonth"
                    dateEditText.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = System.currentTimeMillis() // Block past dates
            datePicker.show()
        }

        // Book Now Button Click
        bookNowBtn.setOnClickListener {
            val occasion = occasionEditText.text.toString().trim()

            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bookingData = hashMapOf(
                "clientId" to clientId,
                "organizerId" to organizerId,
                "date" to selectedDate,
                "occasion" to occasion,
                "status" to "pending"
            )

            FirebaseFirestore.getInstance()
                .collection("bookings")
                .add(bookingData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Booking request sent!", Toast.LENGTH_LONG).show()
                    finish() // Go back after booking
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}

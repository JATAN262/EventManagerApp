package com.example.eventmanagerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class OrganizerDetailsActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var nameView: TextView
    private lateinit var bioView: TextView
    private lateinit var locationView: TextView
    private lateinit var priceView: TextView
    private lateinit var bookBtn: Button
    private lateinit var chatBtn: Button
    // Removed: portfolioDescView, servicesView, imagesView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organizer_details)

        imageView = findViewById(R.id.organizerImageView)
        nameView = findViewById(R.id.organizerNameView)
        bioView = findViewById(R.id.organizerBioView)
        locationView = findViewById(R.id.organizerLocationView)
        priceView = findViewById(R.id.organizerPriceView)
        bookBtn = findViewById(R.id.bookNowBtn)
        chatBtn = findViewById(R.id.chatBtn)
        // Removed: portfolioDescView, servicesView, imagesView

        val organizerId = intent.getStringExtra("organizerId") ?: return
        Log.d("OrganizerDetails", "Viewing organizerId: $organizerId")

        // Fetch organizer details from Firestore
        FirebaseFirestore.getInstance().collection("organizers")
            .document(organizerId)
            .get()
            .addOnSuccessListener { doc ->
                nameView.text = doc.getString("name")
                bioView.text = doc.getString("bio")
                locationView.text = doc.getString("location")
                priceView.text = doc.getString("priceRange")
                Glide.with(this).load(doc.getString("imageUrl")).into(imageView)
                // Removed: portfolio fields
            }

        // Handle Book Now click
        bookBtn.setOnClickListener {
            val intent = Intent(this, BookingActivity::class.java)
            intent.putExtra("organizerId", organizerId)
            startActivity(intent)
        }

        // Handle Chat click - Fixed to pass correct intent extra
        chatBtn.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("otherUserId", organizerId)
            startActivity(intent)
        }
    }
}

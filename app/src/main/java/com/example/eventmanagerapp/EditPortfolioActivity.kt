package com.example.eventmanagerapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.eventmanagerapp.Model.Organizer
import android.util.Log
import com.google.firebase.firestore.SetOptions

class EditPortfolioActivity : AppCompatActivity() {
    private lateinit var descriptionEditText: EditText
    private lateinit var serviceEditText: EditText
    private lateinit var addServiceBtn: Button
    private lateinit var servicesListView: ListView
    private lateinit var imageEditText: EditText
    private lateinit var addImageBtn: Button
    private lateinit var imagesListView: ListView
    private lateinit var saveBtn: Button

    private val services = mutableListOf<String>()
    private val images = mutableListOf<String>()
    private lateinit var servicesAdapter: ArrayAdapter<String>
    private lateinit var imagesAdapter: ArrayAdapter<String>

    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_portfolio)

        descriptionEditText = findViewById(R.id.portfolioDescriptionEditText)
        serviceEditText = findViewById(R.id.serviceEditText)
        addServiceBtn = findViewById(R.id.addServiceBtn)
        servicesListView = findViewById(R.id.servicesListView)
        imageEditText = findViewById(R.id.imageEditText)
        addImageBtn = findViewById(R.id.addImageBtn)
        imagesListView = findViewById(R.id.imagesListView)
        saveBtn = findViewById(R.id.savePortfolioBtn)

        servicesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, services)
        servicesListView.adapter = servicesAdapter
        imagesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, images)
        imagesListView.adapter = imagesAdapter

        addServiceBtn.setOnClickListener {
            val service = serviceEditText.text.toString().trim()
            if (service.isNotEmpty()) {
                services.add(service)
                servicesAdapter.notifyDataSetChanged()
                serviceEditText.text.clear()
            }
        }
        servicesListView.setOnItemClickListener { _, _, position, _ ->
            services.removeAt(position)
            servicesAdapter.notifyDataSetChanged()
        }

        addImageBtn.setOnClickListener {
            val imageUrl = imageEditText.text.toString().trim()
            if (imageUrl.isNotEmpty()) {
                images.add(imageUrl)
                imagesAdapter.notifyDataSetChanged()
                imageEditText.text.clear()
            }
        }
        imagesListView.setOnItemClickListener { _, _, position, _ ->
            images.removeAt(position)
            imagesAdapter.notifyDataSetChanged()
        }

        loadPortfolio()

        saveBtn.setOnClickListener {
            savePortfolio()
        }
    }

    private fun loadPortfolio() {
        if (currentUserId == null) return
        db.collection("organizers").document(currentUserId).get()
            .addOnSuccessListener { doc ->
                val organizer = doc.toObject(Organizer::class.java)
                organizer?.let {
                    Log.d("EditPortfolio", "Loaded portfolio: $it")
                    descriptionEditText.setText(it.portfolioDescription)
                    services.clear()
                    services.addAll(it.services)
                    servicesAdapter.notifyDataSetChanged()
                    images.clear()
                    images.addAll(it.portfolioImages)
                    imagesAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Portfolio loaded", Toast.LENGTH_SHORT).show()
                } ?: run {
                    Toast.makeText(this, "No portfolio found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("EditPortfolio", "Failed to load portfolio: ${e.message}")
                Toast.makeText(this, "Failed to load portfolio: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun savePortfolio() {
        if (currentUserId == null) {
            Toast.makeText(this, "No user ID!", Toast.LENGTH_SHORT).show()
            Log.e("EditPortfolio", "No user ID for saving portfolio")
            return
        }
        val description = descriptionEditText.text.toString().trim()
        val data = mapOf(
            "portfolioDescription" to description,
            "services" to services,
            "portfolioImages" to images
        )
        Log.d("EditPortfolio", "Saving portfolio for user: $currentUserId, data: $data")
        db.collection("organizers").document(currentUserId)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("EditPortfolio", "Portfolio updated: desc=$description, services=$services, images=$images")
                Toast.makeText(this, "Portfolio updated!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("EditPortfolio", "Failed to update portfolio: ${e.message}")
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
} 
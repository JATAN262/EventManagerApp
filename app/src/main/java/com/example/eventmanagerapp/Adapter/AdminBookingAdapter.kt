package com.example.eventmanagerapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagerapp.Model.Booking
import com.example.eventmanagerapp.databinding.ItemBookingAdminBinding

class AdminBookingAdapter(
    private val bookings: List<Pair<String, Booking>>,
    private val onAction: (String, String) -> Unit
) : RecyclerView.Adapter<AdminBookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(val binding: ItemBookingAdminBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val (id, booking) = bookings[position]
        with(holder.binding) {
            clientId.text = "Client ID: ${booking.clientId}"
            organizerId.text = "Organizer ID: ${booking.organizerId}"
            bookingDate.text = "Date: ${booking.date}"
            occasion.text = "Occasion: ${booking.occasion}"
            status.text = "Status: ${booking.status}"

            acceptBtn.setOnClickListener { onAction(id, "accepted") }
            rejectBtn.setOnClickListener { onAction(id, "rejected") }
            deleteBtn.setOnClickListener { onAction(id, "delete") }
        }
    }

    override fun getItemCount() = bookings.size
}

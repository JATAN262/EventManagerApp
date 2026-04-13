package com.example.eventmanagerapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagerapp.Model.Booking
import com.example.eventmanagerapp.R
import com.google.android.material.button.MaterialButton
import android.widget.Toast

class BookingAdapter(
    private val bookings: List<Pair<String, Booking>>,
    private val onAction: (bookingId: String, newStatus: String) -> Unit,
    private val onChatClick: (clientId: String) -> Unit,
    private val showActionButtons: Boolean // new parameter
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val date = view.findViewById<TextView>(R.id.bookingDate)
        val occasion = view.findViewById<TextView>(R.id.bookingOccasion)
        val client = view.findViewById<TextView>(R.id.bookingClient)
        val status = view.findViewById<TextView>(R.id.bookingStatus)
        val acceptBtn = view.findViewById<MaterialButton>(R.id.acceptBtn)
        val rejectBtn = view.findViewById<MaterialButton>(R.id.rejectBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val (bookingId, booking) = bookings[position]
        holder.date.text = "Date: ${booking.date}"
        holder.occasion.text = "Occasion: ${booking.occasion}"
        holder.client.text = "Client: ${booking.clientId.take(10)}..."

        // Always show status badge for all users
        holder.status.text = booking.status.replaceFirstChar { it.uppercaseChar() }
        when (booking.status.trim().lowercase()) {
            "pending" -> holder.status.setBackgroundResource(R.drawable.status_pending_background)
            "accepted" -> holder.status.setBackgroundResource(R.drawable.status_accepted_background)
            "rejected" -> holder.status.setBackgroundResource(R.drawable.status_rejected_background)
            else -> holder.status.setBackgroundResource(R.drawable.status_pending_background)
        }

        // Always show action buttons for all bookings
        holder.acceptBtn?.visibility = View.VISIBLE
        holder.rejectBtn?.visibility = View.VISIBLE
        holder.acceptBtn?.isEnabled = true
        holder.rejectBtn?.isEnabled = true
        holder.acceptBtn?.setOnClickListener {
            onAction(bookingId, "accepted")
        }
        holder.rejectBtn?.setOnClickListener {
            onAction(bookingId, "rejected")
        }

        holder.view.findViewById<Button>(R.id.chatBtn).setOnClickListener {
            onChatClick(booking.clientId)
        }
    }

    override fun getItemCount() = bookings.size
}

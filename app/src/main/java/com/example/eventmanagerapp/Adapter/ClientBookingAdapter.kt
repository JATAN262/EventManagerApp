package com.example.eventmanagerapp.Adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagerapp.Model.Booking
import com.example.eventmanagerapp.R

class ClientBookingAdapter(
    private val bookings: List<Pair<String, Booking>>,
    private val onDelete: (bookingId: String) -> Unit
) : RecyclerView.Adapter<ClientBookingAdapter.ClientBookingViewHolder>() {

    inner class ClientBookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val occasion: TextView = view.findViewById(R.id.bookingOccasion)
        val date: TextView = view.findViewById(R.id.bookingDate)
        val organizer: TextView = view.findViewById(R.id.bookingOrganizer)
        val statusBadge: TextView = view.findViewById(R.id.bookingStatusBadge)
        val deleteBtn: Button = view.findViewById(R.id.deleteBookingBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientBookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_client_booking, parent, false)
        return ClientBookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientBookingViewHolder, position: Int) {
        val (bookingId, booking) = bookings[position]
        holder.occasion.text = booking.occasion
        holder.date.text = booking.date
        holder.organizer.text = booking.organizerId // You may want to resolve organizer name
        holder.statusBadge.text = booking.status.replaceFirstChar { it.uppercaseChar() }
        holder.statusBadge.setTypeface(null, Typeface.BOLD)
        when (booking.status.trim().lowercase()) {
            "pending" -> holder.statusBadge.setBackgroundResource(R.drawable.status_pending_background)
            "accepted" -> holder.statusBadge.setBackgroundResource(R.drawable.status_accepted_background)
            "rejected" -> holder.statusBadge.setBackgroundResource(R.drawable.status_rejected_background)
            else -> holder.statusBadge.setBackgroundResource(R.drawable.status_pending_background)
        }
        holder.deleteBtn.setOnClickListener { onDelete(bookingId) }
    }

    override fun getItemCount() = bookings.size
} 
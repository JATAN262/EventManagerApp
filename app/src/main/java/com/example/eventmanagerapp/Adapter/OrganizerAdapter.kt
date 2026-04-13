package com.example.eventmanagerapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventmanagerapp.Model.Organizer
import com.example.eventmanagerapp.databinding.ItemOrganizerBinding

class OrganizerAdapter(
    private val organizerList: List<Organizer>,
    private val onDetailsClick: (Organizer) -> Unit,
    private val onChatClick: (Organizer) -> Unit,
    @Suppress("UNUSED_PARAMETER")
    private val bookingsStatusMap: Map<String, List<com.example.eventmanagerapp.Model.Booking>> = emptyMap() // no longer used
) : RecyclerView.Adapter<OrganizerAdapter.OrganizerViewHolder>() {

    inner class OrganizerViewHolder(val binding: ItemOrganizerBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganizerViewHolder {
        val binding = ItemOrganizerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrganizerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrganizerViewHolder, position: Int) {
        val organizer = organizerList[position]
        with(holder.binding) {
            organizerName.text = organizer.name
            organizerLocation.text = organizer.location
            organizerPrice.text = organizer.priceRange
            Glide.with(root.context).load(organizer.imageUrl).into(organizerImage)
            // No status badge logic here
            detailsBtn.setOnClickListener { onDetailsClick(organizer) }
            chatBtn.setOnClickListener { onChatClick(organizer) }
        }
    }

    override fun getItemCount() = organizerList.size
}

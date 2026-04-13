package com.example.eventmanagerapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagerapp.Model.ChatMessage
import com.example.eventmanagerapp.R

interface ChatAdapterListener {
    fun onMessageLongClick(message: ChatMessage, position: Int)
}

class ChatAdapter(
    private val messages: List<ChatMessage>, 
    private val currentUserId: String,
    private val listener: ChatAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        
        if (holder is SentMessageViewHolder) {
            if (message.deleted) {
                holder.messageText.text = holder.itemView.context.getString(R.string.this_message_was_deleted)
                holder.messageText.alpha = 0.6f
                holder.messageText.setBackgroundResource(R.drawable.deleted_message_background)
                holder.messageText.setTextColor(holder.itemView.context.getColor(android.R.color.darker_gray))
            } else {
                holder.messageText.text = message.message
                holder.messageText.alpha = 1.0f
                holder.messageText.setBackgroundResource(R.drawable.status_accepted_background)
                holder.messageText.setTextColor(holder.itemView.context.getColor(android.R.color.white))
            }
            
            // Add long click listener for sent messages (user can delete their own messages)
            holder.itemView.setOnLongClickListener {
                listener.onMessageLongClick(message, position)
                true
            }
        } else if (holder is ReceivedMessageViewHolder) {
            if (message.deleted) {
                holder.messageText.text = holder.itemView.context.getString(R.string.this_message_was_deleted)
                holder.messageText.alpha = 0.6f
                holder.messageText.setBackgroundResource(R.drawable.deleted_message_background)
                holder.messageText.setTextColor(holder.itemView.context.getColor(android.R.color.darker_gray))
            } else {
                holder.messageText.text = message.message
                holder.messageText.alpha = 1.0f
                holder.messageText.setBackgroundResource(R.drawable.status_pending_background)
                holder.messageText.setTextColor(holder.itemView.context.getColor(R.color.text_primary))
            }
            
            // Add long click listener for received messages (show info only)
            holder.itemView.setOnLongClickListener {
                listener.onMessageLongClick(message, position)
                true
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.sentMessageText)
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.receivedMessageText)
    }
}

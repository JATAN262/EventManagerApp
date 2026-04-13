package com.example.eventmanagerapp

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagerapp.Adapter.ChatAdapter
import com.example.eventmanagerapp.Adapter.ChatAdapterListener
import com.example.eventmanagerapp.Model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.view.inputmethod.EditorInfo
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity(), ChatAdapterListener {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendBtn: Button
    private lateinit var backButton: ImageView
    private lateinit var chatTitle: TextView
    private lateinit var chatStatus: TextView
    
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter
    private lateinit var chatId: String
    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private var otherUserId: String = ""
    private var otherUserName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initialize views
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        sendBtn = findViewById(R.id.sendBtn)
        backButton = findViewById(R.id.backButton)
        chatTitle = findViewById(R.id.chatTitle)
        chatStatus = findViewById(R.id.chatStatus)

        // Get other user ID from intent
        otherUserId = intent.getStringExtra("otherUserId").orEmpty()
        if (otherUserId.isEmpty()) {
            finish()
            return
        }

        // Generate chat ID
        chatId = generateChatId(currentUserId, otherUserId)

        // Setup RecyclerView
        adapter = ChatAdapter(messages, currentUserId, this)
        chatRecyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        chatRecyclerView.adapter = adapter

        // Load other user's information
        loadOtherUserInfo()

        // Load existing messages
        loadMessages()

        // Setup click listeners
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Back button
        backButton.setOnClickListener {
            finish()
        }

        // Send button
        sendBtn.setOnClickListener {
            sendMessage()
        }

        // Send on Enter/Send key (keyboard)
        messageEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
                sendBtn.performClick()
                true
            } else {
                false
            }
        }
    }

    private fun loadOtherUserInfo() {
        db.collection("users").document(otherUserId).get()
            .addOnSuccessListener { doc ->
                otherUserName = doc.getString("name") ?: "User"
                chatTitle.text = otherUserName
                chatStatus.text = doc.getString("role") ?: ""
            }
    }

    private fun loadMessages() {
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    messages.clear()
                    for (doc in snapshot.documents) {
                        val msg = doc.toObject(ChatMessage::class.java)
                        if (msg != null) messages.add(msg)
                    }
                    adapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(messages.size - 1)
                }
            }
    }

    private fun sendMessage() {
        val text = messageEditText.text.toString().trim()
        if (text.isEmpty()) return
        val msg = ChatMessage(
            senderId = currentUserId,
            receiverId = otherUserId,
            message = text,
            timestamp = System.currentTimeMillis()
        )
        db.collection("chats").document(chatId).collection("messages")
            .add(msg)
            .addOnSuccessListener {
                messageEditText.text.clear()
            }
    }

    private fun generateChatId(user1: String, user2: String): String {
        return if (user1 < user2) user1 + "_" + user2 else user2 + "_" + user1
    }

    // Implementation of ChatAdapterListener
    override fun onMessageLongClick(message: ChatMessage, position: Int) {
        if (message.senderId == currentUserId) {
            // User can delete their own messages
            showDeleteDialog(message)
        } else {
            // Show message info for received messages
            showMessageInfoDialog(message)
        }
    }

    private fun showDeleteDialog(message: ChatMessage) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_message))
            .setMessage(getString(R.string.delete_message_confirmation))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deleteMessage(message)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showMessageInfoDialog(message: ChatMessage) {
        val timeString = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
            .format(Date(message.timestamp))
        
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.message_info))
            .setMessage("Sent on $timeString")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun deleteMessage(message: ChatMessage) {
        // Update the message in Firestore to mark it as deleted
        db.collection("chats").document(chatId).collection("messages")
            .whereEqualTo("timestamp", message.timestamp)
            .whereEqualTo("senderId", message.senderId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.update("deleted", true)
                        .addOnSuccessListener {
                            Toast.makeText(this, getString(R.string.message_deleted), Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "${getString(R.string.failed_to_delete_message)}: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "${getString(R.string.failed_to_delete_message)}: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

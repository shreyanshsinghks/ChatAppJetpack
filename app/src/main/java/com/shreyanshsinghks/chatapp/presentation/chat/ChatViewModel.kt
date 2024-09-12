package com.shreyanshsinghks.chatapp.presentation.chat

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import com.shreyanshsinghks.chatapp.model.MessageModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {
    private val _messages = MutableStateFlow<List<MessageModel>>(emptyList())
    val messages = _messages.asStateFlow()
    private val db = Firebase.database

    fun sendMessage(channelId: String, messageText: String?, imageUrl: String? = null) {
        val message = MessageModel(
            id = db.reference.push().key ?: UUID.randomUUID().toString(),
            senderId = Firebase.auth.currentUser?.uid ?: "",
            message = messageText,
            createdAt = System.currentTimeMillis(),
            senderName = Firebase.auth.currentUser?.displayName ?: "",
            senderImage = Firebase.auth.currentUser?.photoUrl.toString(),
            imageUrl = imageUrl
        )
        db.reference.child("messages").child(channelId).push().setValue(message)
    }

    fun listenForMessages(channelId: String) {
        db.getReference("messages").child(channelId).orderByChild("createdAt")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<MessageModel>()
                    snapshot.children.forEach { data ->
                        val message = data.getValue(MessageModel::class.java)
                        message?.let {
                            list.add(it)
                        }
                    }
                    _messages.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error

                }
            })
    }

    fun sendImageMessage(uri: Uri, channelId: String) {
        val storageRef = Firebase.storage.reference.child("images/${UUID.randomUUID()}")
        storageRef.putFile(uri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { url ->
                sendMessage(channelId, "", url.toString())
            }
        }
    }
}
package com.akeemrotimi.chatapp.core.data

import com.akeemrotimi.chatapp.core.data.local.ChatDao
import com.akeemrotimi.chatapp.core.data.local.MessageDao
import com.akeemrotimi.chatapp.core.data.local.toDomain
import com.akeemrotimi.chatapp.core.data.local.toEntity
import com.akeemrotimi.chatapp.core.data.model.Chat
import com.akeemrotimi.chatapp.core.data.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepository
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
        private val chatDao: ChatDao,
        private val messageDao: MessageDao,
    ) {
        private val scope = CoroutineScope(Dispatchers.IO)

        fun getChatsFlow(currentUserId: String): Flow<List<Chat>> {
            syncChats(currentUserId)
            return chatDao.getChatsFlow().map { entities ->
                entities.map { it.toDomain() }
            }
        }

        private fun syncChats(currentUserId: String) {
            firestore
                .collection("conversations")
                .whereArrayContains("members", currentUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    snapshot?.let { snap ->
                        val chats =
                            snap.documents.mapNotNull { doc ->
                                doc.toObject(Chat::class.java)?.copy(id = doc.id)
                            }
                        scope.launch {
                            chatDao.insertChats(chats.map { it.toEntity() })
                        }
                    }
                }
        }

        suspend fun getChat(chatId: String): Chat? {
            val local =
                chatDao
                    .getChatsFlow()
                    .firstOrNull()
                    ?.firstOrNull { it.id == chatId }
                    ?.toDomain()

            if (local != null) return local

            // FIXED: Changed from "chats" to "conversations"
            val doc =
                firestore
                    .collection("conversations")
                    .document(chatId)
                    .get()
                    .await()
            return doc.toObject(Chat::class.java)?.copy(id = doc.id)?.also { chat ->
                chatDao.insertChat(chat.toEntity())
            }
        }

        fun getMessagesFlow(chatId: String): Flow<List<Message>> {
            syncMessages(chatId)
            return messageDao.getMessagesFlow(chatId).map { entities ->
                entities.map { it.toDomain() }
            }
        }

        private fun syncMessages(chatId: String) {
            // FIXED: Changed from "chats" to "conversations"
            firestore
                .collection("conversations")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    snapshot?.let { snap ->
                        val messages =
                            snap.documents.mapNotNull { doc ->
                                doc.toObject(Message::class.java)?.copy(id = doc.id)
                            }
                        scope.launch {
                            messageDao.insertMessages(messages.map { it.toEntity() })
                        }
                    }
                }
        }

        suspend fun sendMessage(
            chatId: String,
            message: Message,
        ) {
            messageDao.insertMessage(message.copy(isSynced = false).toEntity())

            try {
                // FIXED: Changed from "chats" to "conversations"
                val ref =
                    firestore
                        .collection("conversations")
                        .document(chatId)
                        .collection("messages")
                        .add(message.copy(isSynced = true))
                        .await()

                messageDao.markMessageAsSynced(ref.id)
            } catch (_: Exception) {
            }
        }

        suspend fun syncPendingMessages(chatId: String) {
            val unsynced = messageDao.getUnsyncedMessages()
            unsynced.forEach { entity ->
                try {
                    // FIXED: Changed from "chats" to "conversations"
                    val ref =
                        firestore
                            .collection("conversations")
                            .document(chatId)
                            .collection("messages")
                            .add(entity.toDomain().copy(isSynced = true))
                            .await()

                    messageDao.markMessageAsSynced(ref.id)
                } catch (_: Exception) {
                }
            }
        }

        suspend fun createChatIfNotExists(
            currentUserId: String,
            otherUserId: String,
        ): String {
            val query =
                firestore
                    .collection("conversations")
                    .whereArrayContains("members", currentUserId)
                    .get()
                    .await()

            val existing =
                query.documents.firstOrNull { doc ->
                    val members = doc.get("members") as? List<*>
                    members?.contains(otherUserId) == true
                }

            return existing?.id ?: run {
                val chat =
                    mapOf(
                        "members" to listOf(currentUserId, otherUserId),
                        "lastMessage" to "",
                        "timestamp" to System.currentTimeMillis(),
                    )
                firestore
                    .collection("conversations")
                    .add(chat)
                    .await()
                    .id
            }
        }
    }

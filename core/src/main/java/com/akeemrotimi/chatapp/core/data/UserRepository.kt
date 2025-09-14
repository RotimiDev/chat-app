package com.akeemrotimi.chatapp.core.data

import com.akeemrotimi.chatapp.core.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {
    private val usersCollection = firestore.collection("users")

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun getUser(userId: String): User? {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUsers(userIds: List<String>): List<User> {
        return try {
            if (userIds.isEmpty()) return emptyList()

            val snapshot = usersCollection
                .whereIn(FieldPath.documentId(), userIds)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(User::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getUsersFlow(userIds: List<String>): Flow<List<User>> = callbackFlow {
        val subscription = usersCollection.whereIn("id", userIds)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val users = snapshot?.documents?.mapNotNull { it.toObject(User::class.java) } ?: emptyList()
                trySend(users)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun updateOnlineStatus(userId: String, isOnline: Boolean) {
        val updates = mapOf(
            "isOnline" to isOnline,
            "lastSeen" to System.currentTimeMillis()
        )
        usersCollection.document(userId).update(updates).await()
    }

    suspend fun createUser(user: User) {
        usersCollection.document(user.id).set(user).await()
    }
}

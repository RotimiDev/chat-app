package com.akeemrotimi.chatapp.core.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import javax.inject.Inject

class AuthRepository
    @Inject
    constructor(
        private val auth: FirebaseAuth,
    ) {
        fun signIn(
            email: String,
            password: String,
            onResult: (Boolean, String?) -> Unit,
        ) {
            auth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    onResult(task.isSuccessful, task.exception?.message)
                }
        }

        fun signUp(
            email: String,
            password: String,
            displayName: String,
            onResult: (Boolean, String?) -> Unit,
        ) {
            auth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val profileUpdates =
                            userProfileChangeRequest {
                                this.displayName = displayName
                            }

                        user
                            ?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { updateTask ->
                                onResult(updateTask.isSuccessful, updateTask.exception?.message)
                            }
                    } else {
                        onResult(false, task.exception?.message)
                    }
                }
        }

        fun resetPassword(
            email: String,
            onResult: (Boolean, String?) -> Unit,
        ) {
            auth
                .sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    onResult(task.isSuccessful, task.exception?.message)
                }
        }

        fun getCurrentUser(): FirebaseUser? = auth.currentUser
    }

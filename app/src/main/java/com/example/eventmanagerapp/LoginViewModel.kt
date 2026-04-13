package com.example.eventmanagerapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val role: String) : LoginState()
    data class Error(val message: String) : LoginState()
    object PasswordResetSent : LoginState()
}

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser!!.uid
                fetchUserRole(uid)
            }
            .addOnFailureListener {
                _loginState.value = LoginState.Error("Login failed: ${it.message}")
            }
    }

    private fun fetchUserRole(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val role = documentSnapshot.getString("role")
                    if (role == "client" || role == "organizer" || role == "admin") {
                        _loginState.value = LoginState.Success(role!!)
                    } else {
                        _loginState.value = LoginState.Error("Please complete your registration and select a role.")
                    }
                } else {
                    _loginState.value = LoginState.Error("User not found in Firestore. Please register.")
                }
            }
            .addOnFailureListener {
                _loginState.value = LoginState.Error("Failed to fetch user role: ${it.message}")
            }
    }

    fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                _loginState.value = LoginState.PasswordResetSent
            }
            .addOnFailureListener {
                _loginState.value = LoginState.Error("Failed to send reset email: ${it.message}")
            }
    }

    fun clearState() {
        _loginState.value = LoginState.Idle
    }
} 
package com.akkuunamatata.eco_plant.database.users

import com.google.firebase.auth.FirebaseAuth

class UserDatabaseHelper {

    fun createAccount(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null) // Succès
                } else {
                    onResult(false, task.exception?.message) // Erreur
                }
            }
    }

    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null) // Succès
                } else {
                    onResult(false, task.exception?.message) // Erreur
                }
            }
    }

    fun logoutUser() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        val auth = FirebaseAuth.getInstance()
        return auth.currentUser != null
    }
}
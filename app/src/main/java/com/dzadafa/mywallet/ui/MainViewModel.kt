package com.dzadafa.mywallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> = _userId

    init {
        
        if (auth.currentUser != null) {
            _userId.value = auth.currentUser?.uid
        } else {
            
            signInAnonymously()
        }
    }

    private fun signInAnonymously() {
        
        viewModelScope.launch {
            try {
                
                auth.signInAnonymously().await()
                _userId.value = auth.currentUser?.uid
            } catch (e: Exception) {
                
                _userId.value = null
            }
        }
    }
}

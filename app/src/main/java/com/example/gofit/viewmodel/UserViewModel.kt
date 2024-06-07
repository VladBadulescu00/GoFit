package com.example.gofit.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    fun setUserId(userId: String) {
        _userId.value = userId
        // Debug log to check if userId is being set
        println("UserViewModel: setUserId called with userId = $userId")
    }
}

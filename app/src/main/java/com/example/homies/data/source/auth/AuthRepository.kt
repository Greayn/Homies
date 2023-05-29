package com.example.homies.data.source.auth

import com.example.homies.data.model.UniqueId

interface AuthRepository {
    fun getCurrentStudentId(): UniqueId?
    suspend fun login(email: String, password: String): UniqueId
    suspend fun signup(email: String, password: String): UniqueId
    suspend fun sendResetPasswordEmail(email: String)
    fun logout()
}
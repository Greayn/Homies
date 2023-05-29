package com.example.homies.data.source.notification

import com.example.homies.data.model.UniqueId

interface NotificationRepository {
    suspend fun notifyMatchRequestSent(targetStudentFcmToken: String)
    suspend fun notifyMatchRequestAccepted(targetStudentFcmToken: String)
}
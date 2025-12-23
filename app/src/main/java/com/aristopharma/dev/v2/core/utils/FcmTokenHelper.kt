package com.aristopharma.dev.v2.core.utils

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import android.util.Log

object FcmTokenHelper {
    private const val TAG = "FcmTokenHelper"

    suspend fun getFcmToken(): String? {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            Log.d(TAG, "Fetched FCM Token: $token")
            token
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch FCM Token", e)
            null
        }
    }
}

package com.lamaq.housienumberpicker

import android.content.ContentValues.TAG
import android.util.Log

class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }
}
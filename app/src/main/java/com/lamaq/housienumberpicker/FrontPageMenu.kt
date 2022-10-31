package com.lamaq.housienumberpicker

import android.R
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class FrontPageMenu: AppCompatActivity() {
    var singlePhoneBtn: Button? = null
    var multiPhoneBtn: Button? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.lamaq.housienumberpicker.R.layout.front_page_menu)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d(TAG, "FCM: $token")
        })

        singlePhoneBtn = findViewById(com.lamaq.housienumberpicker.R.id.single_phone_button)
        multiPhoneBtn = findViewById(com.lamaq.housienumberpicker.R.id.multi_phone_button)

        multiPhoneBtn?.setTextColor(resources.getColor(R.color.white))

        singlePhoneBtn?.setOnClickListener {
            //pass textToSpeech to BoardActivity
            intent = android.content.Intent(this, BoardActivity::class.java)
            startActivity(intent)
        }

        multiPhoneBtn?.setOnClickListener {
            //pass textToSpeech to AdminMultiPhoneBoardActivity
            intent = android.content.Intent(this, Lobby::class.java)
            startActivity(intent)
        }

    }
}

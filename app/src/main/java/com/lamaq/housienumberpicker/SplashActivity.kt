package com.lamaq.housienumberpicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast

class SplashActivity : AppCompatActivity() {

    private var textToSpeech: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = android.content.Intent(this, FrontPageMenu::class.java)
        startActivity(intent)
        finish()
    }
}
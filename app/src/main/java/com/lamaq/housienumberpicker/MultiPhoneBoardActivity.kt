package com.lamaq.housienumberpicker

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess



class MultiPhoneBoardActivity : AppCompatActivity() {
    var textToSpeech: TextToSpeech? = null
    var mute = false
    private var history = mutableListOf<Int>()
    private var numTV: TextView? = null
    private var codeTV: TextView? = null
    private var last5TV: TextView? = null
    private val numberBoard = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.multi_phone_board)

        numTV = findViewById(R.id.textView)
        codeTV = findViewById(R.id.codeTV)
        last5TV = findViewById(R.id.history5last)

        val code = intent.getStringExtra("code")
        codeTV?.text = "Room Code: $code"

        //create list of textViews till t100
        for (i in 1..100) {
            val id = resources.getIdentifier("t$i", "id", packageName)
            numberBoard.add(findViewById(id))
        }

        for (i in numberBoard) {
            i.setTextColor(resources.getColor(androidx.appcompat.R.color.material_blue_grey_800))
        }
        //initialize text to speech
        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech?.language = resources.configuration.locales[0]
            }
        }

        //add DataChange listener to the database
        val postListner = object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                if (snapshot.exists()) {
                    for (i in snapshot.children) {
                        if (i.key == code) {
                            for (j in i.child("numbers").children) {
                                val num = j.value.toString().toInt()
                                checkForRepeat(num)
                                numTV?.text = "Number: $num"
                                if (!mute){
                                    textToSpeech?.speak(num.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
                                }
                                numberBoard[num - 1].setTextColor(resources.getColor(R.color.white))
                                numberBoard[num - 1].setBackgroundColor(resources.getColor(R.color.pink))

                                if (history.size > 1) {
                                    numberBoard[history[history.lastIndex - 1] - 1].setBackgroundColor(
                                        resources.getColor(R.color.green)
                                    )
                                }
                            }
                        }
                        if (i.child("reset").value.toString().toBoolean()) {
                            history.clear()
                            numTV?.text = "Board has been reset"
                            for (i in numberBoard) {
                                i.setTextColor(resources.getColor(androidx.appcompat.R.color.material_blue_grey_800))
                                i.setBackgroundColor(resources.getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color))
                            }
                            myRef.child(code!!).child("reset").setValue(false)
                        }
                    }
                } else {
                    Toast.makeText(
                        this@MultiPhoneBoardActivity,
                        "Room Code does not exist",
                        Toast.LENGTH_SHORT
                    ).show()
                    history.clear()
                    exitProcess(0)
                }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Toast.makeText(this@MultiPhoneBoardActivity, "Error: $error", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        myRef.addValueEventListener(postListner)

        numTV?.setOnLongClickListener {
            Toast.makeText(this, history.toString(), Toast.LENGTH_LONG).show()
            true
        }
    }
    override fun onDestroy() {
        if (textToSpeech != null) {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
        super.onDestroy()
    }
    private fun checkForRepeat(num: Int) {
        if (history.contains(num)) {
            history.remove(num)
        }
        history.add(num)
        last5TV?.text = "Last 5: ${history.takeLast(5).toString().replace("[", "").replace("]", "").replace(", ", ",")}"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.slave_menu, menu)

        val home = menu!!.findItem(R.id.action_homepage)
        home.setOnMenuItemClickListener {
            history.clear()
            finish()
            true
        }

        val volume = menu.findItem(R.id.action_volume)
        volume.setOnMenuItemClickListener {
            if (volume.isChecked) {
                volume.isChecked = false
                mute = true
                volume.setIcon(R.drawable.ic_baseline_volume_off_24)
            } else {
                volume.isChecked = true
                mute = false
                volume.setIcon(R.drawable.ic_baseline_volume_up_24)
            }
            true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var alertDialog = android.app.AlertDialog.Builder(this)
        alertDialog.setTitle("Home?")
        alertDialog.setMessage("Are you sure you want to go the homepage?")
        alertDialog.setPositiveButton("Yes") { _, _ ->
            history.clear()
            finish()
        }
        alertDialog.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
    }
}
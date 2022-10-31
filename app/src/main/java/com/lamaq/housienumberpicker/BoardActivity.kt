package com.lamaq.housienumberpicker

import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BoardActivity : AppCompatActivity() {

    private var mute = false
    private var history = mutableListOf<Int>()
    private var num: Int = 0
    private var nextBtn: Button? = null
    private var numTV: TextView? = null
    private var undoTV: TextView? = null
    private var marqueeTV: TextView? = null
    private val numberBoard = mutableListOf<TextView>()

    private var undoTimes = 0

    private var textToSpeech: TextToSpeech? = null

    private var fab: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_activity)

        nextBtn = findViewById(R.id.button)
        numTV = findViewById(R.id.textView)
        marqueeTV = findViewById(R.id.marqueeText)
        undoTV = findViewById(R.id.undoTV)
        fab = findViewById(R.id.repeatFAB)

        //create list of textViews till t100
        for (i in 1..100) {
            val id = resources.getIdentifier("t$i", "id", packageName)
            numberBoard.add(findViewById(id))
        }

        for (i in numberBoard) {
            i.setTextColor(resources.getColor(androidx.appcompat.R.color.material_blue_grey_800))
        }

        marqueeTV?.isSelected = true

        undoTV?.text = "Times Undo: $undoTimes"

        nextBtn?.setOnClickListener {
            nextBtn?.text = "Next Number"
            fab?.show()
            num = (1..100).random()
            checkIfRepeat(num)
            if (history.size > 0){
                marqueeTV?.text = "Last 5 numbers are : ${history.takeLast(5).toString().replace("[", "").replace("]", "").replace(", ", ",")}"
            }
            if (history.size == 100) {
                Toast.makeText(this, "All numbers have been called", Toast.LENGTH_SHORT).show()
                nextBtn?.text = "Click the reset button in the menu to start again"
            }
        }

        numTV?.setOnLongClickListener {
            Toast.makeText(this, history.toString(), Toast.LENGTH_LONG).show()
            true
        }

        fab?.setOnClickListener {
            textToSpeech?.speak(num.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onBackPressed() {
        //alert box
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Exit")
        builder.setMessage("Are you sure you want to leave the board?")
        builder.setPositiveButton("Yes") { _, _ ->
            super.onBackPressed()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.show()
    }

    override fun onDestroy() {
        if (textToSpeech != null) {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(resources.configuration.locales[0])
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
                }
                nextBtn?.isEnabled = true
            } else {
                Toast.makeText(this, "Initialization failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val undo = menu!!.findItem(R.id.action_undo)
        undo.setOnMenuItemClickListener {
            if (history.size > 0) {
                undoTimes++
                undoTV?.text = "Times Undo: $undoTimes"
                fab?.hide()
                val lastNum = history.last()
                history.removeAt(history.lastIndex)
                numberBoard[lastNum - 1].setTextColor(resources.getColor(androidx.appcompat.R.color.material_blue_grey_800))
                numberBoard[lastNum - 1].setBackgroundColor(resources.getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color))
                if (history.size == 0) {
                    numTV?.text = "Last Number: none"
                    numTV?.textSize = 20f
                } else {
                    numTV?.text = "Last Number: ${history.last()}"
                    numTV?.textSize = 20f
                }
                num = lastNum
                nextBtn?.text = "Next Number"
                nextBtn?.isEnabled = true
            } else {
                Toast.makeText(this, "No more numbers to undo", Toast.LENGTH_SHORT).show()
            }
            true
        }

        val reset = menu.findItem(R.id.action_reset)
        reset.setOnMenuItemClickListener {
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("RESET")
            builder.setMessage("Are you sure you want to reset the board?")
            builder.setPositiveButton("Yes") { _, _ ->
                undoTimes = 0
                undoTV?.text = "Times Undo: $undoTimes"
                history.clear()
                numTV?.text = "Number Picker"
                nextBtn?.isEnabled = true
                nextBtn?.text = "1st Number"
                for (i in numberBoard) {
                    i.setTextColor(resources.getColor(androidx.appcompat.R.color.material_blue_grey_800))
                    i.setBackgroundColor(resources.getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color))
                }
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.show()
            true

        }

        val home = menu.findItem(R.id.action_homepage)
        home.setOnMenuItemClickListener {
            //alert box
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Exit")
            builder.setMessage("Are you sure you want to leave the board?")
            builder.setPositiveButton("Yes") { _, _ ->
                finish()
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.show()
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

    private fun checkIfRepeat(num: Int) {
        if (history.contains(num)) {
            checkIfRepeat((1..100).random())
        } else {
            history.add(num)
            if (history.size == 100) {
                Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show()
                nextBtn?.isEnabled = false
            }
            numberBoard[num - 1].setTextColor(resources.getColor(R.color.white))
            numberBoard[num - 1].setBackgroundColor(resources.getColor(R.color.pink))

            if (history.size > 1) {
                numberBoard[history[history.lastIndex - 1] - 1].setBackgroundColor(
                    resources.getColor(
                        R.color.green
                    )
                )
            }
            nextBtn?.isEnabled = false
            if (!mute){
                textToSpeech?.speak(num.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
            }
            Handler().postDelayed({
                nextBtn?.isEnabled = true
            }, 1000)
            nextBtn?.isEnabled = true
            if (num < 10) {
                numTV?.textSize = 20f
                numTV?.text = "Number: 0$num"
            } else {
                numTV?.textSize = 20f
                numTV?.text = "Number: $num"
            }
        }
    }
}

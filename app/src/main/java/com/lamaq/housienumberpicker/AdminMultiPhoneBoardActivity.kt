package com.lamaq.housienumberpicker

import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat.postDelayed
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

val database = Firebase.database
val myRef = database.getReference("codes")
class AdminMultiPhoneBoardActivity : AppCompatActivity() {

    private var history = mutableListOf<Int>()
    private var num: Int = 0
    private var nextBtn: Button? = null
    private var numTV: TextView? = null
    private var codeTV: TextView? = null
    private var repeatFAB: FloatingActionButton? = null
    private val numberBoard = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_multi_phone_board)

        nextBtn = findViewById(R.id.button)
        numTV = findViewById(R.id.textView)
        codeTV = findViewById(R.id.codeTV)
        repeatFAB = findViewById(R.id.repeatFAB)

        val code = intent.getStringExtra("code")
        codeTV?.text = "Room Code: $code"
        val alertDialog = android.app.AlertDialog.Builder(this).create()
        alertDialog.setTitle("Code: $code")
        alertDialog.setMessage("Share this code with your friends to play together")
        alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "OK"
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()

        //create list of textViews till t100
        for (i in 1..100) {
            val id = resources.getIdentifier("t$i", "id", packageName)
            numberBoard.add(findViewById(id))
        }

        for (i in numberBoard){
            i.setTextColor(resources.getColor(androidx.appcompat.R.color.material_blue_grey_800))
        }

        repeatFAB?.setOnClickListener {
            myRef.child(com.lamaq.housienumberpicker.code).child("repeat").setValue(true)
            repeatFAB?.isEnabled = false
            Handler().postDelayed({
                repeatFAB?.isEnabled = true
            }, 1000)
        }

        nextBtn?.setOnClickListener {
            nextBtn?.text = "Next Number"
            num = (1..100).random()
            myRef.child(com.lamaq.housienumberpicker.code).child("repeat").setValue(false)
            myRef.child(com.lamaq.housienumberpicker.code).child("reset").setValue(false)
            checkIfRepeat(num)
            if (history.size == 100) {
                Toast.makeText(this, "All numbers have been called", Toast.LENGTH_SHORT).show()
                nextBtn?.text = "Click the reset button in the menu to start again"
            }
        }

        numTV?.setOnLongClickListener {
            Toast.makeText(this, history.toString(), Toast.LENGTH_LONG).show()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)

        val reset = menu!!.findItem(R.id.action_reset)
        reset.setOnMenuItemClickListener {
            history.clear()
            myRef.child(code).child("numbers").removeValue()
            //if board is reset the change board in MultiPhoneBoardActivity
            myRef.child(code).child("reset").setValue(true)
            numTV?.text = "Number Picker"
            nextBtn?.isEnabled = true
            nextBtn?.text = "1st Number"
            for (i in numberBoard) {
                i.setTextColor(resources.getColor(androidx.appcompat.R.color.material_blue_grey_800))
                i.setBackgroundColor(resources.getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color))
            }
            true
        }

        val home = menu.findItem(R.id.action_homepage)
        home.setOnMenuItemClickListener {
            //alert dialog to confirm
            var alertDialog = android.app.AlertDialog.Builder(this)
            alertDialog.setTitle("Are you sure you want to go to the homepage?")
            alertDialog.setMessage("This will end the game and you will have to create a new room code")
            alertDialog.setPositiveButton("Yes") { _, _ ->
                myRef.child(code).child("numbers").removeValue()
                myRef.child(code).removeValue()
                finish()
            }
            alertDialog.setNegativeButton("No") { _, _ -> }
            alertDialog.show()
            true
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun checkIfRepeat(num: Int){
        if (history.contains(num)){
            checkIfRepeat((1..100).random())
        }else {
            history.add(num)
            myRef.child(code).child("numbers").setValue(history)
            if (history.size == 100){
                Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show()
                nextBtn?.isEnabled = false
            }
            numberBoard[num - 1].setTextColor(resources.getColor(R.color.white))
            numberBoard[num - 1].setBackgroundColor(resources.getColor(R.color.pink))

            if (history.size > 1) {
                numberBoard[history[history.lastIndex - 1] - 1].setBackgroundColor(resources.getColor(R.color.green))
            }

            numTV?.text = "Number: $num"
        }
    }
}

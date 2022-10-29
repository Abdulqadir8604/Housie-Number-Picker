package com.lamaq.housienumberpicker

import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BoardActivity : AppCompatActivity() {

    private var history = mutableListOf<Int>()
    private var num: Int = 0
    private var nextBtn: Button? = null
    private var numTV: TextView? = null
    private val numberBoard = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_activity)

        nextBtn = findViewById(R.id.button)
        numTV = findViewById(R.id.textView)

        //create list of textViews till t100
        for (i in 1..100) {
            val id = resources.getIdentifier("t$i", "id", packageName)
            numberBoard.add(findViewById(id))
        }

        for (i in numberBoard){
            i.setTextColor(resources.getColor(androidx.appcompat.R.color.material_blue_grey_800))
        }

        nextBtn?.setOnClickListener {
            nextBtn?.text = "Next Number"
            num = (1..100).random()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val undo = menu!!.findItem(R.id.action_undo)
        undo.setOnMenuItemClickListener{
            if (history.size > 0) {
                val lastNum = history.last()
                history.removeAt(history.lastIndex)
                numberBoard[lastNum - 1].setTextColor(resources.getColor(androidx.appcompat.R.color.material_blue_grey_800))
                numberBoard[lastNum - 1].setBackgroundColor(resources.getColor(com.google.android.material.R.color.mtrl_btn_transparent_bg_color))
                if (history.size == 0) {
                    numTV?.text = "Last Number: none"
                }else
                    numTV?.text = "Last Number: ${history.last()}"
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
            history.clear()
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
            finish()
            true
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun checkIfRepeat(num: Int){
        if (history.contains(num)){
            checkIfRepeat((1..100).random())
        }else {
            history.add(num)
            if (history.size == 100){
               Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show()
                nextBtn?.isEnabled = false
            }
            numberBoard[num - 1].setTextColor(resources.getColor(R.color.white))
            numberBoard[num - 1].setBackgroundColor(resources.getColor(R.color.pink))

            if (history.size > 1) {
                numberBoard[history[history.lastIndex - 1] - 1].setBackgroundColor(resources.getColor(R.color.green))
            }

            if (num < 10) {
                numTV?.text = "Next Number: 0$num"
            } else {
                numTV?.text = "Next Number: $num"
            }
        }
    }
}

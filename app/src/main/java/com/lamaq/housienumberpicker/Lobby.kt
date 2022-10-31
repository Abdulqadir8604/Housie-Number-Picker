package com.lamaq.housienumberpicker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

var isCodeMaker = true
var code = "null"
var codeFound = false
var checkTemp = true
var keyValue: String = "null"

class Lobby : AppCompatActivity() {
    lateinit var headTV: TextView
    lateinit var codeEdt: EditText
    lateinit var createCodeBtn: Button
    lateinit var joinCodeBtn: Button
    lateinit var loadingPB: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        headTV = findViewById(R.id.title)
        codeEdt = findViewById(R.id.code_edit_text)
        createCodeBtn = findViewById(R.id.create_code_btn)
        joinCodeBtn = findViewById(R.id.join_code_btn)
        loadingPB = findViewById(R.id.loading)

        val database = Firebase.database
        val myRef = database.getReference("codes")

        createCodeBtn.setOnClickListener {
            isCodeMaker = true
            loadingPB.visibility = ProgressBar.VISIBLE
            headTV.text = "Creating Code..."
            createCodeBtn.isVisible = false
            joinCodeBtn.isVisible = false
            codeEdt.isEnabled = false
            code = (1000..9999).random().toString()
            codeEdt.setText("....")
            myRef.child(code).setValue("null")
            myRef.child(code).child("delete_lobby").setValue(false)
            Handler().postDelayed({
                loadingPB.visibility = ProgressBar.INVISIBLE
                headTV.text = "Code Created"
                codeEdt.setText(code)
                createCodeBtn.isVisible= true
                joinCodeBtn.isVisible = true
                codeEdt.isEnabled = true
                val intent = Intent(this, AdminMultiPhoneBoardActivity::class.java)
                intent.putExtra("code", code)
                startActivity(intent)
            }, 2000)
        }

        codeEdt.setOnClickListener {
            createCodeBtn.isVisible = false
        }

        joinCodeBtn.setOnClickListener {
            isCodeMaker = false
            loadingPB.visibility = ProgressBar.VISIBLE
            headTV.text = "Joining Code..."
            createCodeBtn.isVisible = false
            joinCodeBtn.isVisible = false
            codeEdt.isEnabled = false
            code = codeEdt.text.toString()
            //validate code
            if(code.length == 4) {
                myRef.child(code).get().addOnSuccessListener {
                    if (it.exists()) {
                        loadingPB.visibility = ProgressBar.INVISIBLE
                        headTV.text = "Code Found"
                        createCodeBtn.isVisible = true
                        joinCodeBtn.isVisible = true
                        codeEdt.isEnabled = true
                        val intent = Intent(this, MultiPhoneBoardActivity::class.java)
                        intent.putExtra("code", code)
                        startActivity(intent)
                    } else {
                        loadingPB.visibility = ProgressBar.INVISIBLE
                        headTV.text = "Code Not Found"
                        createCodeBtn.isVisible = true
                        joinCodeBtn.isVisible = true
                        codeEdt.isEnabled = true
                    }
                }
            } else {
                loadingPB.visibility = ProgressBar.INVISIBLE
                headTV.text = "Code Not Found"
                createCodeBtn.isVisible = true
                joinCodeBtn.isVisible = true
                codeEdt.isEnabled = true
            }
        }

    }
}
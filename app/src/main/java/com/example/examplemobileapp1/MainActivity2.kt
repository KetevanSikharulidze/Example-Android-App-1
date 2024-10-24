package com.example.examplemobileapp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity2 : AppCompatActivity() {

    lateinit var nameTextView: TextView
    lateinit var ageTextView : TextView
    lateinit var heightTextView : TextView
    lateinit var aliveTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        init()
    }

    private fun init(){
        nameTextView = findViewById(R.id.nameTextView)
        ageTextView = findViewById(R.id.ageTextView)
        heightTextView = findViewById(R.id.heightTextView)
        aliveTextView = findViewById(R.id.aliveTextView)

        nameTextView.text = intent.extras?.getString("name", "no name")
        ageTextView.text = intent.extras?.getInt("age",0).toString()
        heightTextView.text = intent.extras?.getDouble("height",0.0).toString()
        aliveTextView.text = intent.extras?.getBoolean("alive", false).toString()

    }

}
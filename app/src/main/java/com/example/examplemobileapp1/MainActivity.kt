package com.example.examplemobileapp1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    lateinit var nameEditText: EditText
    lateinit var ageEditText: EditText
    lateinit var heightEditText: EditText
    lateinit var aliveCheckBox: CheckBox
    lateinit var enterBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init(){
        nameEditText = findViewById(R.id.nameEditText)
        ageEditText = findViewById(R.id.ageEditText)
        heightEditText = findViewById(R.id.heightEditText)
        aliveCheckBox = findViewById(R.id.aliveCheckBox)
        enterBtn = findViewById(R.id.enterBtn)

        enterBtn.setOnClickListener {
            val name = nameEditText.text.toString()
            val age = ageEditText.text.toString().toInt()
            val height = heightEditText.text.toString().toDouble()
            var alive = false

            if (aliveCheckBox.isChecked) {
                alive = true
                val intent = Intent(this, MainActivity2::class.java)
                intent.putExtra("name", name)
                intent.putExtra("age", age)
                intent.putExtra("height", height)
                intent.putExtra("alive", alive)
                startActivity(intent)
//                finish()

            } else {
                Toast.makeText(this, "alive is not checked!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



        }






    }
}
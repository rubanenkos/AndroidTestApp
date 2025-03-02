package com.example.kotlintestapp

import ApiClient
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val apiClient = ApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainLabel = findViewById<TextView>(R.id.main_label)
        val userData: EditText = findViewById(R.id.user_data)
        val startButton: Button = findViewById(R.id.start_button)
        val resultTextView: TextView = findViewById(R.id.resultTextView)

        startButton.setOnClickListener {
            apiClient.fetchInventory(this) { responseText ->
                Handler(Looper.getMainLooper()).post {
                    resultTextView.text = responseText
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

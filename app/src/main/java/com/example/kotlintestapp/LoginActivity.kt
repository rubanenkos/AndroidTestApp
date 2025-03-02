package com.example.kotlintestapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginField: EditText = findViewById(R.id.login_field)
        val passwordField: EditText = findViewById(R.id.password_field)
        val loginButton: Button = findViewById(R.id.login_button)

        loginButton.setOnClickListener {
            // TODO: Password verification logic
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish() // Close LoginActivity

            val intent = Intent(this, TabsActivity::class.java)
            startActivity(intent)
            finish()

        }
    }
}

package com.example.kotlintestapp

import ApiClient
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var baseUrl: String
    private val apiClient = ApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginField: EditText = findViewById(R.id.login_field)
        val passwordField: EditText = findViewById(R.id.password_field)
        val loginButton: Button = findViewById(R.id.login_button)
        baseUrl = getString(R.string.base_url)

        loginButton.setOnClickListener {
            val email = loginField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                    "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val url = "$baseUrl/login"
        val json = JSONObject()
        json.put("email", email)
        json.put("password", password)
        json.put("name", "")

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json.toString()
        )

        Log.d("LoginRequest", "Request Body: ${json.toString()}")

        apiClient.postData(this, url, requestBody) { response, statusCode ->
            Log.d("LoginResponse", "Response: $response")

            runOnUiThread {
                try {
                    if (statusCode == 200) {
                        val responseObject = JSONObject(response)
                        val error = responseObject.optString("error")
                        val message = responseObject.optString("message")

                        if (error == "Internal Server Error" && message == "Invalid credentials") {
                            Toast.makeText(this,
                                "Invalid credentials", Toast.LENGTH_SHORT).show()
                        } else {

                            Log.d("LoginActivity", "Email set in ViewModel: $email")
                            val intent = Intent(this, TabsActivity::class.java)
                            val bundle = Bundle()
                            bundle.putString("email", email)
                            intent.putExtras(bundle)
                            startActivity(intent)
                            finish()

                        }
                    } else {
                        Toast.makeText(this,
                            "Login failed with status code: $statusCode", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("LoginResponseError", "Error parsing response: ${e.message}")
                    Toast.makeText(this,
                        "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

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
    private val apiClient = ApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginField: EditText = findViewById(R.id.login_field)
        val passwordField: EditText = findViewById(R.id.password_field)
        val loginButton: Button = findViewById(R.id.login_button)

        loginButton.setOnClickListener {
            val email = loginField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val url = "http://10.0.2.2:5000/login"
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
                    if (statusCode == 200) {  // Проверка успешного кода статуса 200
                        val responseObject = JSONObject(response)
                        val error = responseObject.optString("error")
                        val message = responseObject.optString("message")

                        if (error == "Internal Server Error" && message == "Invalid credentials") {
                            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                        } else {
                            // Если нет ошибки, показываем успех и продолжаем
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, TabsActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        // Если код ответа не 200, показываем ошибку
                        Toast.makeText(this, "Login failed with status code: $statusCode", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Если не удается обработать JSON, выводим ошибку в консоль
                    Log.e("LoginResponseError", "Error parsing response: ${e.message}")
                    Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

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

                            fetchUserCoreRole(email) { userCoreData ->
                                if (userCoreData != null) {
                                    Log.d("LoginActivity", "User role: ${userCoreData.roleId}, User ID: ${userCoreData.userId}")
                                    val intent = Intent(this, TabsActivity::class.java)
                                    val bundle = Bundle()
                                    bundle.putString("email", email)
                                    bundle.putString("roleId", userCoreData.roleId)
                                    bundle.putString("userId", userCoreData.userId)
                                    intent.putExtras(bundle)
                                    startActivity(intent)
                                    finish()

                                } else {
                                    Log.e("LoginActivity", "Failed to fetch user core role")
                                }

                            }

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

    private fun fetchUserCoreRole(email: String?, callback: (UserCoreData?) -> Unit) {
        if (email == null) {
            Log.e("LoginActivity", "Email is null, cannot fetch user role.")
            Toast.makeText(this, "Email is missing", Toast.LENGTH_SHORT).show()
            callback(null)
            return
        }

        val encodedEmail = java.net.URLEncoder.encode(email, "UTF-8")
        val url = "$baseUrl/user/email?email=$encodedEmail"
        apiClient.fetchData(this, url) { response, statusCode ->
            runOnUiThread {
                if (statusCode == 200) {
                    val user = parseUserRoleResponse(response)
                    callback(user)
                } else {
                    Toast.makeText(this,
                        "Failed to fetch user role: $statusCode", Toast.LENGTH_LONG).show()
                    callback(null)
                }
            }
        }
    }

    private fun parseUserRoleResponse(response: String): UserCoreData? {
        try {
            val jsonObject = JSONObject(response)
            val roleId = jsonObject.optString("role_id")
            val userId = jsonObject.optString("user_id")

            if (roleId.isNotEmpty()) {
                Log.d("LoginActivity", "User role: $roleId")
                return UserCoreData(userId, roleId)
            } else {
                Log.w("LoginActivity", "User role not found in response.")
                return null
            }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error parsing user role response: ${e.message}")
            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
            return null
        }
    }
}


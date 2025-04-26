package com.example.kotlintestapp

import ApiClient
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

class SensorsDetails : AppCompatActivity() {
    private val apiClient = ApiClient()
    private lateinit var baseUrl: String

    private lateinit var sensorsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sensors_details)

        sensorsContainer = findViewById(R.id.sensorsContainer)

        baseUrl = getString(R.string.base_url)

        val transportId = intent.getStringExtra("transport_id")
        if (transportId != null) {
            Log.d("SensorsDetails", "before fetching")
            for (sensorId in 1..3) {
                fetchSensorData(transportId, sensorId)
            }
        } else {
            Toast.makeText(this, "No Transport ID found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchSensorData(transportId: String, sensorId: Int) {
        val url = "$baseUrl/transport-sensor/$transportId/$sensorId"
        Log.d("SensorsDetails", "Fetching $url")

        apiClient.fetchData(this, url) { response, statusCode ->
            runOnUiThread {
                if (statusCode == 200) {
                    try {
                        val jsonArray = JSONArray(response)
                        if (jsonArray.length() > 0) {
                            val firstItem = jsonArray.getJSONObject(0)

                            val bloodFridgeId = firstItem.getInt("blood_fridge_id")
                            val bloodFridgeName = firstItem.getString("blood_fridge_name")
                            val status = firstItem.getString("status")
                            val temperature = firstItem.getDouble("temperature")
                            val timestamp = firstItem.getString("time_stamp")

                            addSensorBlock(sensorId, bloodFridgeId, bloodFridgeName, status, temperature, timestamp)

                            Log.d("SensorsDetails", "Sensor $sensorId data loaded")

                        } else {
                            Toast.makeText(this, "No data found for sensor $sensorId", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error parsing sensor $sensorId data", Toast.LENGTH_SHORT).show()
                        Log.e("SensorsDetails", "JSON Parsing error for sensor $sensorId", e)
                    }
                } else {
                    Toast.makeText(this, "Failed to fetch sensor $sensorId: $statusCode", Toast.LENGTH_SHORT).show()
                    Log.e("SensorsDetails", "Failed sensor $sensorId with status: $statusCode")
                }
            }
        }
    }

    private fun addSensorBlock(sensorId: Int, bloodFridgeId: Int, bloodFridgeName: String, status: String, temperature: Double, timestamp: String) {
        val sensorTextView = TextView(this).apply {
            text = """
                
                Fridge ID: $bloodFridgeId
                
                Fridge Name: $bloodFridgeName
                
                Status: $status
                
                Temperature: $temperature°C
                
                Timestamp: $timestamp
                
            """.trimIndent()
            textSize = 18f
            setTextColor(resources.getColor(android.R.color.black))
        }

        sensorsContainer.addView(sensorTextView)

        val line = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2
            ).apply {
                topMargin = 16
                bottomMargin = 16
            }
            setBackgroundColor(resources.getColor(android.R.color.darker_gray))
        }

        sensorsContainer.addView(line)
    }
}

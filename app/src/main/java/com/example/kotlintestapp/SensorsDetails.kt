package com.example.kotlintestapp

import ApiClient
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

class SensorsDetails : AppCompatActivity() {
    private val apiClient = ApiClient()
    private lateinit var baseUrl: String
    private val sensorDataBuilder = StringBuilder()

    private lateinit var textSensorsData: TextView

//    private lateinit var textBloodFridgeId: TextView
//    private lateinit var textBloodFridgeName: TextView
//    private lateinit var textStatus: TextView
//    private lateinit var textTemperature: TextView
//    private lateinit var textTimestamp: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sensors_details)

//        textBloodFridgeId = findViewById(R.id.textBloodFridgeId)
//        textBloodFridgeName = findViewById(R.id.textBloodFridgeName)
//        textStatus = findViewById(R.id.textStatus)
//        textTemperature = findViewById(R.id.textTemperature)
//        textTimestamp = findViewById(R.id.textTimestamp)

        textSensorsData = findViewById(R.id.textSensorsData)

        baseUrl = getString(R.string.base_url)

        val transportId = intent.getStringExtra("transport_id")
        if (transportId != null) {
            Log.d("SensorsDetails", "before fetching")
            for (sensorId in 1..3) {
                fetchSensorData("1", sensorId)
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

                            // Build a block for this sensor
                            val sensorBlock = """
                            Sensor $sensorId:
                            Fridge ID: $bloodFridgeId
                            Fridge Name: $bloodFridgeName
                            Status: $status
                            Temperature: $temperature°C
                            Timestamp: $timestamp
                            
                        """.trimIndent()

                            sensorDataBuilder.append(sensorBlock).append("\n\n")

                            // Update the TextView
                            val sensorTextView = findViewById<TextView>(R.id.textSensorsData)
                            sensorTextView.text = sensorDataBuilder.toString()

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

}

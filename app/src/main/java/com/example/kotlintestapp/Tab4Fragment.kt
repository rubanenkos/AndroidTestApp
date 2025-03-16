package com.example.kotlintestapp

import ApiClient
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class Tab4Fragment : Fragment() {
    private val apiClient = ApiClient()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransportRouteAdapter
    private val sessionList = mutableListOf<TransportRoute>()
    private lateinit var baseUrl: String
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_tab4, container, false)
        baseUrl = getString(R.string.base_url) // Устанавливаем baseUrl из ресурсов

        // Инициализация RecyclerView и адаптера
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TransportRouteAdapter(sessionList)
        adapter.setBaseUrl(baseUrl) // Передаем baseUrl в адаптер
        recyclerView.adapter = adapter


        Log.d("Tab4Fragment", "1)User Id: $userId")
        userId = arguments?.getString("userId")
        Log.d("Tab4Fragment", "2)User Id: $userId")
        if (userId != null) {
            fetchTransportRoutes(userId)
        }
        return view
    }


    private fun fetchTransportRoutes(userId: String?) {
        val url = "$baseUrl/blood-transport/user/$userId"
        Log.d("Tab4Fragment", "3)User Id: $userId")
        apiClient.fetchData(requireContext(), url) { response, statusCode ->
            activity?.runOnUiThread {
                if (statusCode == 200) {
                    Log.d("Tab4Fragment", "Calling parseTransportRoutesResponse()")
                    parseTransportRoutesResponse(response)
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch transport routes: $statusCode", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun parseTransportRoutesResponse(response: String) {
        try {
            Log.d("Tab4Fragment", "Response: $response")

            val jsonArray = JSONArray(response)
            sessionList.clear()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                val id = jsonObject.getInt("blood_transport_id")
                val hospitalName = jsonObject.getString("hospital_name")
                val bankName = jsonObject.getString("bank_name")
                val startTime = if (jsonObject.isNull("start_time")) "N/A" else formatDate(jsonObject.getString("start_time"))
                val endTime = if (jsonObject.isNull("end_time")) "N/A" else formatDate(jsonObject.getString("end_time"))
                val status = jsonObject.getString("status")
                sessionList.add(TransportRoute(id, hospitalName, bankName, startTime, endTime, status))
            }

            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }




    private fun formatDate(dateString: String): String {
        try {
            Log.d("Tab4Fragment", "Input date: $dateString")

            val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateString)

            Log.d("Tab4Fragment", "Parsed Date: $date")

            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Tab4Fragment", "Error parsing date: ${e.message}")
            return "Invalid Date"
        }
    }




}

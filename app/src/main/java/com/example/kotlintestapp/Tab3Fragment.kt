package com.example.kotlintestapp

import ApiClient
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class Tab3Fragment : Fragment() {
    private val apiClient = ApiClient()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DonorSessionAdapter
    private val sessionList = mutableListOf<DonorSession>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_tab3, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DonorSessionAdapter(sessionList)
        recyclerView.adapter = adapter

        fetchDonorSession()
        return view
    }

    private fun fetchDonorSession() {
        val url = "http://10.0.2.2:5000/donation-session/1"

        apiClient.fetchData(requireContext(), url) { response, statusCode ->
            activity?.runOnUiThread {
                if (statusCode == 200) {
                    parseDonorSessionsResponse(response)
                    parseDonorResults(response)
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch donor session: $statusCode", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun parseDonorSessionsResponse(response: String) {
        try {
            val jsonArray = JSONArray(response)
            sessionList.clear()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                val date = formatDate(jsonObject.getString("donation_date"))
                val quantityMl = jsonObject.getInt("quantity_ml")
                val centerName = jsonObject.getString("blood_donation_center_name")

                sessionList.add(DonorSession(date, quantityMl, centerName))
            }

            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseDonorResults(response: String) {
        try {
            val jsonArray = JSONArray(response)
            var totalQuantity = 0
            val totalVisits = jsonArray.length()

            for (i in 0 until jsonArray.length()) {
                val session = jsonArray.getJSONObject(i)
                totalQuantity += session.getInt("quantity_ml")
            }

            val message = "Вы сдали $totalQuantity мл крови за $totalVisits посещения"
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Ошибка обработки данных", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatDate(dateString: String): String {
        return dateString.substring(5, 16)
    }
}

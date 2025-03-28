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
import java.util.Locale

class Tab3Fragment : Fragment() {
    private val apiClient = ApiClient()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DonorSessionAdapter
    private val sessionList = mutableListOf<DonorSession>()
    private lateinit var baseUrl: String
    private var donorId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_tab3, container, false)
        baseUrl = getString(R.string.base_url)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DonorSessionAdapter(sessionList)
        recyclerView.adapter = adapter


        donorId = arguments?.getString("donorId")
        if (donorId != null) {
            fetchDonorSession(donorId)
        }
        return view
    }

    fun setDonorId(donorId: String) {
        Log.d("Tab3Fragment", "setDonorId called with: $donorId")
        this.donorId = donorId
        Log.d("Tab3Fragment", "Received donorId: $donorId")
        if (isAdded) {
            fetchDonorSession(donorId)
        }
    }

    private fun fetchDonorSession(donorId: String?) {
        val url = "$baseUrl/donation-session/$donorId"
        Log.d("Tab3Fragment", "Donor Id role: $donorId")
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

            val message = "You have donated $totalQuantity ml of blood in $totalVisits times"
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error while processing data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            Log.d("DateError", "Raw date: $dateString")
            val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: return "Invalid date")
        } catch (e: Exception) {
            Log.e("DateError", "Error parsing date: ${e.message}")
            "Invalid date"
        }
    }


}

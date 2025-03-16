package com.example.kotlintestapp

import ApiClient
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TransportRouteAdapter(
    private val sessionList: MutableList<TransportRoute>,
    private val updateData: () -> Unit
) :

    RecyclerView.Adapter<TransportRouteAdapter.ViewHolder>() {

    private val apiClient = ApiClient()
    private var baseUrl: String = ""
    fun setBaseUrl(url: String) {
        baseUrl = url
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textBloodTransportId: TextView = view.findViewById(R.id.textBloodTransportId)
        val textHospitalName: TextView = view.findViewById(R.id.textHospitalName)
        val textBankName: TextView = view.findViewById(R.id.textBankName)
        val textStartTime: TextView = view.findViewById(R.id.textStartTime)
        val textEndTime: TextView = view.findViewById(R.id.textEndTime)
        val textStatus: TextView = view.findViewById(R.id.textStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transport_route, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val route = sessionList[position]
        holder.textBloodTransportId.text = route.id.toString()
        holder.textHospitalName.text = route.hospitalName
        holder.textBankName.text = route.bankName
        holder.textStartTime.text = route.startTime
        holder.textEndTime.text = route.endTime
        holder.textStatus.text = route.status



        holder.textStartTime.setOnClickListener {
            val context: Context = it.context
            if (holder.textStartTime.text.toString() != "N/A") {
                Toast.makeText(context, "Маршрут вже начатий", Toast.LENGTH_SHORT).show()
            } else {
                startRoute(context, route.id)
            }
        }

        holder.textEndTime.setOnClickListener {
            val context: Context = it.context
            if (holder.textEndTime.toString() != "N/A") {
                Toast.makeText(context, "Маршрут вже закiнчений", Toast.LENGTH_SHORT).show()
            } else {
                completeRoute(context, route.id)
            }
        }
    }

    override fun getItemCount() = sessionList.size

    private fun startRoute(context: Context, routeId: Int?) {
        if (routeId != null && baseUrl.isNotEmpty()) {
            val url = "$baseUrl/blood-transport/start/$routeId"
            Log.d("TransportRouteAdapter", "RouteId: $routeId")
            apiClient.putData(context, url) { response, statusCode ->
                Log.d("TransportRouteAdapter", "Response: $response, Status Code: $statusCode")

                (context as Activity).runOnUiThread {
                    Toast.makeText(context, "Маршрут #${routeId} розпочато", Toast.LENGTH_SHORT).show()
                    updateData()
                }
            }
        } else {
            Log.e("TransportRouteAdapter", "Invalid routeId or baseUrl not initialized")

            (context as Activity).runOnUiThread {
                Toast.makeText(context, "Invalid Route ID or baseUrl not initialized", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun completeRoute(context: Context, routeId: Int?) {
        if (routeId != null && baseUrl.isNotEmpty()) {
            val url = "$baseUrl/blood-transport/complete/$routeId"
            Log.d("TransportRouteAdapter", "RouteId: $routeId")
            apiClient.putData(context, url) { response, statusCode ->
                Log.d("TransportRouteAdapter", "Response: $response, Status Code: $statusCode")

                (context as Activity).runOnUiThread {
                    Toast.makeText(context, "Маршрут #${routeId} закінчено", Toast.LENGTH_SHORT).show()
                    updateData()
                }
            }
        } else {
            Log.e("TransportRouteAdapter", "Invalid routeId or baseUrl not initialized")

            (context as Activity).runOnUiThread {
                Toast.makeText(context, "Invalid Route ID or baseUrl not initialized", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

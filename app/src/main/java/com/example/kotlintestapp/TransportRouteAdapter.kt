package com.example.kotlintestapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransportRouteAdapter(private val sessionList: List<TransportRoute>) :
    RecyclerView.Adapter<TransportRouteAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        holder.textHospitalName.text = route.hospitalName
        holder.textBankName.text = route.bankName
        holder.textStartTime.text = route.startTime
        holder.textEndTime.text = route.endTime
        holder.textStatus.text = route.status
    }

    override fun getItemCount() = sessionList.size
}

package com.example.kotlintestapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DonorSessionAdapter(private val sessionList: List<DonorSession>) :
    RecyclerView.Adapter<DonorSessionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textDate: TextView = view.findViewById(R.id.textDate)
        val textQuantity: TextView = view.findViewById(R.id.textQuantity)
        val textCenter: TextView = view.findViewById(R.id.textCenter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_donor_session, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val session = sessionList[position]
        holder.textDate.text = session.date
        holder.textQuantity.text = "${session.quantityMl} ml"
        holder.textCenter.text = session.centerName
    }

    override fun getItemCount() = sessionList.size
}

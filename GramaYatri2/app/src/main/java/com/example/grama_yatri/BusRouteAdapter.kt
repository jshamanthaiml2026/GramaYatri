package com.example.grama_yatri

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Data Model for a Stop
data class BusStop(
    val id: Int,
    val name: String,
    val minutesFromStart: Int // Average time from the very first stop
)

class BusRouteAdapter(
    private val routeList: List<BusStop>,
    private val onPingClicked: (BusStop) -> Unit
) : RecyclerView.Adapter<BusRouteAdapter.StopViewHolder>() {

    // These will be updated from Firebase
    var currentLastPingedStopId: Int = -1
    var lastPingTimeMillis: Long = 0L
    var pingReportedBy: String = ""

    inner class StopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvVillageName: TextView = itemView.findViewById(R.id.tvVillageName)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnPing: Button = itemView.findViewById(R.id.btnPing)
        val viewDot: View = itemView.findViewById(R.id.viewDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bus_stop, parent, false)
        return StopViewHolder(view)
    }

    override fun onBindViewHolder(holder: StopViewHolder, position: Int) {
        val stop = routeList[position]
        holder.tvVillageName.text = stop.name

        // Logic for ETA Calculation
        if (currentLastPingedStopId == -1) {
            holder.tvStatus.text = "Waiting for live update..."
            holder.viewDot.setBackgroundColor(Color.GRAY)
        } else {
            val lastPingedStop = routeList.find { it.id == currentLastPingedStopId }
            
            if (stop.id == currentLastPingedStopId) {
                holder.tvStatus.text = "Bus is here! (Reported by $pingReportedBy)"
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")) // Green
                holder.viewDot.setBackgroundColor(Color.parseColor("#4CAF50"))
            } else if (stop.minutesFromStart < (lastPingedStop?.minutesFromStart ?: 0)) {
                holder.tvStatus.text = "Bus has passed"
                holder.tvStatus.setTextColor(Color.GRAY)
                holder.viewDot.setBackgroundColor(Color.GRAY)
            } else {
                // Calculate ETA
                val timeDifferenceMins = stop.minutesFromStart - (lastPingedStop?.minutesFromStart ?: 0)
                val expectedArrivalTime = lastPingTimeMillis + (timeDifferenceMins * 60 * 1000)
                val currentTime = System.currentTimeMillis()
                val minutesAway = ((expectedArrivalTime - currentTime) / (1000 * 60)).coerceAtLeast(0)

                holder.tvStatus.text = "Arriving in ~$minutesAway mins"
                holder.tvStatus.setTextColor(Color.parseColor("#FF9800")) // Orange
                holder.viewDot.setBackgroundColor(Color.parseColor("#FF9800"))
            }
        }

        holder.btnPing.setOnClickListener {
            onPingClicked(stop)
        }
    }

    override fun getItemCount(): Int = routeList.size
    
    fun updatePingData(stopId: Int, timeMillis: Long, reporter: String) {
        currentLastPingedStopId = stopId
        lastPingTimeMillis = timeMillis
        pingReportedBy = reporter
        notifyDataSetChanged()
    }
}

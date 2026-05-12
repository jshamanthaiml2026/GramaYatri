package com.example.grama_yatri

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class BusDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_detail)

        val bus = intent.getSerializableExtra("BUS_DATA") as? BusModel ?: return

        findViewById<TextView>(R.id.tvToolbarTitle).text = bus.name
        findViewById<TextView>(R.id.tvToolbarSubtitle).text = bus.route

        val rvStops: RecyclerView = findViewById(R.id.rvStops)
        rvStops.layoutManager = LinearLayoutManager(this)
        rvStops.adapter = StopsAdapter(bus.stops) { stop ->
            setArrivalReminder(stop.name)
        }

        // Setup simple interactions
        findViewById<View>(R.id.btnShare).setOnClickListener {
            Toast.makeText(this, "Sharing Live Location & ETA...", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.btnAlarm).setOnClickListener {
            showReportDialog()
        }
        
        // Update button - simulating location sync
        findViewById<View>(R.id.btnUpdate).setOnClickListener {
            Toast.makeText(this, "Syncing with GPS & Cell Towers...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setArrivalReminder(stopName: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Set Arrival Reminder")
            .setMessage("Would you like a reminder 5 minutes before arriving at $stopName?")
            .setPositiveButton("Yes, Notify Me") { _, _ ->
                Toast.makeText(this, "Reminder set for $stopName", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showReportDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_report, null)
        MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setPositiveButton("Submit Report") { _, _ ->
                Toast.makeText(this, "Thank you. Your report has been sent to the control room.", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

class StopsAdapter(
    private val stops: List<BusStopInfo>,
    private val onStopClick: (BusStopInfo) -> Unit
) : RecyclerView.Adapter<StopsAdapter.StopViewHolder>() {

    class StopViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvStopName: TextView = view.findViewById(R.id.tvStopName)
        val tvStopTime: TextView = view.findViewById(R.id.tvStopTime)
        val ivDot: ImageView = view.findViewById(R.id.ivStopDot)
        val lineTop: View = view.findViewById(R.id.viewLineTop)
        val lineBottom: View = view.findViewById(R.id.viewLineBottom)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_route_stop, parent, false)
        return StopViewHolder(view)
    }

    override fun onBindViewHolder(holder: StopViewHolder, position: Int) {
        val stop = stops[position]
        holder.tvStopName.text = stop.name
        holder.tvStopTime.text = stop.time

        holder.lineTop.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
        holder.lineBottom.visibility = if (position == stops.size - 1) View.INVISIBLE else View.VISIBLE

        if (stop.isPassed) {
            holder.ivDot.setImageResource(android.R.drawable.presence_online)
            holder.tvStopName.setTextColor(0xFF757575.toInt())
        } else if (stop.isCurrent) {
            holder.ivDot.setImageResource(android.R.drawable.presence_busy)
            holder.tvStopName.setTextColor(0xFF0D47A1.toInt())
        } else {
            holder.ivDot.setImageResource(android.R.drawable.presence_invisible)
            holder.tvStopName.setTextColor(0xFF000000.toInt())
        }

        holder.itemView.setOnClickListener { onStopClick(stop) }
    }

    override fun getItemCount() = stops.size
}

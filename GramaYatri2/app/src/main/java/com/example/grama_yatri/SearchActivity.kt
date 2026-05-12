package com.example.grama_yatri

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val type = intent.getStringExtra("TYPE") ?: "Government"
        findViewById<TextView>(R.id.tvAvailableBuses).text = "$type Buses Available Today"

        val rvBuses: RecyclerView = findViewById(R.id.rvBuses)
        rvBuses.layoutManager = LinearLayoutManager(this)

        // Theme Toggle
        findViewById<ImageButton>(R.id.btnThemeToggle).setOnClickListener {
            val isDark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
            AppCompatDelegate.setDefaultNightMode(
                if (isDark) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES
            )
        }

        // Language Toggle
        findViewById<TextView>(R.id.btnLanguage).setOnClickListener {
            val languages = arrayOf("English", "ಕನ್ನಡ", "தமிழ்", "తెలుగు", "മലയാളം")
            MaterialAlertDialogBuilder(this)
                .setTitle("Select Language")
                .setItems(languages) { _, which ->
                    findViewById<TextView>(R.id.btnLanguage).text = languages[which]
                }
                .show()
        }

        val sampleBuses = if (type == "Government") {
            listOf(
                BusModel("Rural Sarige 42A", "07:30 AM", "Majestic → Ramanagara", "ORDINARY", 22, "₹45", listOf(
                    BusStopInfo("Majestic (KBS)", "07:30 AM", true),
                    BusStopInfo("Nayandahalli", "07:45 AM", true),
                    BusStopInfo("Kengeri TTMC", "08:00 AM", true),
                    BusStopInfo("Kumbalgodu", "08:15 AM", true),
                    BusStopInfo("Bidadi", "08:35 AM", false, true),
                    BusStopInfo("Ramanagara", "09:00 AM")
                )),
                BusModel("KSRTC Vaibhav", "08:15 AM", "Bangalore → Mysore", "ULTRA-DELUXE", 15, "₹185", listOf(
                    BusStopInfo("Majestic", "08:15 AM", true),
                    BusStopInfo("Kengeri", "08:45 AM", true),
                    BusStopInfo("Bidadi", "09:15 AM", true),
                    BusStopInfo("Ramanagara", "09:40 AM", true),
                    BusStopInfo("Channapatna", "10:00 AM", true),
                    BusStopInfo("Maddur", "10:25 AM", false, true),
                    BusStopInfo("Mandya", "11:00 AM"),
                    BusStopInfo("Srirangapatna", "11:30 AM"),
                    BusStopInfo("Mysore Central", "12:00 PM")
                )),
                BusModel("KSRTC Express", "06:00 AM", "Bangalore → Tumkur", "EXPRESS", 10, "₹75", listOf(
                    BusStopInfo("Majestic", "06:00 AM", true),
                    BusStopInfo("Yeshwantpur", "06:20 AM", true),
                    BusStopInfo("Jalahalli Cross", "06:35 AM", false, true),
                    BusStopInfo("Nelamangala", "07:00 AM"),
                    BusStopInfo("Dobbaspet", "07:25 AM"),
                    BusStopInfo("Kyatsandra", "07:45 AM"),
                    BusStopInfo("Tumkur Bus Stand", "08:00 AM")
                ))
            )
        } else {
            listOf(
                BusModel("Green Line Travels", "08:30 PM", "Bangalore → Mysore", "AC SLEEPER", 12, "₹650", listOf(
                    BusStopInfo("Kalaisipalyam", "08:30 PM", true),
                    BusStopInfo("Madiwala", "09:15 PM", true),
                    BusStopInfo("Silk Board", "09:30 PM", false, true),
                    BusStopInfo("Electronic City", "09:50 PM"),
                    BusStopInfo("Ramanagara Bypass", "11:00 PM"),
                    BusStopInfo("Mandya", "12:15 AM"),
                    BusStopInfo("Mysore", "01:30 AM")
                )),
                BusModel("SRS Travels", "10:15 PM", "Bangalore → Hubli", "MULTI-AXLE", 5, "₹950", listOf(
                    BusStopInfo("Anand Rao Circle", "10:15 PM", true),
                    BusStopInfo("Yeshwantpur", "10:45 PM", false, true),
                    BusStopInfo("Nelamangala", "11:15 PM"),
                    BusStopInfo("Tumkur", "12:00 AM"),
                    BusStopInfo("Chitradurga", "02:30 AM"),
                    BusStopInfo("Davangere", "04:00 AM"),
                    BusStopInfo("Hubli", "06:30 AM")
                ))
            )
        }

        rvBuses.adapter = BusAdapter(sampleBuses) { bus ->
            val intent = Intent(this, BusDetailActivity::class.java)
            intent.putExtra("BUS_DATA", bus)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}

class BusAdapter(
    private val buses: List<BusModel>,
    private val onClick: (BusModel) -> Unit
) : RecyclerView.Adapter<BusAdapter.BusViewHolder>() {

    class BusViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvBusName)
        val tvTime: TextView = view.findViewById(R.id.tvTiming)
        val tvRoute: TextView = view.findViewById(R.id.tvRoute)
        val tvType: TextView = view.findViewById(R.id.tvTypeTag)
        val tvSeats: TextView = view.findViewById(R.id.tvSeats)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bus_card, parent, false)
        return BusViewHolder(view)
    }

    override fun onBindViewHolder(holder: BusViewHolder, position: Int) {
        val bus = buses[position]
        holder.tvName.text = bus.name
        holder.tvTime.text = bus.time
        holder.tvRoute.text = bus.route
        holder.tvType.text = bus.type
        holder.tvSeats.text = "${bus.seats} Seats Left"
        holder.tvPrice.text = bus.price
        holder.itemView.setOnClickListener { onClick(bus) }
    }

    override fun getItemCount() = buses.size
}

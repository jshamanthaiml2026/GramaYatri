package com.example.grama_yatri

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var adapter: BusRouteAdapter
    private lateinit var tvCancellationAlert: TextView

    // Hardcoded route logic representing average travel times between villages
    private val routeStops = listOf(
        BusStop(1, "Halli Start Point", 0),
        BusStop(2, "Government School Stop", 15),
        BusStop(3, "Lake Junction", 25),
        BusStop(4, "Market Square", 40),
        BusStop(5, "City Depot (End)", 60)
    )

    // For this prototype, we mock a user. In production, use Firebase Auth.
    private val currentUser = "Student User"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        database = Firebase.database.reference

        // Initialize UI
        tvCancellationAlert = findViewById(R.id.tvCancellationAlert)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewRoute)
        val btnReportCancel: Button = findViewById(R.id.btnReportCancel)

        // Setup RecyclerView
        adapter = BusRouteAdapter(routeStops) { stop ->
            // Handle Ping Button Click
            pingBusAtStop(stop.id)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Handle Cancellation Reporting
        btnReportCancel.setOnClickListener {
            reportCancellation()
        }

        // Listen to Realtime Database
        listenToLiveRouteData()
    }

    private fun pingBusAtStop(stopId: Int) {
        val pingData = mapOf(
            "lastStopId" to stopId,
            "timestamp" to System.currentTimeMillis(),
            "reportedBy" to currentUser,
            "isCancelled" to false
        )
        // Push to Firebase under route "42A"
        database.child("routes").child("42A").setValue(pingData)
            .addOnSuccessListener {
                Toast.makeText(this, "Pinged successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to ping. Check connection.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun reportCancellation() {
        val cancelData = mapOf(
            "isCancelled" to true,
            "reportedBy" to currentUser
        )
        database.child("routes").child("42A").updateChildren(cancelData)
    }

    private fun listenToLiveRouteData() {
        database.child("routes").child("42A").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val isCancelled = snapshot.child("isCancelled").getValue(Boolean::class.java) ?: false
                    
                    if (isCancelled) {
                        val reporter = snapshot.child("reportedBy").getValue(String::class.java) ?: "Unknown"
                        tvCancellationAlert.visibility = View.VISIBLE
                        tvCancellationAlert.text = "ALERT: Bus cancelled today. (Reported by $reporter)"
                        findViewById<RecyclerView>(R.id.recyclerViewRoute).alpha = 0.5f // Dim the route
                    } else {
                        tvCancellationAlert.visibility = View.GONE
                        findViewById<RecyclerView>(R.id.recyclerViewRoute).alpha = 1.0f
                        
                        val lastStopId = snapshot.child("lastStopId").getValue(Int::class.java) ?: -1
                        val timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                        val reporter = snapshot.child("reportedBy").getValue(String::class.java) ?: "Unknown"

                        adapter.updatePingData(lastStopId, timestamp, reporter)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to sync data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

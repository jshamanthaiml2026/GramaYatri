package com.example.grama_yatri

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class CategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val intent = Intent(this, SearchActivity::class.java)

        findViewById<CardView>(R.id.cardGovt).setOnClickListener {
            intent.putExtra("TYPE", "Government")
            startActivity(intent)
        }

        findViewById<CardView>(R.id.cardPrivate).setOnClickListener {
            intent.putExtra("TYPE", "Private")
            startActivity(intent)
        }
    }
}

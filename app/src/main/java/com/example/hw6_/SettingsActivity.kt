package com.example.hw6_

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SettingsActivity : AppCompatActivity() {
    private var selectedDistanceUnit = "Kilometers" // default value
    private var selectedBearingUnit = "Degrees" // default value


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val currentDistanceUnit = intent.getStringExtra("distanceUnit")
        val currentBearingUnit = intent.getStringExtra("bearingUnit")


        //IN CASE YOU NEED THIS LATER https://developer.android.com/develop/ui/views/components/spinner
        val spinnerDistance = findViewById<Spinner>(R.id.spinner)
        val distanceUnits = resources.getStringArray(R.array.distance_units)
        spinnerDistance.setSelection(distanceUnits.indexOf(currentDistanceUnit))
        ArrayAdapter.createFromResource(
            this,
            R.array.distance_units,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDistance.adapter = adapter
        }

        val spinnerBearing = findViewById<Spinner>(R.id.spinner2)
        val bearingUnits = resources.getStringArray(R.array.bearing_units)
        spinnerBearing.setSelection(bearingUnits.indexOf(currentBearingUnit))

        ArrayAdapter.createFromResource(
            this,
            R.array.bearing_units,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerBearing.adapter = adapter
        }

        spinnerDistance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                selectedDistanceUnit = parent.getItemAtPosition(position).toString()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        spinnerBearing.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                selectedBearingUnit = parent.getItemAtPosition(position).toString()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        val fabConfirm = findViewById<FloatingActionButton>(R.id.fab_confirm)
        fabConfirm.setOnClickListener {
            val data = Intent().apply {
                putExtra("distanceUnit", selectedDistanceUnit)
                putExtra("bearingUnit", selectedBearingUnit)
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }
}


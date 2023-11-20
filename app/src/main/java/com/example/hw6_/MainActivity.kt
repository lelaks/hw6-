package com.example.hw6_

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import android.location.Location
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import kotlin.math.roundToLong

class MainActivity : AppCompatActivity() {

    private var startLatitude: EditText? = null
    private var startLongitude: EditText? = null
    private var endLatitude: EditText? = null
    private var endLongitude: EditText? = null
    private var distanceTextView: TextView? = null
    private var bearingTextView: TextView? = null

    private var currentDistanceUnit = "Kilometers"
    private var currentBearingUnit = "Degrees"

    private val settingsResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.let {
                val distanceUnit = it.getStringExtra("distanceUnit")
                val bearingUnit = it.getStringExtra("bearingUnit")
                if (distanceUnit != null && bearingUnit != null) {
                    updateCalculations(distanceUnit, bearingUnit)
                }
            }
        }
    }


    @SuppressLint("SetTextI18n") //compiler warning auto fix
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)

        val startLatitude = findViewById<EditText>(R.id.lat1)
        val startLongitude = findViewById<EditText>(R.id.long1)
        val endLatitude = findViewById<EditText>(R.id.lat2)
        val endLongitude = findViewById<EditText>(R.id.long2)
        val calculateButton = findViewById<Button>(R.id.calcBtn)
        val clearButton = findViewById<Button>(R.id.clearBtn)

         fun hideKeyboard(button: Button) {
             val view = this.currentFocus
             if (view != null) {
                 val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                 imm.hideSoftInputFromWindow(button.windowToken, 0)
             }
         }


        calculateButton.setOnClickListener {
            // Hide the keypad
            hideKeyboard(calculateButton)

            val startLat = startLatitude.text.toString().toDoubleOrNull()
            val startLong = startLongitude.text.toString().toDoubleOrNull()
            val endLat = endLatitude.text.toString().toDoubleOrNull()
            val endLong = endLongitude.text.toString().toDoubleOrNull()

            if (startLat != null && startLong != null && endLat != null && endLong != null) {
                val results = FloatArray(2)
                Location.distanceBetween(startLat, startLong, endLat, endLong, results)
                val distance = results[0] / 1000 // Convert to kilometers
                val bearing = results[1]

                // Round to 2 decimal places
                val distanceRounded = (distance * 100).roundToLong() / 100.0
                val bearingRounded = (bearing * 100).roundToLong() / 100.0

                val distanceTextView = findViewById<TextView>(R.id.distanceTxt)
                val bearingTextView = findViewById<TextView>(R.id.bearingTxt)

                distanceTextView.text = getString(R.string.distance_format, distanceRounded)
                bearingTextView.text = getString(R.string.bearing_format, bearingRounded)

            }
            else {
                Toast.makeText(this, "Please make sure to enter all latitudes and longitudes", Toast.LENGTH_SHORT).show()
            }
        }

        clearButton.setOnClickListener {
            // Hide the keypad
            hideKeyboard(clearButton)

            startLatitude.setText("")
            startLongitude.setText("")
            endLatitude.setText("")
            endLongitude.setText("")

            val distanceTextView = findViewById<TextView>(R.id.distanceTxt)
            val bearingTextView = findViewById<TextView>(R.id.bearingTxt)

            //reset the calcs to default
            distanceTextView.text = "Distance: 0.00km"
            bearingTextView.text = "Bearing: 0.00°"
        }

    }


    //reference this for errors https://developer.android.com/develop/ui/views/components/menus
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {

                val intent = Intent(this, SettingsActivity::class.java).apply {
                    putExtra("distanceUnit", currentDistanceUnit)
                    putExtra("bearingUnit", currentBearingUnit)
                }
                settingsResultLauncher.launch(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun updateCalculations(distanceUnit: String, bearingUnit: String) {

        //App crashes here because of null
        //FIX THIS these values are null after returning from settingsActivity and updatecalc is called FIX THIS
        val startLat = startLatitude?.text.toString().toDoubleOrNull() ?: 0.0
        val startLong = startLongitude?.text.toString().toDoubleOrNull() ?: 0.0
        val endLat = endLatitude?.text.toString().toDoubleOrNull() ?: 0.0
        val endLong = endLongitude?.text.toString().toDoubleOrNull() ?: 0.0

        currentDistanceUnit = distanceUnit
        currentBearingUnit = bearingUnit


        val results = FloatArray(2)
        Location.distanceBetween(startLat, startLong, endLat, endLong, results)
        val distanceInMeters = results[0].toDouble()
        val bearingInDegrees = results[1].toDouble()

        // Convert distance to the selected unit
        val distance= if (distanceUnit == "Kilometers") {
            distanceInMeters / 1000.0 // Convert meters to kilometers
        } else {
            distanceInMeters / 1609.34 // Convert meters to miles
        }

        // Convert bearing to the selected unit
        val bearing = if (bearingUnit == "Degrees") {
            bearingInDegrees // Bearing is already in degrees
        } else {
            bearingInDegrees * 17.777777778 // Convert degrees to mils (1 degree = 17.78 mils)
        }

        // Update the TextViews to display the changes
        distanceTextView?.text = getString(R.string.distance_format, distance)
        bearingTextView?.text = getString(R.string.bearing_format, bearing)
    }
}



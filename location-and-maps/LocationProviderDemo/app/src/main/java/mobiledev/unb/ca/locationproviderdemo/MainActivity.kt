package mobiledev.unb.ca.locationproviderdemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    private lateinit var locationTextView: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Having a cancellation token allows the system to abort getCurrentLocation
    // if the Coroutine scope cancels
    private var cancellationTokenSource: CancellationTokenSource? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted || coarseLocationGranted) {
                Log.i(TAG, "Permission granted via launcher")
                checkLocationSettingsAndFetch()
            } else {
                Log.w(TAG, "Permission denied")
                showToast(getString(R.string.permission_denied))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        locationTextView = findViewById(R.id.location_text_view)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationButton: Button = findViewById(R.id.location_button)
        locationButton.setOnClickListener {
            handleLocationFlow()
        }
    }

    private fun handleLocationFlow() {
        if (hasLocationPermissions()) {
            checkLocationSettingsAndFetch()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkLocationSettingsAndFetch() {
        if (isLocationEnabled()) {
            // Launch a coroutine bounded to the Activity lifecycle
            lifecycleScope.launch {
                fetchLocationSequence()
            }
        } else {
            showToast("Please turn location services on")
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // Main Coroutine Execution sequence
    @SuppressLint("MissingPermission")
    private suspend fun fetchLocationSequence() {
        if (!hasLocationPermissions()) {
            return
        }

        try {
            // Fetch the cached last known location instantly
            val cachedLocation = fusedLocationClient.lastLocation.await()

            if (cachedLocation != null) {
                setTextViewDisplay(cachedLocation)
            } else {
                // Fallback to getting a fresh location fix sequentially
                fetchFreshCurrentLocation()
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e // Preserve coroutine cancellation behavior
            Log.e(TAG, "Failed to get last location, trying fresh location", e)
            fetchFreshCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun fetchFreshCurrentLocation() {
        if (!hasLocationPermissions()) {
            return
        }

        locationTextView.text = getString(R.string.fetching)

        val locationRequest = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        cancellationTokenSource = CancellationTokenSource()

        try {
            // Uses .await() to pause execution line-by-line until Play Services responds
            val freshLocation = fusedLocationClient.getCurrentLocation(
                locationRequest,
                cancellationTokenSource!!.token
            ).await()

            if (freshLocation != null) {
                setTextViewDisplay(freshLocation)
            } else {
                locationTextView.text = getString(R.string.fetch_location_error)
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }

            Log.e(TAG, "Failed to get fresh current location", e)
            locationTextView.text = getString(R.string.fetch_location_error)
        }
    }

    private fun setTextViewDisplay(location: Location) {
        val latitude = location.latitude.toString()
        val longitude = location.longitude.toString()
        val accuracy = location.accuracy.toString()
        val text = getString(R.string.location_details, latitude, longitude, accuracy)
        locationTextView.text = text
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the location hardware request when the activity is destroyed
        cancellationTokenSource?.cancel()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

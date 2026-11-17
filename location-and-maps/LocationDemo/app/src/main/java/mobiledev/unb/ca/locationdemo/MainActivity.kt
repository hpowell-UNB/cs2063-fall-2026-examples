package mobiledev.unb.ca.locationdemo

import android.Manifest
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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var locationTextView: TextView

    // Lazy initialize the ViewModel using standard ktx extensions
    private val viewModel: LocationViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted || coarseLocationGranted) {
                Log.i(TAG, "Permission granted via launcher")
                checkLocationSettingsAndFetch()
            } else {
                Log.w(TAG, "Permission denied")
                showToast("Location permissions are required to use this feature.")
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

        val locationButton: Button = findViewById(R.id.location_button)
        locationButton.setOnClickListener {
            handleLocationFlow()
        }

        // Collect UI state safely matching the lifecycle of the activity
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderUiState(state)
                }
            }
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
            viewModel.fetchLocationSequence()
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

    private fun renderUiState(state: LocationUiState) {
        when (state) {
            is LocationUiState.Idle -> {
                locationTextView.text = "Tap the button to get location details."
            }
            is LocationUiState.Loading -> {
                locationTextView.text = "Fetching location coordinates..."
            }
            is LocationUiState.Success -> {
                setTextViewDisplay(state.location)
            }
            is LocationUiState.Error -> {
                locationTextView.text = state.errorMessage
            }
        }
    }

    private fun setTextViewDisplay(location: Location) {
        val text = getString(
            R.string.location_details,
            location.latitude.toString(),
            location.longitude.toString(),
            location.accuracy.toString()
        )
        locationTextView.text = text
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

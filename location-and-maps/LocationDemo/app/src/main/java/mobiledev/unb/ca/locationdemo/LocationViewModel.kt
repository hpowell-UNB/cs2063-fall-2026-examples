package mobiledev.unb.ca.locationdemo

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<LocationUiState>(LocationUiState.Idle)
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private var cancellationTokenSource: CancellationTokenSource? = null

    // Permissions must be explicitly handled by the Activity caller before calling this
    @SuppressLint("MissingPermission")
    fun fetchLocationSequence() {
        viewModelScope.launch {
            _uiState.value = LocationUiState.Loading

            try {
                // Fetch the cached last known location instantly
                val cachedLocation = fusedLocationClient.lastLocation.await()

                if (cachedLocation != null) {
                    _uiState.value = LocationUiState.Success(cachedLocation)
                } else {
                    // Fallback to getting a fresh location fix
                    fetchFreshCurrentLocation()
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e(TAG, "Failed to get last location, trying fresh location", e)
                fetchFreshCurrentLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun fetchFreshCurrentLocation() {
        val locationRequest = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        cancellationTokenSource = CancellationTokenSource()

        try {
            val freshLocation = fusedLocationClient.getCurrentLocation(
                locationRequest,
                cancellationTokenSource!!.token
            ).await()

            if (freshLocation != null) {
                _uiState.value = LocationUiState.Success(freshLocation)
            } else {
                _uiState.value = LocationUiState.Error("Unable to fetch location details.")
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "Failed to get fresh current location", e)
            _uiState.value = LocationUiState.Error("Failed to resolve current location hardware.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel the location hardware request if the ViewModel is destroyed
        cancellationTokenSource?.cancel()
    }

    companion object {
        private const val TAG = "LocationViewModel"
    }
}

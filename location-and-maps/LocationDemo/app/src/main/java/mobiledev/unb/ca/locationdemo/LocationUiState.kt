package mobiledev.unb.ca.locationdemo

import android.location.Location

sealed interface LocationUiState {
    object Idle : LocationUiState
    object Loading : LocationUiState
    data class Success(val location: Location) : LocationUiState
    data class Error(val errorMessage: String) : LocationUiState
}

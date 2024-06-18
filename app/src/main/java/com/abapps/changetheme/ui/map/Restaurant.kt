package com.abapps.changetheme.ui.map

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abapps.changetheme.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Restaurant(val name: String, val lat: Double, val lng: Double)

data class MapsUiState(
    val currentLocation: Location? = null,
    val restaurants: List<Restaurant> = emptyList()
)

class MapsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapsUiState())
    val uiState: StateFlow<MapsUiState> = _uiState

    fun updateLocation(location: Location) {
        _uiState.value = _uiState.value.copy(currentLocation = location)
    }

    fun fetchNearbyRestaurants(location: Location) {
        val apiKey =
            "AIzaSyD1kFbtDfzI3bXkfkCEjb0LKtC8PTz-YZU"
        val locationString = "${location.latitude},${location.longitude}"
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getNearbyRestaurants(
                    location = locationString,
                    radius = 1500,
                    type = "restaurant",
                    apiKey = apiKey
                )
                val restaurants = response.results.map {
                    Restaurant(it.name, it.geometry.location.lat, it.geometry.location.lng)
                }
                _uiState.value = _uiState.value.copy(restaurants = restaurants)
            } catch (e: Exception) {
                Log.e("KAMELOO", e.message.toString())
            }
        }
    }
}
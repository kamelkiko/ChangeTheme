package com.abapps.changetheme.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.abapps.changetheme.resource.AppTheme
import com.abapps.changetheme.ui.map.MapsUiState
import com.abapps.changetheme.ui.map.MapsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val viewModel: MapsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            val mainViewModel by viewModels<MainViewModel>()
            val languageCode by mainViewModel.state.collectAsState()
            AppTheme(languageCode = languageCode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RequestLocationPermission { getLastKnownLocation() }
                    val state by viewModel.uiState.collectAsState()
                    MapView(state)
                }
            }
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    viewModel.updateLocation(location)
                    viewModel.fetchNearbyRestaurants(location)
                    fusedLocationClient.removeLocationUpdates(this)
                    break
                }
            }
        }
    }

    private fun getLastKnownLocation() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location: Location? ->
//                Log.e("KAMELOO", location.toString())
//                location?.let {
//                    viewModel.updateLocation(it)
//                    viewModel.fetchNearbyRestaurants(it)
//                }
//            }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(onPermissionGranted: () -> Unit) {
    val locationPermissionState =
        rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    if (locationPermissionState.status.isGranted) {
        onPermissionGranted()
    } else {
        LaunchedEffect(locationPermissionState) {
            locationPermissionState.launchPermissionRequest()
        }
    }
}

@Composable
fun MapView(state: MapsUiState) {
    val cameraPositionState = rememberCameraPositionState {
        state.currentLocation?.let {
            position = CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        state.restaurants.forEach { restaurant ->
            Marker(
                state = rememberMarkerState(position = LatLng(restaurant.lat, restaurant.lng)),
                title = restaurant.name
            )
        }
    }
}
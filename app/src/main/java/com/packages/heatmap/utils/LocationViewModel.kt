package com.packages.heatmap.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.packages.heatmap.walkscore.Area
import com.packages.heatmap.walkscore.CircleArea
import com.packages.heatmap.walkscore.HexagonArea
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class LocationViewModel : ViewModel() {
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var placesClient: PlacesClient
    lateinit var geoCoder: Geocoder
    val dataMap = HexagonArea.mapping
    val firstMapObject: HexagonArea = dataMap[dataMap.keys.first()]!!
    var locationState by mutableStateOf<LocationState>(LocationState.NoPermission)
    val locationAutofill = mutableStateListOf<AutoCompleteResult>()
    var currentLatLong by mutableStateOf(LatLng(firstMapObject.latitude, firstMapObject.longitude))

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        locationState = LocationState.LocationLoading
        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                locationState = if (location == null && locationState !is LocationState.LocationAvailable) {
                    LocationState.Error
                } else {
                    LocationState.LocationAvailable(LatLng(location.latitude, location.longitude))
                }
            }
    }
    private var job: Job? = null
    fun searchPlaces(query: String, context: Context) {
        val placesClient: PlacesClient = Places.createClient(context)
        job?.cancel()
        locationAutofill.clear()
        job = viewModelScope.launch {
            val request = FindAutocompletePredictionsRequest
                .builder()
                .setQuery(query)
                .build()
            placesClient
                .findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    locationAutofill += response.autocompletePredictions.map {
                        AutoCompleteResult(
                            it.getFullText(null).toString(),
                            it.placeId
                        )
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    println(it.cause)
                    println(it.message)
                }
        }
    }
    fun getCoordinates(result: AutoCompleteResult) {
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(result.placeId, placeFields)
        placesClient.fetchPlace(request).addOnSuccessListener {
            if (it != null) {
                currentLatLong = it.place.latLng!!
                update(result.address)
            }
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }
    fun update(result: String? = null){
        val address: String = result ?: try{
                geoCoder.getFromLocation(currentLatLong.latitude, currentLatLong.longitude, 1)?.get(0)?.getAddressLine(0)!!
            } catch(e: Exception){
                "Could not locate"
            }
        val thread = Area.getAreaFromAPIRequest(
            currentLatLong.latitude,
            currentLatLong.longitude,
            address
        )
        thread.start()
        thread.join()
        if (currentLatLong in dataMap.keys) {
            dataMap[currentLatLong]?.getNeighbors()?.forEach {
                val address: String = result ?: try{
                    geoCoder.getFromLocation(it.latitude, it.longitude, 1)?.get(0)?.getAddressLine(0)!!
                } catch(e: Exception){
                    "Could not locate"
                }
                val thread = Area.getAreaFromAPIRequest(
                    it.latitude,
                    it.longitude,
                    address
                )
                thread.start()
                thread.join()
            }
        }
    }
}

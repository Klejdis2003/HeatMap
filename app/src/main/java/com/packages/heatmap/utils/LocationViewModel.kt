package com.packages.heatmap.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
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
import com.google.gson.Gson
import com.packages.heatmap.BuildConfig
import com.packages.heatmap.walkscore.Area
import com.packages.heatmap.walkscore.CircleArea
import com.packages.heatmap.walkscore.HexagonArea
import com.packages.heatmap.walkscore.api.Request
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.CoroutineContext


class LocationViewModel : ViewModel() {
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var placesClient: PlacesClient
    lateinit var geoCoder: Geocoder
    //val dataMap by  mutableStateOf(HexagonArea.mapping)
    val firstMapObject: HexagonArea = HexagonArea.mapping[HexagonArea.mapping.keys.first()]!!
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
        viewModelScope.launch(Dispatchers.IO){
            val address: String = result ?: try {
                geoCoder.getFromLocation(currentLatLong.latitude, currentLatLong.longitude, 1)
                    ?.get(0)?.getAddressLine(0)!!
            } catch (e: Exception) {
                "Could not locate"
            }
            makeAreaFromAPIRequest(
                currentLatLong.latitude,
                currentLatLong.longitude,
                address
            )

            if (currentLatLong in HexagonArea.mapping.keys) {
                HexagonArea.mapping[currentLatLong]?.getNeighbors()?.forEach {
                    viewModelScope.launch(Dispatchers.IO) {
                        val address: String = try {
                            geoCoder.getFromLocation(it.latitude, it.longitude, 1)?.get(0)
                                ?.getAddressLine(0)!!
                        } catch (e: Exception) {
                            "Could not locate"
                        }
                        makeAreaFromAPIRequest(
                            it.latitude,
                            it.longitude,
                            address
                        )
                    }
                }
            }
        }
    }

    private fun makeAreaFromAPIRequest(latitude: Double, longitude: Double, address: String? = "") {
        val url = URL(
            "https://api.walkscore.com/score?format=json&" +
                    "address=1119%8th%20Avenue%20Seattle%20WA%2098101&lat=${latitude}&" +
                    "lon=${longitude}&transit=1&bike=1&wsapikey=${BuildConfig.WALKSCORE_API_KEY}"
        )
        val connection = url.openConnection() as HttpURLConnection
        if (connection.responseCode == 200) {
            val inputSystem = connection.inputStream
            val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
            val request = Gson().fromJson(inputStreamReader, Request::class.java)
            inputStreamReader.close()
            inputSystem.close()
            HexagonArea(
                latitude,
                longitude,
                request.walkscore,
                address = address,
                description = request.description
            )
        } else
            Log.w("Connection Error", "Failed to connect")
    }
}

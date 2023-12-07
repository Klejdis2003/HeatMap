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
import com.packages.heatmap.walkscore.HexagonArea
import com.packages.heatmap.walkscore.api.Request
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class LocationViewModel : ViewModel() {
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var placesClient: PlacesClient
    lateinit var geoCoder: Geocoder
    val firstMapObject: HexagonArea = HexagonArea.mapping[HexagonArea.mapping.keys.first()]!!
    var locationState by mutableStateOf<LocationState>(LocationState.NoPermission)
    val locationAutofill = mutableStateListOf<AutoCompleteResult>()
    var currentLatLong by mutableStateOf(LatLng(firstMapObject.latitude, firstMapObject.longitude))
    @OptIn(DelicateCoroutinesApi::class)
    private val hexContext = newSingleThreadContext("HexContext")
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
    /**
     * Uses the Google Places API to search for locations based on the query.
     * @param query The query to search for.
     * @param context The context of the application, only called from main context for our purpose.
     */
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

    /**
     * Uses the Google Places API to reverse geocode the placeId in order to retrieve its position on the map.
     * @param result An AutoCompleteResult object with data retrieved from the Google Places API
     */
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

    /**
     * Generates new hexagons when the position of the map changes. It makes a request to the WalkScore API
     * for each of them and also reverse geocodes the coordinates to retrieve a nearby address.
     * @param result The address of the new location, if available. If it is not, it will fetch it from the API.
     */
    fun update(result: String?) {
        viewModelScope.launch(Dispatchers.IO){
            var address = when (result) {
                null -> reverseGeocode(currentLatLong.latitude, currentLatLong.longitude)
                else -> result
            }

            makeAreaFromAPIRequest(
                currentLatLong.latitude,
                currentLatLong.longitude,
                address
            )

            if (currentLatLong in HexagonArea.mapping.keys) {
                HexagonArea.mapping[currentLatLong]?.getNeighbors()?.forEach {
                    viewModelScope.launch(Dispatchers.IO) {
                        address = reverseGeocode(it.latitude, it.longitude)
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

    /**
     * Handles all Walskcore API calls. On a successful HTTP response, it build a HexagonArea object
     * with the retrieved data.
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param address The address of the location, if available.
     */
    private suspend fun makeAreaFromAPIRequest(latitude: Double, longitude: Double, address: String = "") {
        val url = URL(
            "https://api.walkscore.com/score?format=json&" +
                    "address=${URLEncoder.encode(address, "UTF-8")}&lat=${latitude}&" +
                    "lon=${longitude}&transit=1&bike=1&wsapikey=${BuildConfig.WALKSCORE_API_KEY}"
        )
        val connection = url.openConnection() as HttpURLConnection
        if (connection.responseCode == 200) {
            val inputSystem = connection.inputStream
            val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
            val request = Gson().fromJson(inputStreamReader, Request::class.java)
            inputStreamReader.close()
            inputSystem.close()
            withContext(hexContext) {

                HexagonArea(
                    latitude,
                    longitude,
                    request.walkscore,
                    address = address,
                    description = request.description
                )

            }


        } else
            Log.w("Connection Error", "Failed to connect")
    }

    /**
     * Reverse geocodes the coordinates to retrieve an address.
     * @param lat The latitude of the location.
     * @param long The longitude of the location.
     */
    private fun reverseGeocode(lat: Double, long: Double): String {
        return try {
            geoCoder.getFromLocation(lat, long, 1)?.get(0)?.getAddressLine(0)!!
        } catch (e: Exception) {
            "Could not locate"
        }
    }
}

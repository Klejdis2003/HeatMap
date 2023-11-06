package com.packages.heatmap.walkscore

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.packages.heatmap.BuildConfig
import com.packages.heatmap.walkscore.api.Request
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


fun main(args: Array<String>){
//   val url = URL("https://api.walkscore.com/score?format=json&" +
//           "address=1119%8th%20Avenue%20Seattle%20WA%2098101&lat=47.6085&" +
//           "lon=-122.3295&transit=1&bike=1&wsapikey=${BuildConfig.WALKSCORE_API_KEY}")
//   val connection = url.openConnection() as HttpURLConnection
//
//   if(connection.responseCode == 200){
//      val inputSystem = connection.inputStream
//      val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
//      val request = Gson().fromJson(inputStreamReader, Request::class.java)
//      inputStreamReader.close()
//      inputSystem.close()
//      print(request.walkscore)
//   }
//   else
//      println("Failed")

   val lt = LatLng(43.3, 21.8)
   print(lt)
}


package mobiledev.unb.ca.httpurlconnectiondemo.utils

import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object RestCountriesApiHelper {
    fun fetchDataFromUrl(url: String) : String? {
        return try {
            // API Key added as environment variable
            val apiKey = ""

            val connection = URL(url).openConnection() as HttpURLConnection
            try {
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = 5000
                    readTimeout = 5000
                    setRequestProperty("Authorization", "Bearer $apiKey")
                }

                return when (connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> connection.inputStream.bufferedReader()
                        .use { it.readText() }

                    else -> throw Exception("HTTP error code: $connection.responseCode")
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: IOException) {
            Log.e("RestCountriesApiHelper", "Error executing  api call", e)
            null
        }
    }
}

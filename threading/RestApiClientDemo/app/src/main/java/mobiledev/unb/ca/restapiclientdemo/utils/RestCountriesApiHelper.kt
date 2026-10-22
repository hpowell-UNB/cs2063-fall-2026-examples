package mobiledev.unb.ca.restapiclientdemo.utils

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object RestCountriesApiHelper {
    // Instantiate once for use throughout the app
    private val client = OkHttpClient.Builder()
        .connectTimeout(java.time.Duration.ofSeconds(5))
        .readTimeout(java.time.Duration.ofSeconds(5))
        .build()

    fun fetchDataFromUrl(url: String): String? {
        // API Key added as environment variable
        val apiKey = ""

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        return try {
            // NOTE: execute() runs synchronously.
            //  Wrap in Dispatchers.IO if calling from a Coroutine.
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body.string()
                } else {
                    throw IOException("HTTP error code: ${response.code}")
                }
            }
        } catch (e: IOException) {
            Log.e("RestCountriesApiHelper", "Error executing api call", e)
            null
        }
    }
}
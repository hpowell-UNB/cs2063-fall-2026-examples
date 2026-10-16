package mobiledev.unb.ca.httpurlconnectiondemo.utils

import mobiledev.unb.ca.httpurlconnectiondemo.models.CountryItem
import org.json.JSONArray
import org.json.JSONObject

class JsonUtils {
    lateinit var countryItemList: ArrayList<CountryItem>
        private set

    private fun processJSON() {
        countryItemList = ArrayList()

        val jsonString = loadJsonFromURL()

        jsonString?.let { jsonString ->
            val rootObject = JSONObject(jsonString.trimIndent())
            val dataObject = rootObject.getJSONObject("data")
            val objectsArray = dataObject.getJSONArray("objects")

            (0 until objectsArray.length()).forEach { index ->
                val country: JSONObject = objectsArray.getJSONObject(index)

                // Extract Name (Nested inside names -> common)
                val namesObj: JSONObject = country.getJSONObject("names")
                val countryName: String = namesObj.getString("common")

                // Extract Capital (Nested inside a list array -> first object -> name)
                val capitalsArray: JSONArray = country.getJSONArray("capitals")
                val capitalName: String = if (capitalsArray.length() > 0) {
                    capitalsArray.getJSONObject(0).optString("name")
                } else {
                    "N/A"
                }

                // Extract Flag Picture (Nested inside flag -> emoji)
                val flagObj: JSONObject = country.getJSONObject("flag")
                val flagPic: String = flagObj.getString("emoji")

                // Extract Continent (Nested inside continents string array -> first item)
                val continentsArray: JSONArray = country.getJSONArray("continents")
                val continentName: String = if (continentsArray.length() > 0) {
                    continentsArray.optString(0)
                } else {
                    "N/A"
                }

                val countryItem = CountryItem(
                    name = countryName,
                    flagPic = flagPic,
                    capital = capitalName,
                    continent = continentName
                )

                countryItemList.add(countryItem)
            }
        }
    }

    private fun loadJsonFromURL(): String? {
        return RestCountriesApiHelper.fetchDataFromUrl(REQUEST_URL)
    }

    // Initializer to read the data from the API endpoint
    init {
        processJSON()
    }

    companion object {
        private const val REQUEST_URL =
            "https://api.restcountries.com/countries/v5?region=Americas&fields=name,capital,continents,flags,flag"
    }
}
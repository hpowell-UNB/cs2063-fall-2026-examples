package mobiledev.unb.ca.restapiclientdemo.utils

import kotlinx.serialization.json.Json
import mobiledev.unb.ca.restapiclientdemo.models.CountryItem
import mobiledev.unb.ca.restapiclientdemo.models.CountryResponse

class JsonUtils {
    lateinit var countryItemList: ArrayList<CountryItem>
        private set

    // Reusable and thread-safe JSON configuration
    private val jsonConfig = Json {
        ignoreUnknownKeys = true // Prevents crashes if the API adds new fields later
    }

    private fun processJSON() {
        countryItemList = ArrayList()
        val jsonString = loadJsonFromURL()

        jsonString?.let { jsonString ->
            val root = jsonConfig.decodeFromString<CountryResponse>(jsonString.trimIndent())

            root.data.objects.forEach { country ->
                // Extract Name (Nested inside names -> common)
                val countryName = country.names.common

                // Extract Capital (Nested inside a list array -> first object -> name)
                val capitalName = country.capitals.firstOrNull()?.name ?: "N/A"

                // Extract Flag Picture (Nested inside flag -> emoji)
                val flagPic = country.flag.emoji

                // Extract Continent (Nested inside continents string array -> first item)
                val continentName = country.continents.firstOrNull() ?: "N/A"

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
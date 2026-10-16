package mobiledev.unb.ca.restapiclientdemo.models

import kotlinx.serialization.Serializable

@Serializable
data class CountryResponse(val data: DataContainer)

@Serializable
data class DataContainer(val objects: List<Country>)

@Serializable
data class Country(
    val names: Names,
    val capitals: List<Capital> = emptyList(),
    val flag: Flag,
    val continents: List<String> = emptyList()
)

@Serializable
data class Names(val common: String)

@Serializable
data class Capital(val name: String = "N/A")

@Serializable
data class Flag(val emoji: String)

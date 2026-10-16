package mobiledev.unb.ca.restapiclientdemo.models

data class CountryItem(val name: String,
                       val flagPic: String,
                       val capital: String,
                       val continent: String){
    // Custom getter for the title
    val title: String
        get() = "$name $flagPic"
}
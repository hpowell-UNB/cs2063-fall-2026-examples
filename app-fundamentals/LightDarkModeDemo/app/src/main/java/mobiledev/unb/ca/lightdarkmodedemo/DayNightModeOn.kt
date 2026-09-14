package mobiledev.unb.ca.lightdarkmodedemo

import android.content.Context
import android.content.res.Configuration

// An extension function to easily check anywhere you have a Context
fun Context.isDarkModeEnabled(): Boolean {
    val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return currentNightMode == Configuration.UI_MODE_NIGHT_YES
}
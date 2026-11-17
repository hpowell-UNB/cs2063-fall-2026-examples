package mobiledev.unb.ca.canvasdemo

/**
 * Lightweight data class to store properties
 */
data class Bubble(
    var x: Float,
    var y: Float,
    val stepX: Float,
    val stepY: Float
)
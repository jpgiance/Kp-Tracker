package com.autonomy_lab.kptracker.utils

import kotlin.math.pow
import kotlin.math.round

object Utils {

}

fun Float.roundToDecimal( decimalPlaces: Int): Float {
    val factor = 10.0.pow(decimalPlaces.toDouble())
    return (round(this * factor) / factor).toFloat()
}

fun Double.roundToDecimal( decimalPlaces: Int): Double {
    val factor = 10.0.pow(decimalPlaces.toDouble())
    return (round(this * factor) / factor)
}
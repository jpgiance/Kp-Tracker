package com.autonomy_lab.kptracker.data.models

import java.time.LocalDateTime

data class PlanetaryKIndexItem(
    val timeTag: LocalDateTime,
    val kpIndex: Double,
    val aRunning: Int,
    val stationCount: Int
)



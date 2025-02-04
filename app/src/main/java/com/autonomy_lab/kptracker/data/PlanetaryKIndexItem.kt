package com.autonomy_lab.kptracker.data

import co.yml.charts.common.model.Point
import java.time.LocalDateTime

data class PlanetaryKIndexItem(
    val timeTag: LocalDateTime,
    val kpIndex: Double,
    val aRunning: Int,
    val stationCount: Int
)



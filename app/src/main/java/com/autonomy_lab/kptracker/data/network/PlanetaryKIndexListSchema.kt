package com.autonomy_lab.kptracker.data.network

import android.util.Log
import com.autonomy_lab.kptracker.data.PlanetaryKIndexItem
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JsonClass(generateAdapter = true)
data class PlanetaryKIndexListSchema(
    val items: List<List<String>>
)

fun PlanetaryKIndexListSchema.toPlanetaryKIndexItemList(): List<PlanetaryKIndexItem>?{

    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

//    Log.e("TAG", "toPlanetaryKIndexItemList: \n\n" + items)
//    return null

    return items.drop(1).mapNotNull {row ->
        if (row.size >= 4) {
            PlanetaryKIndexItem(
                timeTag = LocalDateTime.parse(row[0], dateTimeFormatter),
                kpIndex = row[1].toDoubleOrNull() ?: 0.0,
                aRunning = row[2].toIntOrNull() ?: 0,
                stationCount = row[3].toIntOrNull() ?: 0
            )
        } else null

    }
}
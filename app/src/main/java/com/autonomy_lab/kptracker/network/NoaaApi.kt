package com.autonomy_lab.kptracker.network

import retrofit2.Response
import retrofit2.http.GET

interface NoaaApi {

    @GET("products/noaa-planetary-k-index.json")
    suspend fun fetchLastKpData(): Response<List<List<String>>>


}
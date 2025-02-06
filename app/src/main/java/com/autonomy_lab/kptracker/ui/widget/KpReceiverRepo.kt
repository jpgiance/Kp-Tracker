package com.autonomy_lab.kptracker.ui.widget

import android.util.Log
import com.autonomy_lab.kptracker.data.PlanetaryKIndexItem
import com.autonomy_lab.kptracker.data.network.PlanetaryKIndexListSchema
import com.autonomy_lab.kptracker.data.network.toPlanetaryKIndexItemList
import com.autonomy_lab.kptracker.di.ApplicationModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlin.random.Random

object KpReceiverRepo {

    private val noaaApi = ApplicationModule().noaaApi(ApplicationModule().retrofit())

    private var _latestKpValue = MutableStateFlow<Double?>(null)
    val latestKpValue: StateFlow<Double?> get() = _latestKpValue


    private var _kpIndexList = MutableStateFlow<List<PlanetaryKIndexItem>>(emptyList())
    val kpIndexList : StateFlow<List<PlanetaryKIndexItem>> get() = _kpIndexList

    suspend fun fetchKpIndexList(){

        withContext(Dispatchers.IO){

            try {

                val response = noaaApi.fetchLastKpData()

                if (response.isSuccessful){

                    val schema = response.body()?.let { PlanetaryKIndexListSchema(it) }

                    _kpIndexList.value = schema?.toPlanetaryKIndexItemList() ?: emptyList()

                    val latestItem: PlanetaryKIndexItem? = _kpIndexList.value.maxByOrNull { it.timeTag }

                    _latestKpValue.value = latestItem?.kpIndex
                }else{
                    Log.e("TAG", "doWork: Error Fetching data: Response Unsuccessful"  )
                }

            }catch (e: Exception){
                Log.e("TAG", "doWork: Error Fetching data: \n ${e.message}"  )
            }

        }

    }

    fun updateKpIndexFromViewModel(newKp: Double?){
        _latestKpValue.value = newKp

    }


    fun test(){
        _latestKpValue.value = Random.nextDouble(1.0, 10.0)

    }
}
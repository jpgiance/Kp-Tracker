package com.autonomy_lab.kptracker.ui.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.autonomy_lab.kptracker.data.PlanetaryKIndexItem
import com.autonomy_lab.kptracker.data.network.PlanetaryKIndexListSchema
import com.autonomy_lab.kptracker.data.network.toPlanetaryKIndexItemList
import com.autonomy_lab.kptracker.di.ApplicationModule

class UpdateWidgetWorker (
    private val context: Context,
    workerParameters: WorkerParameters
): CoroutineWorker(context, workerParameters) {

    private val noaaApi = ApplicationModule().noaaApi(ApplicationModule().retrofit())

    override suspend fun doWork(): Result {

        val response = noaaApi.fetchLastKpData()

        try {

            if (response.isSuccessful){

                val schema = response.body()?.let { PlanetaryKIndexListSchema(it) }

                val kpIndexList  = schema?.toPlanetaryKIndexItemList()?.sortedByDescending { it.timeTag }
                    ?: emptyList()


                val latestItem: PlanetaryKIndexItem? = kpIndexList.maxByOrNull { it.timeTag }

                val latestKpValue = latestItem?.kpIndex

                KpRepo.updateKpIndexFromViewModel(latestKpValue)
//                KpRepo.updateKpIndexFromViewModel(Random.nextDouble(1.0, 10.0))  // For testing only
                KpTrackerWidget().updateAll(context)

                return Result.success()

            }else{
                Log.e("TAG", "doWork: Error Fetching data: Response Unsuccessful. Retrying..."  )
                return Result.retry()
            }

        }catch (e:Exception){
            Log.e("TAG", "doWork: Error Fetching data: \n ${e.message}"  )
            return Result.failure()
        }


    }

}

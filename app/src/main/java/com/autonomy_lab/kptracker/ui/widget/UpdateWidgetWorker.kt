package com.autonomy_lab.kptracker.ui.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.autonomy_lab.kptracker.data.PlanetaryKIndexItem
import com.autonomy_lab.kptracker.data.SettingsData
import com.autonomy_lab.kptracker.data.network.PlanetaryKIndexListSchema
import com.autonomy_lab.kptracker.data.network.toPlanetaryKIndexItemList
import com.autonomy_lab.kptracker.di.ApplicationModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UpdateWidgetWorker (
    private val context: Context,
    workerParameters: WorkerParameters
): CoroutineWorker(context, workerParameters) {

    private val noaaApi = ApplicationModule().noaaApi(ApplicationModule().retrofit())
    private val notificationProvider = ApplicationModule().notificationProvider(context.applicationContext)
    private val kpRepo = KpRepo.get(context.applicationContext)

    override suspend fun doWork(): Result {




        Log.e("TAG", "doWork: Doing some work", )
        kpRepo.incrementNumb()

        val response = noaaApi.fetchLastKpData()

        try {

            if (response.isSuccessful){

                val schema = response.body()?.let { PlanetaryKIndexListSchema(it) }

                val kpIndexList  = schema?.toPlanetaryKIndexItemList()?.sortedByDescending { it.timeTag }
                    ?: emptyList()

                val latestItem: PlanetaryKIndexItem? = kpIndexList.maxByOrNull { it.timeTag }

                val latestKpValue = latestItem?.kpIndex

                KpReceiverRepo.updateKpIndexFromViewModel(latestKpValue)
//                KpRepo.updateKpIndexFromViewModel(Random.nextDouble(1.0, 10.0))  // For testing only
//                KpTrackerWidget().updateAll(context)

                kpRepo.valueChanged()
                if (latestKpValue != null) {
                    Log.e("TAG", "doWork: checking for notification", )
                    checkIfNotificationIsNeeded(latestKpValue)
                }

                Log.e("TAG", "doWork: finished", )

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

    private fun checkIfNotificationIsNeeded(latestKp: Double) {

        if (kpRepo.isNotificationsEnable()){

            if (latestKp >= kpRepo.notificationThreshold()){

                notificationProvider.sendNotification(title = "Kp index Notification threshold met", message = "Kp index is $latestKp")
            }
        }


    }

}

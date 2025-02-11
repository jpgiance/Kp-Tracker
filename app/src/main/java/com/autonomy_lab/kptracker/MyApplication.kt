package com.autonomy_lab.kptracker

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import com.autonomy_lab.kptracker.ui.widget.UpdateWidgetWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MyApplication() : Application(){


    override fun onCreate() {
        super.onCreate()

        scheduleUniquePeriodicWork()

    }



    suspend fun hasWorkWithTag(context: Context, tag: String): Boolean {
        val workManager = WorkManager.getInstance(context)

        val workQuery = WorkQuery.Builder
            .fromTags(listOf(tag))
            .addStates(listOf(
                WorkInfo.State.ENQUEUED,
                WorkInfo.State.RUNNING
            ))
            .build()

        return withContext(Dispatchers.IO) {
            workManager.getWorkInfos(workQuery).get()
        }.isNotEmpty()
    }

    suspend fun isWorkerActive(context: Context,  uniqueWorkName: String): Boolean {
        val workManager = WorkManager.getInstance(context)

        // Check for existing periodic work
        val workInfos = withContext(Dispatchers.IO) {
            workManager
                .getWorkInfosForUniqueWork(uniqueWorkName)  // Use your work name
                .get()
        }  // Note: Don't call on main thread

//        val workQuery = WorkQuery.Builder
//            .fromUniqueWorkNames(listOf("DataFetchWork"))
//            .addStates(listOf(
//                WorkInfo.State.ENQUEUED,
//                WorkInfo.State.RUNNING
//            ))
//            .build()

        return workInfos.any { workInfo ->
            // Check if work is scheduled or running
            listOf(
                WorkInfo.State.RUNNING,
                WorkInfo.State.ENQUEUED
            ).contains(workInfo.state)
        }

//        return withContext(Dispatchers.IO) {
//            workManager.getWorkInfos(workQuery).get()
//        }.isNotEmpty()


    }

    private fun scheduleUniquePeriodicWork(){
        val workRequest = PeriodicWorkRequestBuilder<UpdateWidgetWorker>(repeatInterval = 30, repeatIntervalTimeUnit = TimeUnit.MINUTES, flexTimeInterval = 5, flexTimeIntervalUnit = TimeUnit.MINUTES)
//            .setConstraints(Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .build())
//            .setExpedited()
            .setConstraints(androidx.work.Constraints.NONE)
            .addTag("My_worker")
            .build()

        val uniqueWork = OneTimeWorkRequestBuilder<UpdateWidgetWorker>()
//            .setConstraints(Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .build())
//            .setExpedited()
            .setConstraints(androidx.work.Constraints.NONE)
            .addTag("My_One_worker")
            .build()

//        WorkManager.initialize(applicationContext,Configuration.Builder()
//            .setMinimumLoggingLevel(android.util.Log.DEBUG)
//            .build())
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "DataFetchWork",
            ExistingPeriodicWorkPolicy.UPDATE,
//            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }


}


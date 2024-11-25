package com.autonomy_lab.kptracker

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.autonomy_lab.kptracker.ui.widget.UpdateWidgetWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class MyApplication() : Application(){


    override fun onCreate() {
        super.onCreate()

        val workRequest = PeriodicWorkRequestBuilder<UpdateWidgetWorker>(repeatInterval = 3, TimeUnit.HOURS)
            .setConstraints(Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build())
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "DataFetchWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

}


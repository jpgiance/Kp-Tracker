package com.autonomy_lab.kptracker

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.autonomy_lab.kptracker.ui.widget.UpdateWidgetWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import kotlin.math.log

@HiltAndroidApp
class MyApplication() : Application(){

    override fun onCreate() {
        super.onCreate()

        Log.e("TAG", "onCreate: From Application" )
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


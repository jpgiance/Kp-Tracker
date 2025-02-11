package com.autonomy_lab.kptracker.ui.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.autonomy_lab.kptracker.data.DataStoreManager
import com.autonomy_lab.kptracker.data.models.SettingsData
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class KpRepo @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val dataStoreManager: DataStoreManager
) {

    val _numb = MutableStateFlow<Int>(0)
    val numb: StateFlow<Int> get() = _numb

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface KpRepoEntryPoint{
        fun kpRepo(): KpRepo
    }


    companion object{
        fun get(applicationContext: Context): KpRepo{
            val kpRepoEntryPoint: KpRepoEntryPoint =
                EntryPoints.get(
                    applicationContext,
                    KpRepoEntryPoint::class.java,
                )
            return kpRepoEntryPoint.kpRepo()
        }


    }

    suspend fun valueChanged(){
        try {
            KpTrackerWidget().updateAll(appContext)
        }catch (_:Exception){}

    }

    fun loadSettings(): Flow<SettingsData>{
        return dataStoreManager.settingsState
    }

    fun isNotificationsEnable(): Boolean{
        return dataStoreManager.settingsState.value.notificationsEnabled
    }

    fun notificationThreshold(): Double{
        return dataStoreManager.settingsState.value.notificationThreshold
    }

    suspend fun incrementNumb(){
        dataStoreManager.incrementWidgetTestInt()
    }


}
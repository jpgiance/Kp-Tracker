package com.autonomy_lab.kptracker.ui.widget

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.viewModelScope
import com.autonomy_lab.kptracker.data.DataStoreManager
import com.autonomy_lab.kptracker.data.PlanetaryKIndexItem
import com.autonomy_lab.kptracker.data.PreferenceKeys
import com.autonomy_lab.kptracker.data.SettingsData
import com.autonomy_lab.kptracker.data.SettingsData.Companion.notificationThresholdDefault
import com.autonomy_lab.kptracker.data.SettingsData.Companion.notificationsEnabledDefault
import com.autonomy_lab.kptracker.data.SettingsData.Companion.widgetThresholdHighDefault
import com.autonomy_lab.kptracker.data.SettingsData.Companion.widgetThresholdLowDefault
import com.autonomy_lab.kptracker.data.network.PlanetaryKIndexListSchema
import com.autonomy_lab.kptracker.data.network.toPlanetaryKIndexItemList
import com.autonomy_lab.kptracker.di.ApplicationModule
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

class KpRepo @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val dataStoreManager: DataStoreManager
) {

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
        KpTrackerWidget().updateAll(appContext)
    }

    fun loadSettings(): Flow<SettingsData>{
        return dataStoreManager.settingsState
    }


}
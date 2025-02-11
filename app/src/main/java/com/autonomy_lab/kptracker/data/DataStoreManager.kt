package com.autonomy_lab.kptracker.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.autonomy_lab.kptracker.data.DataStoreManager.Companion.PREFERENCES_DATA_STORE_NAME
import com.autonomy_lab.kptracker.data.models.PreferenceKeys
import com.autonomy_lab.kptracker.data.models.SettingsData
import com.autonomy_lab.kptracker.data.models.SettingsData.Companion.notificationThresholdDefault
import com.autonomy_lab.kptracker.data.models.SettingsData.Companion.notificationsEnabledDefault
import com.autonomy_lab.kptracker.data.models.SettingsData.Companion.widgetThresholdHighDefault
import com.autonomy_lab.kptracker.data.models.SettingsData.Companion.widgetThresholdLowDefault
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_DATA_STORE_NAME)


class DataStoreManager(
    private val context: Context,
    private val dataStore: DataStore<Preferences> = context.dataStore
) {

    private val _settingsState = MutableStateFlow(SettingsData())
    val settingsState: StateFlow<SettingsData> = _settingsState




    init {
        loadSettings()
    }




    private fun loadSettings() {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.data
                .catch { e ->
                    // Handle exceptions here, e.g., log the error
                    emit(emptyPreferences()) // Emit empty preferences to avoid crashing
                }
                .map { preferences ->
                    SettingsData(
                        notificationsEnabled = preferences[PreferenceKeys.notificationsEnabled] ?: notificationsEnabledDefault,
                        notificationThreshold = preferences[PreferenceKeys.notificationThreshold] ?: notificationThresholdDefault,
                        widgetThresholdLow = preferences[PreferenceKeys.widgetThresholdLow] ?: widgetThresholdLowDefault,
                        widgetThresholdHigh = preferences[PreferenceKeys.widgetThresholdHigh] ?: widgetThresholdHighDefault,
                        testInt = preferences[PreferenceKeys.testInt] ?: 0
                    )
                }
                .collect { settings ->
                    _settingsState.value = settings
                }
        }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[PreferenceKeys.notificationsEnabled] = enabled
            }
        }
    }

    suspend fun updateNotificationThreshold(threshold: Double) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[PreferenceKeys.notificationThreshold] = threshold
            }
        }
    }

    suspend fun updateWidgetThresholdLow(threshold: Double) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[PreferenceKeys.widgetThresholdLow] = threshold
            }
        }
    }

    suspend fun updateWidgetThresholdHigh(threshold: Double) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[PreferenceKeys.widgetThresholdHigh] = threshold
            }
        }
    }

    suspend fun incrementWidgetTestInt() {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[PreferenceKeys.testInt] =  settingsState.value.testInt + 1
            }
        }
    }


    companion object{
        const val PREFERENCES_DATA_STORE_NAME = "settings"
    }

}
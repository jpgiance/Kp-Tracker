package com.autonomy_lab.kptracker.data.models

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

data class SettingsData(
    val notificationsEnabled: Boolean = notificationsEnabledDefault,
    val notificationThreshold: Double = notificationThresholdDefault,
    val widgetThresholdLow: Double = widgetThresholdLowDefault,
    val widgetThresholdHigh: Double = widgetThresholdHighDefault,
    val testInt: Int = 0
){

    companion object{
        const val notificationsEnabledDefault = false
        const val notificationThresholdDefault = 4.0
        const val widgetThresholdLowDefault = 3.0
        const val widgetThresholdHighDefault = 5.0
    }
}

// Preferences Keys
object PreferenceKeys {
    val notificationsEnabled = booleanPreferencesKey("notifications_enabled")
    val notificationThreshold = doublePreferencesKey("notification_threshold")
    val widgetThresholdLow = doublePreferencesKey("widget_threshold_low")
    val widgetThresholdHigh = doublePreferencesKey("widget_threshold_high")
    val testInt = intPreferencesKey("widget_testInt")


}
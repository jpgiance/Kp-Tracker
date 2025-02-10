package com.autonomy_lab.kptracker.ui.screens.settings

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autonomy_lab.kptracker.data.DataStoreManager
import com.autonomy_lab.kptracker.data.SettingsData
import com.autonomy_lab.kptracker.ui.dialogs.SnackBarAction
import com.autonomy_lab.kptracker.ui.dialogs.SnackBarController
import com.autonomy_lab.kptracker.ui.dialogs.SnackBarEvent
import com.autonomy_lab.kptracker.ui.widget.KpRepo
import com.autonomy_lab.kptracker.ui.widget.WidgetHelper
import com.autonomy_lab.kptracker.utils.roundToDecimal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val helper: WidgetHelper,
    private val kpRepo: KpRepo
): ViewModel() {

    val settingsState: StateFlow<SettingsData> = dataStoreManager.settingsState


    val visiblePermissionDialogQueue = mutableStateListOf<String>()


    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.updateNotificationsEnabled(enabled)
        }
    }

    fun updateNotificationThreshold(threshold: Double) {
        viewModelScope.launch {
            dataStoreManager.updateNotificationThreshold(threshold.roundToDecimal(1))
        }
    }

    fun updateWidgetThresholdLow(threshold: Double, ctx: Context) {
        viewModelScope.launch {
            dataStoreManager.updateWidgetThresholdLow(threshold.roundToDecimal(1))
            kpRepo.valueChanged()
        }
    }

    fun updateWidgetThresholdHigh(threshold: Double) {
        viewModelScope.launch {
            dataStoreManager.updateWidgetThresholdHigh(threshold.roundToDecimal(1))
            kpRepo.valueChanged()
        }
    }





    fun dismissDialog() {
        visiblePermissionDialogQueue.removeAt(0)
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }


    fun showSnackBar() {
        viewModelScope.launch {
            SnackBarController.sendEvent(
                event = SnackBarEvent(
                    message = "Hello from ViewModel",
                    action = SnackBarAction(
                        name = "Click me!",
                        action = {
                            SnackBarController.sendEvent(
                                event = SnackBarEvent(
                                    message = "Action pressed!"
                                )
                            )
                        }
                    )
                )
            )
        }
    }

}
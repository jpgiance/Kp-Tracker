package com.autonomy_lab.kptracker.ui.screens.settings

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.RoundedCorner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.autonomy_lab.kptracker.ui.components.UserInputDialog
import com.autonomy_lab.kptracker.ui.dialogs.LocationPermissionTextProvider
import com.autonomy_lab.kptracker.ui.dialogs.NotificationPermissionTextProvider
import com.autonomy_lab.kptracker.ui.dialogs.PermissionDialog
import com.autonomy_lab.kptracker.ui.theme.WidgetGreen
import com.autonomy_lab.kptracker.ui.theme.WidgetOrange
import com.autonomy_lab.kptracker.ui.theme.WidgetRed
import com.autonomy_lab.kptracker.utils.roundToDecimal

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {

    val context = LocalContext.current
    val notificationManager = remember { NotificationManagerCompat.from(context) }
    val isNotificationPermissionPending = remember{ mutableStateOf(false)}

    // Collect the state from the ViewModel
    val settingsData by settingsViewModel.settingsState.collectAsState()
    val settingsContentDescription = "Settings Screen"

    val dialogQueue = settingsViewModel.visiblePermissionDialogQueue

    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        }
        else {
            arrayOf(
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        }
    } else {
        arrayOf(
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            var allPermissionsGranted = true
            permissionsToRequest.forEach { permission ->
                settingsViewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true
                )

                if (perms[permission] != true) allPermissionsGranted = false
            }

//                        if (allPermissionsGranted) startMainService()
            isNotificationPermissionPending.value = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                perms[Manifest.permission.POST_NOTIFICATIONS]?.let {
                    settingsViewModel.updateNotificationsEnabled(it)
                }
            }

        }
    )


    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("Notifications", style = MaterialTheme.typography.titleLarge, )
            NotificationSettings(
                notificationsEnabled = settingsData.notificationsEnabled,
                notificationThreshold = settingsData.notificationThreshold,
                onNotificationsEnabledChange = {
                    Log.e("TAG", "SettingsScreen: counter: ${settingsData.testInt}" )
                    Log.e("TAG", "SettingsScreen: changed to $it" )
                    if (it){
                        Log.e("TAG", "SettingsScreen: launching permissions" )
                        isNotificationPermissionPending.value = true
                        multiplePermissionResultLauncher.launch(permissionsToRequest)


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if(notificationManager.areNotificationsEnabled() && ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED){
                                settingsViewModel.updateNotificationsEnabled(it)
                            }
                        } else {
                            settingsViewModel.updateNotificationsEnabled(it)
                        }


                    } else{
                        settingsViewModel.updateNotificationsEnabled(it)
                    }
                                               },
                onNotificationThresholdChange = { settingsViewModel.updateNotificationThreshold(it) }
            )
            HorizontalDivider(Modifier.height(32.dp))

            Text("Kp Widget", style = MaterialTheme.typography.titleLarge)
            WidgetSettings(
                widgetThresholdLow = settingsData.widgetThresholdLow,
                widgetThresholdHigh = settingsData.widgetThresholdHigh,
                onWidgetThresholdLowChange = { settingsViewModel.updateWidgetThresholdLow(it, context.applicationContext) },
                onWidgetThresholdHighChange = { settingsViewModel.updateWidgetThresholdHigh(it) }
            )
        }
    }

    dialogQueue
        .reversed()
        .forEach { permission ->
            PermissionDialog(
                permissionTextProvider = when (permission) {
                    Manifest.permission.POST_NOTIFICATIONS -> {
                        NotificationPermissionTextProvider()
                    }
                    Manifest.permission.ACCESS_FINE_LOCATION -> {
                        LocationPermissionTextProvider()
                    }
                    Manifest.permission.ACCESS_COARSE_LOCATION -> {
                        LocationPermissionTextProvider()
                    }
                    else -> return@forEach
                },
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    context as Activity,
                    permission
                ),
                onDismiss = settingsViewModel::dismissDialog,
                onOkClick = {
                    settingsViewModel.dismissDialog()
                    multiplePermissionResultLauncher.launch(
                        arrayOf(permission)
                    )
                },
                onGoToAppSettingsClick = {
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    ).also(context::startActivity)
                }
            )
        }

}

@SuppressLint("DefaultLocale")
@Composable
fun NotificationSettings(
    notificationsEnabled: Boolean,
    notificationThreshold: Double,
    onNotificationsEnabledChange: (Boolean) -> Unit,
    onNotificationThresholdChange: (Double) -> Unit
) {

    val showInputDialog = remember { mutableStateOf(false) }
    val titleInputDialog = remember { mutableStateOf("Enter New Value") }
    val oldValueInputDialog = remember { mutableFloatStateOf(0f) }


    UserInputDialog(
        showDialog = showInputDialog.value,
        onDismiss = {showInputDialog.value = false},
        onConfirm = {position, newValue ->
            onNotificationThresholdChange(newValue.toDouble())
            showInputDialog.value = false
        },
        title = titleInputDialog.value,
        oldValue = oldValueInputDialog.floatValue
    )

    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enable Notifications")
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = onNotificationsEnabledChange,
                modifier = Modifier.semantics { contentDescription = "Enable Notifications Switch" }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Trigger when Kp is greater than: ")
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ){
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.LightGray)
                        .clickable(onClick = {
                            oldValueInputDialog.floatValue = notificationThreshold
                                .toFloat()
                                .roundToDecimal(1)
                            titleInputDialog.value = "Notification Threshold"
                            showInputDialog.value = true
                        })
                ){
                    Text(
                        text = String.format("%.1f", notificationThreshold),
                        color = Color.Black,
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                }
            }

//            Slider(
//                value = notificationThreshold.toFloat(),
//                onValueChange = { onNotificationThresholdChange(it.toDouble()) },
//                valueRange = 0f..200f,
//                steps = 199,
//                modifier = Modifier.semantics { contentDescription = "Notification Threshold Slider" }
//            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun WidgetSettings(
    widgetThresholdLow: Double,
    widgetThresholdHigh: Double,
    onWidgetThresholdLowChange: (Double) -> Unit,
    onWidgetThresholdHighChange: (Double) -> Unit
) {


    val showInputDialog = remember { mutableStateOf(false) }
    val widgetValueType = remember { mutableStateOf(WidgetValueType.NONE) }
    val titleInputDialog = remember { mutableStateOf("Enter New Value") }
    val oldValueInputDialog = remember { mutableFloatStateOf(0f) }


    UserInputDialog(
        showDialog = showInputDialog.value,
        onDismiss = {showInputDialog.value = false},
        onConfirm = {position, newValue ->
            when(widgetValueType.value){
                WidgetValueType.WIDGET_THRESHOLD_HIGH ->{
                    onWidgetThresholdHighChange(newValue.toDouble())
                }
                WidgetValueType.WIDGET_THRESHOLD_LOW -> {
                    onWidgetThresholdLowChange(newValue.toDouble())
                }
                WidgetValueType.NONE -> {}
            }
            widgetValueType.value = WidgetValueType.NONE
            showInputDialog.value = false
        },
        title = titleInputDialog.value,
        oldValue = oldValueInputDialog.floatValue
    )


    Column(modifier = Modifier.padding(8.dp)) {
//        Text("Quiet < $widgetThresholdLow < Unsettled < $widgetThresholdHigh < Storm")

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("QUITE Threshold: ")
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ){
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.LightGray)
                        .clickable(onClick = {
                            widgetValueType.value = WidgetValueType.WIDGET_THRESHOLD_LOW
                            oldValueInputDialog.floatValue = widgetThresholdLow
                                .toFloat()
                                .roundToDecimal(1)
                            showInputDialog.value = true
                        })
                ){
                    Text(
                        text = String.format("%.1f", widgetThresholdLow),
                        color = Color.Black,
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                }
            }
//            Slider(
//                value = widgetThresholdLow.toFloat(),
//                onValueChange = { onWidgetThresholdLowChange(it.toDouble()) },
//                valueRange = 0f..100f,
//                steps = 99,
//                modifier = Modifier.semantics { contentDescription = "Widget Low Threshold Slider" }
//            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("High Threshold: ")
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ){
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.LightGray)
                        .clickable(onClick = {
                            widgetValueType.value = WidgetValueType.WIDGET_THRESHOLD_HIGH
                            oldValueInputDialog.floatValue = widgetThresholdHigh
                                .toFloat()
                                .roundToDecimal(1)
                            showInputDialog.value = true
                        })
                ){
                    Text(
                        String.format("%.1f", widgetThresholdHigh),
                        color = Color.Black,
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                }
            }
//            Slider(
//                value = widgetThresholdHigh.toFloat(),
//                onValueChange = { onWidgetThresholdHighChange(it.toDouble()) },
//                valueRange = 0f..100f,
//                steps = 99,
//                modifier = Modifier.semantics { contentDescription = "Widget High Threshold Slider" }
//            )
        }
    }
}



enum class WidgetValueType{
    WIDGET_THRESHOLD_LOW,
    WIDGET_THRESHOLD_HIGH,
    NONE
}


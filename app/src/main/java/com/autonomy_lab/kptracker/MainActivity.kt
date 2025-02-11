package com.autonomy_lab.kptracker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.autonomy_lab.kptracker.ui.components.DrawerContent
import com.autonomy_lab.kptracker.ui.components.MainTopBar
import com.autonomy_lab.kptracker.ui.dialogs.SnackBarController
import com.autonomy_lab.kptracker.ui.navigation.HelpAndFeedbackRoute
import com.autonomy_lab.kptracker.ui.navigation.MainScreenRoute
import com.autonomy_lab.kptracker.ui.navigation.RawDataScreenRoute
import com.autonomy_lab.kptracker.ui.navigation.ScreenNavigator
import com.autonomy_lab.kptracker.ui.navigation.SettingsScreenRoute
import com.autonomy_lab.kptracker.ui.theme.KpTrackerTheme
import com.autonomy_lab.kptracker.utils.Constants.DAYS_FOR_FLEXIBLE_UPDATE
import com.autonomy_lab.kptracker.utils.ObserveAsEvents
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class MainActivity() : ComponentActivity() {


    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
        // handle callback
        if (result.resultCode != RESULT_OK) {
            Log.e("","Update flow failed! Result code: " + result.resultCode);
            // If the update is canceled or fails,
            // you can request to start the update again.
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkForAppUpdate()


//        enableEdgeToEdge()
        setContent {
            KpTrackerTheme {
                val mainViewModel = hiltViewModel<MainViewModel>()
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val snackBarHostState = remember { SnackbarHostState() }

                LaunchedEffect(true) {
                    mainViewModel.showFeedBackDialog(context = applicationContext, activity = this@MainActivity)
                }

                LaunchedEffect(navController) {
                    navController.addOnDestinationChangedListener { _, destination, _ ->
                        mainViewModel.updateTopBar(destination.route)
                    }
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        DrawerContent(
                            onRawDataNavClicked = {
                                navController.navigate(RawDataScreenRoute)
                                scope.launch {drawerState.close()}
                            },
                            onHomeNavClicked = {
                                navController.navigate(MainScreenRoute)
                                scope.launch {drawerState.close()}
                            },
                            onHelpAndFeedbackNavClicked = {
                                navController.navigate(HelpAndFeedbackRoute)
                                scope.launch {drawerState.close()}
                            },
                            onSettingsNavClicked = {
                                navController.navigate(SettingsScreenRoute)
                                scope.launch {drawerState.close()}
                            },
                        )
                    },
                ) {

                    ObserveAsEvents(
                        flow = SnackBarController.events,
                        snackBarHostState
                    ) { event ->
                        scope.launch {
                            snackBarHostState.currentSnackbarData?.dismiss()

                            val result = snackBarHostState.showSnackbar(
                                message = event.message,
                                actionLabel = event.action?.name,
                                duration = SnackbarDuration.Long
                            )

                            if(result == SnackbarResult.ActionPerformed) {
                                event.action?.action?.invoke()
                            }
                        }
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            MainTopBar(
                                mainViewModel = mainViewModel,
                                onDrawerIconClicked = {
                                    scope.launch {
                                        if (drawerState.isClosed) {
                                            drawerState.open()
                                        } else {
                                            drawerState.close()
                                        }
                                    }
                                }
                            )
                        },
                        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
                        content = { innerPadding ->
                            ScreenNavigator(
                                navController = navController,
                                innerPadding = innerPadding,
                                viewModel = mainViewModel
                            )
                        }
                    )
                }

            }
        }
    }


    fun checkForAppUpdate(){
        val appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && (appUpdateInfo.clientVersionStalenessDays() ?: -1) >= DAYS_FOR_FLEXIBLE_UPDATE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // an activity result launcher registered via registerForActivityResult
                    activityResultLauncher,
                    // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                    // flexible updates.
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
            }
        }
    }


}


fun LocalDateTime.toStringForDisplay(): String{
    val utcZone = ZoneId.of("UTC")
    val localZone = ZoneId.systemDefault()

    val utcDateTime = ZonedDateTime.of(this, utcZone)
    val localDateTime = utcDateTime.withZoneSameInstant(localZone)

    val formatter = DateTimeFormatter.ofPattern("h:mm a MMM d")
    return localDateTime.format(formatter)
}


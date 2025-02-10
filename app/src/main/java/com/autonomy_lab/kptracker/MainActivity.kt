package com.autonomy_lab.kptracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberDrawerState
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
import com.autonomy_lab.kptracker.utils.ObserveAsEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class MainActivity() : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            KpTrackerTheme {
                val mainViewModel = hiltViewModel<MainViewModel>()
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val snackBarHostState = remember { SnackbarHostState() }

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




}


fun LocalDateTime.toStringForDisplay(): String{
    val utcZone = ZoneId.of("UTC")
    val localZone = ZoneId.systemDefault()

    val utcDateTime = ZonedDateTime.of(this, utcZone)
    val localDateTime = utcDateTime.withZoneSameInstant(localZone)

    val formatter = DateTimeFormatter.ofPattern("h:mm a MMM d")
    return localDateTime.format(formatter)
}


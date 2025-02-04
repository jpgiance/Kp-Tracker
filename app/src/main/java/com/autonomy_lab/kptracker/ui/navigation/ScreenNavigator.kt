package com.autonomy_lab.kptracker.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.autonomy_lab.kptracker.MainViewModel
import com.autonomy_lab.kptracker.ui.screens.help_feedback.HelpAndFeedbackScreen
import com.autonomy_lab.kptracker.ui.screens.main.MainScreen
import com.autonomy_lab.kptracker.ui.screens.rawdata.RawDataScreen

@Composable
fun ScreenNavigator(
    navController: NavHostController,
    innerPadding: PaddingValues,
    viewModel: MainViewModel
) {

    NavHost(
        navController = navController,
        startDestination = MainScreenRoute,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable<MainScreenRoute>{
            MainScreen(viewModel)
        }

        composable<RawDataScreenRoute> {
            RawDataScreen(
                viewModel = viewModel
            )
        }

        composable<HelpAndFeedbackRoute> {
            HelpAndFeedbackScreen()
        }
    }
}
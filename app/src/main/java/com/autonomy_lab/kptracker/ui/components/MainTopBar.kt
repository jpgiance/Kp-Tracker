package com.autonomy_lab.kptracker.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.sharp.Refresh
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.autonomy_lab.kptracker.R
import com.autonomy_lab.kptracker.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    onDrawerIconClicked: () -> Unit
) {

    val basState by mainViewModel.topBarState.collectAsStateWithLifecycle()
    val refreshing = mainViewModel.refreshing.collectAsStateWithLifecycle()

    TopAppBar(
        title = {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){

                Text(
                    text = basState.title,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(16.dp))

                if (refreshing.value){
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onDrawerIconClicked) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
        },
        actions = {
            if (basState.isRefreshIconVisible){
                IconButton(
                    onClick = { mainViewModel.fetchKpIndexList()}
                ) {
                    Icon(
                        imageVector = Icons.Sharp.Refresh,
                        contentDescription = "Localized description",
                        tint = Color.White
                    )
                }
            }

        },
        colors = TopAppBarDefaults.topAppBarColors(Color.Black),
    )

}
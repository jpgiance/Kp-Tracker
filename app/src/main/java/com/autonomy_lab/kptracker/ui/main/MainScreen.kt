package com.autonomy_lab.kptracker.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.sharp.Refresh
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autonomy_lab.kptracker.R
import com.autonomy_lab.kptracker.data.PlanetaryKIndexItem
import com.autonomy_lab.kptracker.ui.dialogs.InfoDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: KpTrackerWidgetViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {


    val list by viewModel.kpIndexList.collectAsState()
    val listIsEmpty by remember { derivedStateOf { list.isEmpty() } }
    val refreshing by viewModel.refreshing.collectAsState()
    val internetIsAvailable by viewModel.isInternetAvailable.collectAsState()
    var openInfoDialog = remember { mutableStateOf(false) }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){

                        Text(
                            text = stringResource(id = R.string.app_name),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(16.dp))

                        if (refreshing){
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.fetchKpIndexList() }) {
                        Icon(
                            imageVector = Icons.Sharp.Refresh,
                            contentDescription = "Localized description",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { openInfoDialog.value = true }) {
                        Icon(
                            imageVector = Icons.Sharp.Settings,
                            contentDescription = "Localized description",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Color.Black),
            )
        },
        content = { innerPadding ->

            InfoDialog(
                innerPadding = innerPadding,
                onDismiss = { openInfoDialog.value = false },
                showDialog = openInfoDialog.value,
                onConfirm = {  },
            )

            if (internetIsAvailable){
                if (listIsEmpty){

                    MessageDisplay(
                        innerPadding = innerPadding,
                        text = "Nothing to show \n\n Please press Refresh for trying again",
                    )

                }else{

                    LazyColumn (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(vertical = 5.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)

                    ) {
                        items(list.size) { index ->

                            ItemContent(item = list[index])

                            if (index < list.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .padding(top = 20.dp),
                                    thickness = 2.dp
                                )
                            }
                        }


                    }

                }
            }else{

                MessageDisplay(
                    innerPadding = innerPadding,
                    text = "No Internet Available \n\n Please connect to the Internet"
                )
            }



        }
    )
}


@Composable
fun ItemContent(
    item: PlanetaryKIndexItem,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = Modifier
            .padding(start = 16.dp)
    ){
        Text( text = "time tag: ${item.timeTag}" )
        Spacer(modifier = Modifier.size(2.dp))
        Text( text = "kp: ${item.kpIndex}" )
        Spacer(modifier = Modifier.size(2.dp))
        Text( text = "a running: ${item.aRunning}" )
        Spacer(modifier = Modifier.size(2.dp))
        Text( text = "station count: ${item.stationCount}" )

    }
}

@Composable
fun MessageDisplay(modifier: Modifier = Modifier, innerPadding: PaddingValues, text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .padding(innerPadding),
        contentAlignment = Alignment.Center
    ){
        Text(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            text = text,
            color = Color.White
            )
    }
}
package com.autonomy_lab.kptracker.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autonomy_lab.kptracker.R
import com.autonomy_lab.kptracker.data.PlanetaryKIndexItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: KpTrackerWidgetViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {


    val list = viewModel.kpIndexList.collectAsState()



    LaunchedEffect(Unit) {
        viewModel.fetchKpIndexList()
    }


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
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Color.Black),
            )
        },
        content = { innerPadding ->

            LazyColumn (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(vertical = 5.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)

            ) {
                items(list.value.size) { index ->
                    val item = list.value[index]

                    ItemContent(item = item)

                    if (index < list.value.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(top = 20.dp),
                            thickness = 2.dp
                        )
                    }
                }


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
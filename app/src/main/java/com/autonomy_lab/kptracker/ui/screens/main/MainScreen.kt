package com.autonomy_lab.kptracker.ui.screens.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.autonomy_lab.kptracker.MainViewModel
import com.autonomy_lab.kptracker.data.models.PlanetaryKIndexItem
import com.autonomy_lab.kptracker.ui.components.ChartComponent
import com.autonomy_lab.kptracker.ui.components.NoaaKpScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {


    val list by viewModel.kpIndexList.collectAsState()
//    val listIsEmpty by remember { derivedStateOf { list.isEmpty() } }
//    val refreshing by viewModel.refreshing.collectAsState()
//    val internetIsAvailable by viewModel.isInternetAvailable.collectAsState()
//    var openInfoDialog = remember { mutableStateOf(false) }

    val latestKpValue by viewModel.latestKpValue.collectAsStateWithLifecycle()
    val latestKpValueTime by viewModel.latestKpValueTime.collectAsStateWithLifecycle()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "Last update:",
                style = MaterialTheme.typography.titleLarge
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            contentAlignment = Alignment.Center
        ){
            Row(

            ) {

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "Kp index: $latestKpValue",
                        color = Color.Black,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = " $latestKpValueTime",
                        color = Color.Black,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

            }
        }
        Box(
            modifier = Modifier
                .padding(start = 8.dp, end = 16.dp),
            contentAlignment = Alignment.Center
        ){
            ChartComponent(list = list)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ){
            NoaaKpScale()
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ){
                Text(
                    text = "NOAA Scales Geomagnetic Storms",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }


    }


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
fun MessageDisplay(
    modifier: Modifier = Modifier,
//    innerPadding: PaddingValues,
    text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
//            .padding(innerPadding),
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
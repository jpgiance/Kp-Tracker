package com.autonomy_lab.kptracker.ui.screens.rawdata

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.autonomy_lab.kptracker.MainViewModel
import com.autonomy_lab.kptracker.data.PlanetaryKIndexItem
import com.autonomy_lab.kptracker.ui.dialogs.InfoDialog
import com.autonomy_lab.kptracker.ui.screens.main.MessageDisplay


@Composable
fun RawDataScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {


    val list by viewModel.kpIndexList.collectAsState()
    val listIsEmpty by remember { derivedStateOf { list.isEmpty() } }
    val refreshing by viewModel.refreshing.collectAsState()
    val internetIsAvailable by viewModel.isInternetAvailable.collectAsState()
    var openInfoDialog = remember { mutableStateOf(false) }





    InfoDialog(
//        innerPadding = innerPadding,
        onDismiss = { openInfoDialog.value = false },
        showDialog = openInfoDialog.value,
        onConfirm = {  },
    )

    if (internetIsAvailable){
        if (listIsEmpty){

            MessageDisplay(
//                innerPadding = innerPadding,
                text = "Nothing to show \n\n Please press Refresh for trying again",
            )

        }else{

            LazyColumn (
                modifier = Modifier
                    .fillMaxSize()
//                    .padding(innerPadding)
                    .padding(vertical = 5.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)

            ) {
                items(list.size) { index ->

                    com.autonomy_lab.kptracker.ui.screens.main.ItemContent(item = list[index])

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
//            innerPadding = innerPadding,
            text = "No Internet Available \n\n Please connect to the Internet"
        )
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
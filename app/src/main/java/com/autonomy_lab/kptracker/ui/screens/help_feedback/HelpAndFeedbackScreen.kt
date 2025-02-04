package com.autonomy_lab.kptracker.ui.screens.help_feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autonomy_lab.kptracker.ui.dialogs.LinkedText

@Composable
fun HelpAndFeedbackScreen(modifier: Modifier = Modifier) {

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ){
            Text(
                textAlign = TextAlign.Center,
                text = "About this App",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,


                )
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {



            Text(
                text = "The Kp Index Tracker app provides real-time updates on the Kp Index, " +
                        "a global geomagnetic activity measure ranging from 0 to 9. The Kp Index " +
                        "is used to monitor geomagnetic storms caused by solar activity, which can " +
                        "impact satellite communications, GPS systems, power grids, and even trigger stunning auroras.\n\n" +
                        "A Widget is available for your convenience, showing the index value and a GREEN, ORANGE or RED label " +
                        "depending on the value. The data is updated in the background every 3 hours from the NOAA's website",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "About the Developer",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "This app was developed by a solo developer and available for free and Ads free for your satisfaction. " +
                        "I would greatly appreciate if you share your feedback or let me know if you'd like to add a specific feature to the app. " +
                        "If you like my work and would like to hire my services, you can contact me using the following links.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            LinkedText(
                text = "Visit our website: ",
                url = "https://autonomy-lab.com",
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinkedText(
                text = "Email your feedback: ",
                url = "jpgiance@gmail.com",
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Thank you for using Kp Index Tracker!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HelpPreview() {
    HelpAndFeedbackScreen()
}
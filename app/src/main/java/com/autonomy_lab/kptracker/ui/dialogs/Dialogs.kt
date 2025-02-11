package com.autonomy_lab.kptracker.ui.dialogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun InfoDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    title: String = "About this App",
//    innerPadding: PaddingValues
) {

    if (showDialog) {

        Dialog(
            onDismissRequest = onDismiss
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
//                    .padding(innerPadding)
                    .padding(16.dp)
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.DarkGray)
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        textAlign = TextAlign.Center,
                        text = title,
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
                        text = "Visit our website: \n",
                        url = "https://autonomy-lab.com",
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinkedText(
                        text = "Email your feedback: \n",
                        url = "jpgiance@gmail.com",
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Thank you for using Kp Index Tracker!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    )

                }
            }
        }
    }
}



@Composable
fun LinkedText(text: String, url: String, clickableAction: (() -> Unit)? = null) {


    val annotatedLinkString: AnnotatedString = remember {
        buildAnnotatedString {

            val style = SpanStyle(
                color = Color.White,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                letterSpacing = 0.2.sp
            )

            val styleCenter = SpanStyle(
                color = Color(0xff64B5F6),
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                letterSpacing = 0.2.sp,
                textDecoration = TextDecoration.Underline
            )

            withStyle(
                style = style
            ) {
                append(text)
            }

            if (clickableAction == null){
                withLink(LinkAnnotation.Url(url = url)) {
                    withStyle(
                        style = styleCenter
                    ) {
                        append(url)
                    }
                }
            }else{

                withStyle(
                    style = styleCenter
                ) {
                    append(url)
                }

            }


            withStyle(
                style = style
            ) {
                append(" ")
            }
        }
    }

    if (clickableAction == null){
        Text(
            text = annotatedLinkString
        )
    }else{
        Text(
            text = annotatedLinkString,
            modifier = Modifier.clickable(enabled = true, onClick = clickableAction)
        )
    }

}






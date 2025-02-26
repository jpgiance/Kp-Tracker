package com.autonomy_lab.kptracker.ui.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.autonomy_lab.kptracker.MainActivity
import com.autonomy_lab.kptracker.data.models.SettingsData
import com.autonomy_lab.kptracker.ui.theme.WidgetGreen
import com.autonomy_lab.kptracker.ui.theme.WidgetOrange
import com.autonomy_lab.kptracker.ui.theme.WidgetRed
import kotlinx.coroutines.launch
import javax.inject.Inject

class KpTrackerWidget(

) : GlanceAppWidget() {



    companion object {
        private val SMALL_SQUARE = DpSize(100.dp, 100.dp)
        private val HORIZONTAL_RECTANGLE = DpSize(250.dp, 100.dp)
        private val BIG_SQUARE = DpSize(250.dp, 250.dp)

    }

    override val sizeMode = SizeMode.Responsive(
        setOf(
            SMALL_SQUARE,
            HORIZONTAL_RECTANGLE,
            BIG_SQUARE
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        val kpRepo = KpRepo.get(context)

        provideContent {
            // create your AppWidget here
            MyContent(kpRepo)
        }
    }


    @Composable
    fun MyContent(kpRepo: KpRepo) {

        val size = LocalSize.current
        val latestKp by KpReceiverRepo.latestKpValue.collectAsState()
        val settingsData = kpRepo.loadSettings().collectAsState(SettingsData())
        val numb by kpRepo.numb.collectAsState()

        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val updateKp: () -> Unit = {
            scope.launch {
                KpReceiverRepo.fetchKpIndexList()
                KpTrackerWidget().updateAll(context)
            }
        }

        val test: () -> Unit = {
            scope.launch {
                KpReceiverRepo.test()
                KpTrackerWidget().updateAll(context)
            }
        }

        var widgetTitle = ""

        var backgroundColor = Color.Transparent

        latestKp?.let {
            when {
                it < settingsData.value.widgetThresholdLow -> {
                    backgroundColor = WidgetGreen
                    widgetTitle = "QUIET"
                } // Green color for value < 4
                it > settingsData.value.widgetThresholdHigh -> {
                    backgroundColor = WidgetRed
                    widgetTitle = "STORM"
                } // Red color for value > 4
                it >= settingsData.value.widgetThresholdLow && it <= settingsData.value.widgetThresholdHigh ->{
                    backgroundColor = WidgetOrange
                    widgetTitle = "UNSETTLED"
                } // Orange color for value = 4
                else  -> {
                    backgroundColor = Color.DarkGray
                    widgetTitle = "UNKNOWN"
                } // Orange color for value = 4
            }
        } ?: {
            backgroundColor = WidgetOrange
            widgetTitle = "---"
        }

        GlanceTheme {

            if (latestKp == null){
                Column (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    Text("Data not available")
                    Button("Refresh", updateKp)
                }
            }else{
                Row(
                    modifier = GlanceModifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .clickable(actionStartActivity<MainActivity>()),
//                        .clickable(updateKp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Spacer(GlanceModifier.size(8.dp))
                    Box (
                        contentAlignment = Alignment.Center,
                        modifier = GlanceModifier.background(backgroundColor).cornerRadius(5.dp),
                    ){
                        Text(
                            text = widgetTitle,
                            style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 18.sp),
                            modifier = GlanceModifier.padding(start = 10.dp, top = 3.dp, end = 10.dp, bottom = 3.dp)
                        )
                    }
                    Spacer(GlanceModifier.size(8.dp))
                    Box (
                        contentAlignment = Alignment.Center,
                        modifier = GlanceModifier.background(GlanceTheme.colors.widgetBackground)
                    ){
                        Text(
                            text = "kp: $latestKp",
//                            text = "kp: ${settingsData.value.widgetThresholdLow}",
                            style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 18.sp),
                            maxLines = 1,
                            modifier = GlanceModifier.padding(start = 10.dp, top = 3.dp, end = 10.dp, bottom = 3.dp)
                        )
                    }


                }
            }
        }



    }



}

class WidgetHelper @Inject constructor(private val context: Context){

    suspend fun updateKpWidget(){
        KpTrackerWidget().updateAll(context = context)

//        val manager = GlanceAppWidgetManager(context)
//        val widget = KpTrackerWidget()
//        val glanceIds = manager.getGlanceIds(widget.javaClass)
//        glanceIds.forEach { glanceId ->
//            widget.update(context, glanceId)
//        }
    }
}


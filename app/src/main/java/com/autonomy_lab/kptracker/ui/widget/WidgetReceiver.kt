package com.autonomy_lab.kptracker.ui.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class WidgetReceiver (): GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = KpTrackerWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        CoroutineScope(Dispatchers.IO).launch {

//            KpRepo.test()
            KpReceiverRepo.fetchKpIndexList()

        }
    }
}
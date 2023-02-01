package com.example.airquality.libraryComponent

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.airquality.MainActivity
import com.example.airquality.services.DataViewModel
import com.example.airquality.services.room.SensorModel
import com.example.airquality.ui.theme.Red
import com.example.airquality.ui.theme.Yellow
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot
import java.util.Objects

@Composable
fun LineGraph(model: DataViewModel, date: String, indicator: String) {

    val data: List<SensorModel>? by model.getDataInDate("$date%").observeAsState()

    val dataDisplay = data?.map {
        it.getValue(indicator = indicator.lowercase())?.let { it1 ->
            DataPoint(
                x = (it.time?.split(",")?.get(1)?.trim()?.split(":")?.get(0)?.toFloat() ?: 0f),
                y = it1.toFloat())
        }
    }
    Log.d(MainActivity.tag, "LineGraph: $dataDisplay")

    // get size of phone's screen
    val screenPixelDensity = LocalContext.current.resources.displayMetrics.density
    val dpValue = Resources.getSystem().displayMetrics.widthPixels / screenPixelDensity
    val cardSize = dpValue * 0.8

    if (dataDisplay != null && dataDisplay.isNotEmpty()) {
        LineGraph(
            plot = LinePlot(
                listOf(
                    LinePlot.Line(
                        dataDisplay as List<DataPoint>,
                        LinePlot.Connection(color = Red),
                        LinePlot.Intersection(color = Red),
                        LinePlot.Highlight(color = Yellow),
                    )
                ),
                grid = LinePlot.Grid(Red, steps = 4),
            ),
            modifier = Modifier
                .width(cardSize.dp)
                .height(300.dp),
            onSelection = { xLine, points ->
                // Do whatever you want here
            }
        )
    }
}


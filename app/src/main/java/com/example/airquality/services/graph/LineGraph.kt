package com.example.airquality.services.graph

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.airquality.MainActivity
import com.example.airquality.services.DataViewModel
import com.example.airquality.services.dataclass.DataIndicator
import com.example.airquality.services.room.SensorModel
import com.example.airquality.ui.theme.Red
import com.example.airquality.ui.theme.Yellow
import com.example.airquality.ui.theme.medium
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot

@Composable
fun LineGraph(model: DataViewModel, date: String, indicator: String) {

    val data: List<SensorModel>? by model.getDataInDate("$date%").observeAsState()
    var dataDisplay by remember { mutableStateOf<List<DataPoint>?>(null) }

    // map data according to indicator
    val indicatorArray = data?.map {
        it.getValue(indicator = indicator.lowercase())?.let { it1 ->
            it.time?.let { it2 ->
                DataIndicator(
                    it1,
                    it2
                )
            }
        }
    }

    // get all the time that have value
    val time =
        indicatorArray?.map { it?.time?.split(",")?.get(1)?.trim()?.split(":")?.get(0) }?.toSet()
    Log.d(MainActivity.tag, "LineGraph time set: $time")

    // more than 8 hours a day have data
    if ((time?.size ?: 0) >= 8) {
        val listData = mutableListOf<DataPoint>()
        time?.map {
            indicatorArray.find { item ->
                item?.time?.split(",")?.get(1)?.trim()?.split(":")?.get(0) == it
            }
        }?.forEach {
            if (it != null) {
                Log.d(
                    MainActivity.tag,
                    "LineGraph indicator by time: value ${it.value} time ${it.time}"
                )
            }
            it?.value?.toFloat()
                ?.let { it1 ->
                    DataPoint(
                        it.time.split(",")[1].trim().split(":")[0].toFloat(),
                        it1
                    )
                }
                ?.let { it2 -> listData.add(it2) }
        }
        if (dataDisplay != listData) {
            Log.d(MainActivity.tag, "LineGraph: update $listData")
            dataDisplay = listData.sortedByDescending { item -> item.x }
        }

        // less than 8 hours a day have data
    } else if ((time?.size ?: 0) > 0) {
        val listData = mutableListOf<DataPoint>()
        time?.map {
            indicatorArray.filter { item ->
                item?.time?.split(",")?.get(1)?.trim()?.split(":")?.get(0) == it
            }
        }?.forEach {
            val length = it.size
            it[0]?.value?.toFloat()
                ?.let { it1 ->
                    it[0]?.time?.split(",")?.get(1)?.trim()?.split(":")?.get(0)
                        ?.let { it2 -> DataPoint(it2.toFloat(), it1) }
                }
                ?.let { it2 -> listData.add(it2) }
            it[length / 2]?.value?.toFloat()
                ?.let { it1 ->
                    it[length / 2]?.time?.split(",")?.get(1)?.trim()?.split(":")?.get(0)
                        ?.let { it2 -> DataPoint(it2.toFloat(), it1) }
                }
                ?.let { it2 -> listData.add(it2) }
        }
        if (dataDisplay != listData) {
            Log.d(MainActivity.tag, "LineGraph: update $listData")
            dataDisplay = listData.sortedByDescending { item -> item.x }
        }
    } else {
        dataDisplay = null
    }

    dataDisplay?.forEach {
        Log.d(
            MainActivity.tag,
            "LineGraph: data for displaying x = ${it.x} y = ${it.y}"
        )
    }

    // get size of phone's screen
    val screenPixelDensity = LocalContext.current.resources.displayMetrics.density
    val dpValue = Resources.getSystem().displayMetrics.widthPixels / screenPixelDensity
    val cardSize = dpValue * 0.8

    // display chart
    if (dataDisplay != null && dataDisplay!!.isNotEmpty()) {
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
                grid = LinePlot.Grid(Red, steps = 1),
            ),
            modifier = Modifier
                .width(cardSize.dp)
                .height(300.dp)
                .onSizeChanged { },
            )
    } else {
        Text(
            "No data for this day",
            fontFamily = medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
        )
    }
}
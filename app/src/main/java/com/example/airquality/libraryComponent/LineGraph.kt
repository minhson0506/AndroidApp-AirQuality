package com.example.airquality.libraryComponent

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.airquality.MainActivity
import com.example.airquality.services.DataViewModel
import com.example.airquality.services.room.SensorModel
import com.example.airquality.ui.theme.Red
import com.example.airquality.ui.theme.Yellow
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot

@Composable
fun LineGraph(model: DataViewModel, date: String, indicator: String) {

    val data: List<SensorModel>? by model.getDataInDate("$date%").observeAsState()
    var dataDisplay by remember { mutableStateOf<List<Entry>>(listOf()) }


/*    var dataDisplay = data?.map {
        it.getValue(indicator = indicator.lowercase())?.let { it1 ->
            DataPoint(
                x = ((it.time?.split(",")?.get(1)?.trim()?.split(":")?.get(0)?.toFloat()
                    ?: 0f) + ((it.time?.split(",")?.get(1)?.trim()?.split(":")?.get(1)
                    ?.toDouble())?.div(60)!!) + ((it.time.split(",")[1].trim().split(":")[2]
                    .toDouble()).div(60))).toFloat(),
                y = it1.toFloat()
            )
        }
    }?.sortedBy { it?.x }*/

    // indicator
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

    /*val value = indicatorArray?.map {
        it?.value?.toInt() ?: 0
    }?.sortedDescending()
    Log.d(MainActivity.tag, "LineGraph indicators: $indicatorArray")*/

    // get time
    val time =
        indicatorArray?.map { it?.time?.split(",")?.get(1)?.trim()?.split(":")?.get(0) }?.toSet()
    Log.d(MainActivity.tag, "LineGraph time set: $time")

    // more than 8 hours a day
    if ((time?.size ?: 0) >= 8) {
        /*val listData = mutableListOf<DataPoint>()
        time?.map {
            indicatorArray.find { item ->
                item?.time?.split(",")?.get(1)?.trim()?.split(":")?.get(0) == it
            }
        }?.forEach {
            if (it != null) {
                Log.d(MainActivity.tag, "LineGraph indicator by time: value ${it.value} time ${it.time}")
            }
            it?.value?.toFloat()
                ?.let { it1 -> DataPoint(x = it.time.split(",")[1].trim().split(":")[0].toFloat(), y = it1) }
                ?.let { it2 -> listData.add(it2) }
        }
        dataDisplay = listData*/
        val listData = mutableListOf<Entry>()
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
                ?.let { it1 -> Entry(it.time.split(",")[1].trim().split(":")[0].toFloat(), it1) }
                ?.let { it2 -> listData.add(it2) }
        }
        if (dataDisplay != listData) {
            Log.d(MainActivity.tag, "LineGraph: update")
            dataDisplay = listData
        }

    } else if ((time?.size ?: 0) >= 3) {
        val listData = mutableListOf<Entry>()
        time?.map {
            indicatorArray.filter { item ->
                item?.time?.split(",")?.get(1)?.trim()?.split(":")?.get(0) == it
            }
        }?.forEach {
            val length = it.size
            it[0]?.value?.toFloat()
                ?.let { it1 ->
                    it[0]?.time?.split(",")?.get(1)?.trim()?.split(":")?.get(0)
                        ?.let { it2 -> Entry(it2.toFloat(), it1) }
                }
                ?.let { it2 -> listData.add(it2) }
            if (length > 1) if (length < 3) {
                it[1]?.value?.toFloat()
                    ?.let { it1 ->
                        it[1]?.time?.split(",")?.get(1)?.trim()?.split(":")?.get(0)
                            ?.let { it2 -> Entry(it2.toFloat(), it1) }
                    }
                    ?.let { it2 -> listData.add(it2) }
            } else {
                it[length / 2]?.value?.toFloat()
                    ?.let { it1 ->
                        it[length / 2]?.time?.split(",")?.get(1)?.trim()?.split(":")?.get(0)
                            ?.let { it2 -> Entry(it2.toFloat(), it1) }
                    }
                    ?.let { it2 -> listData.add(it2) }
            }

        }
        if (dataDisplay != listData) {
            Log.d(MainActivity.tag, "LineGraph: update")
            dataDisplay = listData
        }

    }


    dataDisplay.forEach {
        Log.d(
            MainActivity.tag,
            "LineGraph: data for displaying x = ${it?.x} y = ${it?.y}"
        )
    }
    //Log.d(MainActivity.tag, "LineGraph: $dataDisplay")

    // get size of phone's screen
    /*val screenPixelDensity = LocalContext.current.resources.displayMetrics.density
    val dpValue = Resources.getSystem().displayMetrics.widthPixels / screenPixelDensity
    val cardSize = dpValue * 0.8*/

    if (dataDisplay != null && dataDisplay!!.isNotEmpty()) {
        /*  LineGraph(
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
                  .height(300.dp).onSizeChanged {  },
              onSelection = { xLine, points ->
                  // Do whatever you want here

              }

          )*/
        Graph(points = dataDisplay)
    }
}

internal class DataIndicator(val value: Double, val time: String)

// Graph to display Heart rate
@Composable
fun Graph(points: List<Entry>) {
    // get size of phone's screen
    val screenPixelDensity = LocalContext.current.resources.displayMetrics.density
    val dpValue = Resources.getSystem().displayMetrics.heightPixels / screenPixelDensity

    // draw the graph with data
    AndroidView(
        modifier = Modifier
            .padding(start = 30.dp, end = 30.dp)
            .fillMaxSize()
            .height(dpValue.dp),
        factory = { context: Context ->
            // init map with line chart
            val view = LineChart(context)
            view.legend.isEnabled = false
            val data = LineData(LineDataSet(points, "BPM"))
            val desc = Description()
            desc.text = ""
            // set color of data in graph
            data.setValueTextColor(ColorTemplate.LIBERTY_COLORS[0])
            view.xAxis.textColor = 0xffffff
            view.legend.textColor = ColorTemplate.LIBERTY_COLORS[0]
            desc.textColor = 0xffffff
            view.axisLeft.textColor = ColorTemplate.LIBERTY_COLORS[0]
            view.axisRight.textColor = ColorTemplate.LIBERTY_COLORS[0]
            view.description = desc
            view.data = data
            view // return the view
        },
        update = { view ->
            // Update the view
            view.invalidate()
        }
    )
}

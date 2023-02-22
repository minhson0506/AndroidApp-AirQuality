package com.example.airquality.components

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airquality.MainActivity
import com.example.airquality.R
import com.example.airquality.libraryComponent.DropDownComp
import com.example.airquality.libraryComponent.LineGraph
import com.example.airquality.services.DataViewModel
import com.example.airquality.ui.theme.*
import com.mabn.calendarlibrary.ExpandableCalendar
import com.mabn.calendarlibrary.core.calendarDefaultTheme
import com.madrapps.plot.line.DataPoint
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChartPage(model: DataViewModel) {
    // get size of phone's screen
    val screenPixelDensity = LocalContext.current.resources.displayMetrics.density
    val dpValue = Resources.getSystem().displayMetrics.widthPixels / screenPixelDensity
    val cardSize = dpValue * 0.9


    val today = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)

    Log.d(MainActivity.tag, "ChartPage: today $today")
    var date: String by remember { mutableStateOf(today) }

    val indicator: String by model.indicator.observeAsState("PM10")

    val imageIndicator: Int by model.imageIndicator.observeAsState(R.drawable.wind)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue)
    ) {
        ExpandableCalendar(theme = calendarDefaultTheme.copy(
            dayShape = CircleShape,
            dayBackgroundColor = Color.White,
            selectedDayBackgroundColor = Blue,
            weekDaysTextColor = DarkGray,
            dayValueTextColor = Black,
            selectedDayValueTextColor = White,
            backgroundColor = LightBlue
        ), onDayClick = {
            Log.d(MainActivity.tag, "ChartPage: $it")
            date = it.toString()
        })

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp), horizontalAlignment = CenterHorizontally
        ) {
            DropDownComp(model = model)
            Card(
                modifier = Modifier
                    .width(cardSize.dp)
                    .padding(top = 20.dp)
                    .align(CenterHorizontally)
            ) {
                Column() {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 20.dp, start = 10.dp, top = 10.dp)
                    ) {
                        Image(
                            painterResource(id = imageIndicator),
                            contentDescription = "",
                            modifier = Modifier
                                .size(30.dp)
                                .background(Green)
                                .padding(5.dp),
                            colorFilter = ColorFilter.tint(color = White)
                        )
                        Text(
                            text = indicator.capitalize(Locale.ROOT),
                            color = Black,
                            fontSize = 20.sp,
                            fontFamily = bold,
                            modifier = Modifier.padding(top = 15.dp, start = 5.dp)
                        )
                    }
                    LineGraph(model = model, date = date, indicator = indicator)
                }
            }

        }
    }
}

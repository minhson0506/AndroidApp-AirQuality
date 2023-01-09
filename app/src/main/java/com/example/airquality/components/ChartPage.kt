package com.example.airquality.components

import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airquality.R
import com.example.airquality.libraryComponent.DropDownComp
import com.example.airquality.libraryComponent.SampleLineGraph
import com.example.airquality.ui.theme.*
import com.himanshoe.kalendar.Kalendar
import com.himanshoe.kalendar.color.KalendarThemeColor
import com.himanshoe.kalendar.model.KalendarType
import com.madrapps.plot.line.DataPoint

@Composable
fun ChartPage() {
    // get size of phone's screen
    val screenPixelDensity = LocalContext.current.resources.displayMetrics.density
    val dpValue = Resources.getSystem().displayMetrics.widthPixels / screenPixelDensity
    val cardSize = dpValue * 0.9

    val list = listOf(listOf(DataPoint(5F, 200F), DataPoint(10F, 300F), DataPoint(15F, 400F),  DataPoint(20F, 400F)))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue)
    ) {
        Kalendar(kalendarType = KalendarType.Oceanic, kalendarThemeColor = KalendarThemeColor(White, Blue, Black))
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp), horizontalAlignment = CenterHorizontally) {
            DropDownComp()
            Card(modifier = Modifier
                .width(cardSize.dp)
                .padding(top = 20.dp).align(CenterHorizontally)) {
                Column() {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 20.dp, start = 10.dp, top = 10.dp)) {
                        Image(
                            painterResource(id = R.drawable.wind),
                            contentDescription = "",
                            modifier = Modifier
                                .size(30.dp)
                                .background(Green)
                                .padding(5.dp),
                            colorFilter = ColorFilter.tint(color = White)
                        )
                        Text(
                            text = "PM2.5",
                            color = Black,
                            fontSize = 20.sp,
                            fontFamily = bold,
                            modifier = Modifier.padding(top = 15.dp, start = 5.dp)
                        )
                    }
                    SampleLineGraph(lines = list)
                }
            }

        }
    }

}

@Preview(showBackground = true)
@Composable
fun ChartPreview() {
    AirQualityTheme {
        ChartPage()
    }
}
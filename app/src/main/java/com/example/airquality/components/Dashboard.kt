package com.example.airquality.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.example.airquality.R
import com.example.airquality.libraryComponent.Headline
import com.example.airquality.libraryComponent.NumberText
import com.example.airquality.libraryComponent.UnitText
import com.example.airquality.ui.theme.*
import org.json.JSONArray


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Dashboard() {
    val array = listOf(
        Triple(Pair("Pm10", R.drawable.wind), 0.6, "µg/m3"),
        Triple(Pair("Pm2.5", R.drawable.wind), 1.85, "µg/m3"),
        Triple(
            Pair(
                "CO2", R.drawable.co2
            ),
            674.0,
            "ppm"
        ),
        Triple(Pair("Humidity", R.drawable.humidity), 12.72, "RH"),
        Triple(Pair("Light", R.drawable.light), 310.76, "lux"),
        Triple(
            Pair("Noise", R.drawable.sound),
            -60.97,
            "dB"
        ),
        Triple(Pair("Pressure", R.drawable.pressure), 1017.14, "hPa"),
        Triple(
            Pair(
                "Temp", R.drawable.temp
            ),
            42.65,
            "°C"
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column() {
                Headline(text = "ISD420")
                Text("KMC 752", fontFamily = medium, fontSize = 18.sp, color = DarkGray)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painterResource(id = R.drawable.location),
                        contentDescription = "",
                        modifier = Modifier.padding(end = 5.dp)
                            .size(15.dp),
                        colorFilter = ColorFilter.tint(color = Red)
                    )
                    Text(text = "Location", fontFamily = bold, fontSize = 18.sp, color = Black)

                }

                Text("Weather", fontFamily = medium, fontSize = 18.sp, color = DarkGray)
            }

        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
        ) {
            items(array) {
                Card(
                    modifier = Modifier.padding(
                        all = 10.dp
                    ),
                    backgroundColor = White,
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painterResource(id = it.first.second),
                                contentDescription = "",
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(Green)
                                    .padding(5.dp),
                                colorFilter = ColorFilter.tint(color = White)
                            )
                            Text(
                                text = it.first.first,
                                color = Black,
                                fontSize = 20.sp,
                                fontFamily = bold,
                                modifier = Modifier.padding(top = 15.dp, start = 5.dp)
                            )
                        }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            NumberText(text = it.second.toString())
                            UnitText(text = it.third)
                        }


                    }
                }


            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashBoardPreview() {
    AirQualityTheme {
        Dashboard()
    }
}
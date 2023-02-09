package com.example.airquality.components

import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.airquality.R
import com.example.airquality.libraryComponent.SampleSlider
import com.example.airquality.libraryComponent.TextTitle
import com.example.airquality.ui.theme.AirQualityTheme
import com.example.airquality.ui.theme.LightBlue
import com.example.airquality.ui.theme.LightGray
import com.example.airquality.ui.theme.bold


@Composable
fun Settings() {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var checkedState by remember { mutableStateOf(true) }

    // get size of phone's screen
    val screenPixelDensity = LocalContext.current.resources.displayMetrics.density
    val dpValue = Resources.getSystem().displayMetrics.widthPixels / screenPixelDensity
    val cardSize = dpValue * 0.9

    val data = listOf(
        Triple(Pair("Pm10", 0.01f), Pair(0.4f, 0.8f), Pair(0.2f, 1.0f)),
        Triple(Pair("Pm2.5", 0.01f), Pair(0.5f, 1.8f), Pair(0.2f, 2.0f)),
        Triple(Pair("Pm1", 0.01f), Pair(0.4f, 0.8f), Pair(0.2f, 1.0f)),
        Triple(Pair("Pm4", 0.01f), Pair(0.4f, 0.8f), Pair(0.2f, 1.0f)),
        Triple(Pair("CO2", 10f), Pair(200f, 650f), Pair(100f, 800f)),
        Triple(Pair("Humidity", 1f), Pair(2f, 15f), Pair(1f, 30f)),
        Triple(Pair("Light", 10f), Pair(20f, 40f), Pair(10f, 60f)),
        Triple(Pair("Noise", 10f), Pair(30f, 120f), Pair(10f, 150f)),
        Triple(Pair("Pressure", 10f), Pair(950f, 1050f), Pair(900f, 1100f)),
        Triple(Pair("Temperature", 1f), Pair(18f, 30f), Pair(10f, 55f))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .width(cardSize.dp)
                .padding(top = 20.dp)
        ) {
            Column(modifier = Modifier.padding(top = 20.dp)) {
                TextTitle(id = R.string.change)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 20.dp)
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .padding(start = 10.dp)
                            .background(LightGray)
                            .width((cardSize * 0.8).dp)
                    ) {
                        Text(
                            text = "ISD420",
                            fontFamily = bold,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                        TextField(
                            value = text,
                            onValueChange = { newText ->
                                text = newText
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = LightGray,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                        )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Image(
                            painterResource(id = R.drawable.checkbox),
                            contentDescription = "Favorite",
                            modifier = Modifier.size(50.dp)
                        )
                    }

                }

            }
        }
        Card(
            modifier = Modifier.padding(bottom = 20.dp)
                .width(cardSize.dp)
                .padding(top = 20.dp)
        ) {
            Column() {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextTitle(id = R.string.alert)
                    Switch(
                        checked = checkedState,
                        onCheckedChange = { checkedState = it }
                    )
                }
                if (checkedState) {
                    Column( modifier = Modifier.verticalScroll(rememberScrollState())) {
                        for (item in data) {
                            SampleSlider(
                                text = item.first.first,
                                minValue = item.second.first,
                                maxValue = item.second.second,
                                min = item.third.first,
                                max = item.third.second,
                                step = item.first.second
                            )
                        }
                    }
                }

            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingPreview() {
    AirQualityTheme {
        Settings()
    }
}
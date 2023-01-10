package com.example.airquality.libraryComponent

import android.content.res.Resources
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airquality.ui.theme.*
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot

// TextView
@Composable
fun TextTitle(
    id: Int,
    size: Int = 20,
    color: Color = Color.Black,
    font: FontFamily = bold,
) {
    Text(
        text = stringResource(id = id),
        color = color,
        fontFamily = font,
        fontSize = size.sp,
        modifier = Modifier.padding(start = 10.dp)
    )
}

@Composable
fun Headline(
    text: String,
    size: Int = 28,
    color: Color = Black,
    font: FontFamily = bold,
) {
    Text(
        text = text,
        color = color,
        fontFamily = font,
        fontSize = size.sp
    )
}

@Composable
fun NormalText(
    text: String,
    size: Int = 18,
    color: Color = Color.Black,
    font: FontFamily = medium,
) {
    Text(
        text = text,
        color = color,
        fontFamily = font,
        fontSize = size.sp
    )
}

@Composable
fun BoldText(
    id: Int,
    size: Int = 18,
    color: Color = Color.White,
    font: FontFamily = bold,
) {
    Text(
        text = stringResource(id = id),
        color = color,
        fontFamily = font,
        fontSize = size.sp
    )
}

@Composable
fun UnitText(
    text: String,
    size: Int = 12,
    color: Color = Color.Black,
    font: FontFamily = medium,
) {
    Text(
        text = text,
        color = color,
        fontFamily = font,
        fontSize = size.sp,
        modifier = Modifier.padding(top = 5.dp)
    )
}

@Composable
fun NumberText(
    text: String,
    size: Int = 32,
    color: Color = Color.Black,
    font: FontFamily = bold,
) {
    Text(
        text = text,
        color = color,
        fontFamily = font,
        fontSize = size.sp,
        modifier = Modifier.padding(top = 15.dp)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DropDownComp() {

    val contextForToast = LocalContext.current.applicationContext

    val listItems = arrayOf("Favorites", "Options", "Settings", "Share")

    var selectedItem by remember {
        mutableStateOf(listItems[0])
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    // the box
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {

        // text field
        TextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(backgroundColor = Color.White)
        )

        // menu
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listItems.forEach { selectedOption ->
                // menu item
                DropdownMenuItem(onClick = {
                    selectedItem = selectedOption
                    Toast.makeText(contextForToast, selectedOption, Toast.LENGTH_SHORT).show()
                    expanded = false
                }) {
                    Text(text = selectedOption)
                }
            }
        }
    }
}

@Composable
fun SampleLineGraph(lines: List<List<DataPoint>>) {
    // get size of phone's screen
    val screenPixelDensity = LocalContext.current.resources.displayMetrics.density
    val dpValue = Resources.getSystem().displayMetrics.widthPixels / screenPixelDensity
    val cardSize = dpValue * 0.8

    LineGraph(
        plot = LinePlot(
            listOf(
                LinePlot.Line(
                    lines[0],
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SampleSlider(text: String, minValue: Float, maxValue: Float, min: Float, max: Float) {
    var range by remember { mutableStateOf(min..max) }

    Column(modifier = Modifier.padding(10.dp)) {
        NormalText(text = text)
        RangeSlider(
            values = range,
            onValueChange = { range = it },
            valueRange = minValue..maxValue,
            colors = SliderDefaults.colors(
                thumbColor = Blue,
                activeTrackColor = Gray,
                inactiveTrackColor = LightGray,
            ),
        )
    }
}

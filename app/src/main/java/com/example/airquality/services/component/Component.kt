package com.example.airquality.services.component

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airquality.MainActivity
import com.example.airquality.services.*
import com.example.airquality.ui.theme.*
import kotlin.math.roundToInt

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
fun DropDownComp(model: DataViewModel) {

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
                    model.indicator.postValue(selectedOption)
                    model.imageIndicator.postValue(itemImages[listItems.indexOf(selectedOption)])
                    expanded = false
                }) {
                    Text(text = selectedOption)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SampleSlider(id: Int, text: String, min: Float, max: Float, step: Float, model: DataViewModel) {
    val minValue by model.minArray.observeAsState(minValueInit)
    val maxValue by model.maxArray.observeAsState(maxValueInit)

    //var range by remember { mutableStateOf(min..max) }
    var range = minValue[id]..maxValue[id]

    val stepDisplay = if (step > 0.1) 10 else 100

    Column(modifier = Modifier.padding(10.dp)) {
        NormalText(text = text)
        Text(text = ("Min: " + (range.start * stepDisplay).roundToInt() / stepDisplay.toFloat()) + " - Max: " + ((range.endInclusive * stepDisplay).roundToInt() / stepDisplay.toFloat()).toString())
        RangeSlider(
            values = range,
            onValueChange = {
                range = it
                val listMin = mutableListOf<Float>()
                minValue.forEach { item -> listMin.add(item) }
                val listMax = mutableListOf<Float>()
                maxValue.forEach { item -> listMax.add(item) }
                listMin[id] = range.start
                listMax[id] = range.endInclusive
                model.minArray.postValue(listMin)
                model.maxArray.postValue(listMax)
            },
            valueRange = min..max,
            colors = SliderDefaults.colors(
                thumbColor = Blue,
                activeTrackColor = Gray,
                inactiveTrackColor = LightGray,
            ),
        )
    }
}

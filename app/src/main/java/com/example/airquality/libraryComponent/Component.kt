package com.example.airquality.libraryComponent

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airquality.ui.theme.*

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
        fontSize = size.sp
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
    id: Int,
    size: Int = 20,
    color: Color = Color.White,
    font: FontFamily = medium,
) {
    Text(
        text = stringResource(id = id),
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